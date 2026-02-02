## Test Plan

A test plan document for Borsibaar application project.

### Testing objectives

- Verify that the system meets functional requirements (ordering, payments, registration);
- Ensure non-functional requirements are satisfied (performance, security, usability);
- Identify and document defects to reduce the risk of failures in production;
- Provide confidence to stakeholders that the system is stable and ready for deployment.

### Testing levels (e.g. unit, integration, system)

- Unit Testing is for individual components in which we can focus on specific entities and methods: 
    a) Organization change in correspondence with inventory
    b) Product listing and its relationship with organization
    c) Inventory and its relationship with Products and related main methods
    d) Transaction and its related methods regarding inventory, prices, availability and dynamic price change
    e) System Testing for the bar application to check if the entire application is working in a deployed environment in a real life bar setting scenario as intended. Who ? Bartenders
### Test scope
### Test approach

### Test environment
- The test environment consists of campus bar workstations and mobile devices used by bartenders, 
- a dedicated test backend with a separate database, API endpoints, authentication, app server
- network infrastructure. 
- 3rd party payment processing.
- The environment should be able to simulate as closely as possible real life integration. 
### Entry and exit criteria
### Roles and responsibilities


| ID   | Assumption                                                              | Impact if False                                |
| ---- | ----------------------------------------------------------------------- | ---------------------------------------------- |
| A001 | Team members have sufficient knowledge of Spring Boot and Next.js       | Development delays, code quality issues        |
| A002 | PostgreSQL database will handle expected load                           | Performance degradation, system downtime       |
| A003 | OAuth2 (Google) authentication service will remain available            | Users cannot log in                            |
| A004 | Docker and containerization infrastructure is reliable                  | Deployment failures                            |
| A005 | Team members are available throughout the project duration              | Schedule delays                                |
| A006 | Requirements are well-defined and stable                                | Scope creep, rework                            |
| A007 | Third-party libraries (Radix UI, D3.js, etc.) are maintained and secure | Security vulnerabilities, compatibility issues |
| A008 | Network connectivity between services is stable                         | System failures                                |

---

## Risk Analysis

### Risk Scoring Method

**Risk Score = Probability × Impact**

| Scale | Probability       | Impact                              |
| ----- | ----------------- | ----------------------------------- |
| 1     | Very Low (< 10%)  | Minimal effect                      |
| 2     | Low (10-25%)      | Minor delays or issues              |
| 3     | Medium (25-50%)   | Moderate impact on schedule/quality |
| 4     | High (50-75%)     | Significant impact                  |
| 5     | Very High (> 75%) | Critical, project-threatening       |

---

## Risk Register

### Technical Risks

| ID   | Risk Name                       | Description                                                                                  | Probability | Impact | Score | Category |
| ---- | ------------------------------- | -------------------------------------------------------------------------------------------- | ----------- | ------ | ----- | -------- |
| R001 | Database performance under load | System may not handle high concurrent users during peak events, causing slowdowns or crashes | 3           | 4      | 12    | Product  |
| R002 | Authentication service failure  | Google OAuth2 service unavailability prevents user login                                     | 2           | 5      | 10    | Product  |
| R003 | Data consistency issues         | Concurrent transactions may cause race conditions in inventory updates                       | 3           | 4      | 12    | Product  |
| R004 | Security vulnerabilities        | Potential security flaws in authentication, authorization, or data handling                  | 3           | 5      | 15    | Product  |
| R005 | API integration failures        | Backend-frontend communication issues causing functionality loss                             | 2           | 4      | 8     | Product  |
| R006 | Dynamic pricing logic errors    | Price calculation bugs leading to incorrect pricing                                          | 3           | 4      | 12    | Product  |
| R007 | Database migration failures     | Liquibase migrations failing during deployment                                               | 2           | 4      | 8     | Project  |
| R008 | Docker container issues         | Container orchestration problems in production                                               | 2           | 3      | 6     | Project  |

### Organizational Risks

| ID   | Risk Name                 | Description                                      | Probability | Impact | Score | Category |
| ---- | ------------------------- | ------------------------------------------------ | ----------- | ------ | ----- | -------- |
| R009 | Schedule delays           | Development tasks take longer than estimated     | 4           | 3      | 12    | Project  |
| R010 | Scope creep               | New requirements added during development        | 3           | 3      | 9     | Project  |
| R011 | Insufficient testing time | Rushed testing due to deadline pressure          | 3           | 4      | 12    | Project  |
| R012 | Environment setup issues  | Development environment differences causing bugs | 2           | 3      | 6     | Project  |

### People Risks

| ID   | Risk Name                  | Description                                          | Probability | Impact | Score | Category |
| ---- | -------------------------- | ---------------------------------------------------- | ----------- | ------ | ----- | -------- |
| R013 | Team member unavailability | Key team member becomes unavailable                  | 2           | 4      | 8     | Project  |
| R014 | Knowledge gaps             | Team lacks expertise in specific technologies        | 3           | 3      | 9     | Project  |
| R015 | Communication issues       | Miscommunication leading to incorrect implementation | 2           | 3      | 6     | Project  |
| R016 | Code review bottlenecks    | Slow code reviews delaying merges                    | 3           | 2      | 6     | Project  |

### Supplier/External Risks

| ID   | Risk Name                  | Description                                                  | Probability | Impact | Score | Category |
| ---- | -------------------------- | ------------------------------------------------------------ | ----------- | ------ | ----- | -------- |
| R017 | Third-party service outage | External services (Google OAuth, hosting) become unavailable | 2           | 4      | 8     | Product  |
| R018 | Library deprecation        | Critical dependencies become unsupported                     | 1           | 3      | 3     | Project  |
| R019 | SSL certificate expiration | HTTPS certificates expire causing service disruption         | 2           | 4      | 8     | Project  |

### Product Quality Risks

| ID   | Risk Name               | Description                                   | Probability | Impact | Score | Category |
| ---- | ----------------------- | --------------------------------------------- | ----------- | ------ | ----- | -------- |
| R020 | Poor user experience    | UI/UX issues affecting user adoption          | 3           | 3      | 9     | Product  |
| R021 | Performance issues      | Slow page loads or API responses              | 3           | 3      | 9     | Product  |
| R022 | Data integrity problems | Incorrect data being stored or displayed      | 2           | 5      | 10    | Product  |
| R023 | Missing error handling  | Unhandled errors causing poor user experience | 3           | 3      | 9     | Product  |

---

## Test Deliverables

### Test Process Overview

The testing process consists of the following activities:

1. **Test Planning** - Define test objectives and exit criteria
2. **Test Monitoring & Control** - Track progress and apply corrective actions
3. **Test Analysis** - Identify testable features and define test conditions
4. **Test Design** - Create test cases and test procedures
5. **Test Implementation** - Prepare test environment and test data
6. **Test Execution** - Run tests and compare results
7. **Test Completion** - Compile reports and archive test artifacts

---

### Deliverables by Test Phase

#### 1. Test Planning Deliverables

| Deliverable     | Description                                                        |
| --------------- | ------------------------------------------------------------------ |
| Test Plan       | Overall testing strategy, scope, approach, resources, and schedule |
| Test Schedule   | Timeline for test activities aligned with project milestones       |
| Resource Plan   | Allocation of team members to testing tasks                        |
| Risk Assessment | Testing-related risks and mitigation strategies                    |

#### 2. Test Analysis Deliverables

| Deliverable         | Description                                                        |
| ------------------- | ------------------------------------------------------------------ |
| Test Basis Analysis | Review of requirements, design documents, and risk analysis        |
| Test Conditions     | List of testable features and conditions derived from requirements |
| Traceability Matrix | Mapping between requirements and test conditions                   |

#### 3. Test Design Deliverables

| Deliverable                    | Description                                                         |
| ------------------------------ | ------------------------------------------------------------------- |
| Test Cases                     | Detailed test cases with preconditions, steps, and expected results |
| Test Data Requirements         | Specification of required test data                                 |
| Test Environment Specification | Hardware, software, and configuration requirements                  |

#### 4. Test Implementation Deliverables

| Deliverable            | Description                                                   |
| ---------------------- | ------------------------------------------------------------- |
| Test Procedures        | Step-by-step instructions for executing test cases            |
| Automated Test Scripts | Automated tests for backend (JUnit) and frontend              |
| Test Suites            | Organized collections of test cases grouped by feature/module |
| Test Data              | Prepared datasets for testing                                 |
| Test Environment       | Configured and verified test environment                      |

#### 5. Test Execution Deliverables

| Deliverable          | Description                                                     |
| -------------------- | --------------------------------------------------------------- |
| Test Execution Log   | Record of executed tests with pass/fail status                  |
| Defect Reports       | Documented bugs with severity, steps to reproduce, and evidence |
| Test Progress Report | Status of test execution against plan                           |

#### 6. Test Completion Deliverables

| Deliverable             | Description                                                 |
| ----------------------- | ----------------------------------------------------------- |
| Test Summary Report     | Final report with metrics, coverage, and quality assessment |
| Lessons Learned         | Documentation of insights for future projects               |
| Archived Test Artifacts | Preserved test cases, data, and environment for future use  |