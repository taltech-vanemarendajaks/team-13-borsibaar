# Borsibaar Deployment Documentation

This document describes the deployment process for the Borsibaar application to our server at `193.40.157.30`.

## Architecture

```
Browser --> kairo-borsibaar.zapto.org
  --> Host Nginx (port 80/443 with SSL)
    --> /oauth2/, /login/oauth2/, /auth/  --> backend (127.0.0.1:8081)
    --> everything else                   --> frontend (127.0.0.1:3001)

Docker containers (kairo-network):
  kairo-frontend (3001:3000) --> kairo-backend (8081:8080) --> kairo-db (5432)
```

- **Host Nginx** handles SSL termination and routes requests to the correct container
- **Docker containers** communicate internally via a Docker network
- **Docker Hub** (`kairokrgend/borsibaar-backend:kairo`, `kairokrgend/borsibaar-frontend:kairo`) is used as the image registry
- **GitHub Actions** automatically builds, pushes, and deploys on push to the `feature/deployment-kairo` branch

---

## Step 1: Server Setup

SSHed into the server and verified Docker and Nginx were installed:

```bash
ssh ubuntu@193.40.157.30
docker --version          # Docker version 28.2.2
sudo nginx -t             # nginx config syntax ok
```

Installed Docker Compose v2:
```bash
sudo apt-get update
sudo apt-get install -y docker-compose-v2
```

---

## Step 2: Manual Deployment

### Modified the Backend Dockerfile

Changed `backend/Dockerfile` to use a multi-stage build so the Java code compiles inside the container (no need for Java 21 locally):

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Built and pushed Docker images

Images were built with `--platform linux/amd64` because the server runs AMD64 and we're building on Apple Silicon:

```bash
docker build --platform linux/amd64 \
  -t kairokrgend/borsibaar-backend:kairo \
  -f backend/Dockerfile backend/

docker build --platform linux/amd64 \
  -t kairokrgend/borsibaar-frontend:kairo \
  --build-arg NEXT_PUBLIC_BACKEND_URL=https://kairo-borsibaar.zapto.org \
  -f frontend/Dockerfile .

docker push kairokrgend/borsibaar-backend:kairo
docker push kairokrgend/borsibaar-frontend:kairo
```

### Created a server-side Docker Compose file

Created `docker-compose.kairo.yaml` which differs from the repo's `docker-compose.prod.yaml`:
- Uses `image:` (pulls from Docker Hub) instead of `build:`
- No nginx container (host nginx handles routing instead)
- Unique container names (`kairo-db`, `kairo-backend`, `kairo-frontend`)
- Frontend exposed on port 3001, backend on port 8081

### Deployed to the server

```bash
ssh ubuntu@193.40.157.30 "mkdir -p ~/kairo"
scp docker-compose.kairo.yaml ubuntu@193.40.157.30:~/kairo/docker-compose.yaml
scp .env.kairo ubuntu@193.40.157.30:~/kairo/.env
ssh ubuntu@193.40.157.30 "cd ~/kairo && docker compose pull && docker compose up -d"
```

### Configured host Nginx

Created `/etc/nginx/sites-available/kairo` on the server to route `kairo-borsibaar.zapto.org` to the containers:
- `/oauth2/`, `/login/oauth2/`, `/auth/`, `/actuator/` → backend (127.0.0.1:8081)
- Everything else → frontend (127.0.0.1:3001)

```bash
sudo ln -sf /etc/nginx/sites-available/kairo /etc/nginx/sites-enabled/kairo
sudo nginx -t
sudo systemctl reload nginx
```

### Fixed cookie issue

The backend was setting `cookie.setSecure(true)` which prevented the JWT cookie from working over HTTP. Made it configurable via `APP_COOKIE_SECURE` environment variable in `AuthController.java`, so it can be set to `false` for HTTP or `true` for HTTPS.

---

## Step 3: Domain

Registered a free hostname on [noip.com](https://noip.com):
- Went to **DDNS & Remote Access** → **Create Hostname**
- Host: `kairo-borsibaar`, Domain: `zapto.org`, Type: A, IPv4: `193.40.157.30`

Updated Google OAuth credentials in [Google Cloud Console](https://console.cloud.google.com/apis/credentials):
- Authorized JavaScript origins: `https://kairo-borsibaar.zapto.org`
- Authorized redirect URIs: `https://kairo-borsibaar.zapto.org/login/oauth2/code/google`

Note: Google does not allow bare IP addresses for OAuth — a domain is required.

---

## Step 4: CI/CD

### Created a GitHub Actions workflow

Created `.github/workflows/deploy-kairo.yml` (separate from the existing `docker-image.yml`) that:
1. Triggers on push to `feature/deployment-kairo` branch
2. Logs into Docker Hub
3. Builds backend and frontend images for `linux/amd64`
4. Pushes images to Docker Hub
5. SSHes into the server
6. Writes the `.env` file from a GitHub secret
7. Pulls new images and restarts containers

### Added GitHub Secrets

Added to repo Settings → Secrets and variables → Actions:

| Secret | Description |
|--------|-------------|
| `SSH_USER` | Server SSH username (`ubuntu`) |
| `SERVER_IP` | Server IP address |
| `SSH_PRIVATE_KEY` | SSH private key for connecting to the server |
| `DOCKERHUB_USERNAME` | Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token (created at hub.docker.com/settings/security) |
| `ENV_PRODUCTION_FILE` | Full contents of the production .env file |

---

## Step 5: HTTPS

### Installed Certbot

```bash
ssh ubuntu@193.40.157.30
sudo apt update
sudo apt install certbot python3-certbot-nginx -y
```

### Got a Let's Encrypt certificate

```bash
sudo certbot --nginx -d kairo-borsibaar.zapto.org --non-interactive --agree-tos --email kairokorgend@gmail.com
```

Certbot automatically modified the nginx config to serve HTTPS and set up automatic certificate renewal.

### Updated configuration for HTTPS

- Changed all URLs in `.env` from `http://` to `https://`
- Set `APP_COOKIE_SECURE=true`
- Rebuilt and pushed the frontend image with the HTTPS URL
- Updated the `ENV_PRODUCTION_FILE` GitHub secret
- Updated the workflow's `NEXT_PUBLIC_BACKEND_URL` to `https://`
- Updated Google OAuth redirect URIs to `https://`

---

## Result

The application is live at **https://kairo-borsibaar.zapto.org** with:
- HTTPS with auto-renewing Let's Encrypt certificate
- Google OAuth login working
- CI/CD pipeline that auto-deploys on push to `feature/deployment-kairo`
- Docker images hosted on Docker Hub

## Useful Commands

```bash
# Check container status
ssh ubuntu@193.40.157.30 "docker ps"

# View logs
ssh ubuntu@193.40.157.30 "docker logs kairo-backend --tail 30"
ssh ubuntu@193.40.157.30 "docker logs kairo-frontend --tail 30"

# Restart containers
ssh ubuntu@193.40.157.30 "cd ~/kairo && docker compose restart"

# Stop everything
ssh ubuntu@193.40.157.30 "cd ~/kairo && docker compose down"

# Full redeploy
ssh ubuntu@193.40.157.30 "cd ~/kairo && docker compose pull && docker compose up -d"
```
