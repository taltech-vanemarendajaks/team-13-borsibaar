# TEAM.md

## Team name

Mingi Kapapatrull

## Team members and GitHub usernames

- Karoliina Rebane - kreban
- Mihkel Koks - MiKoks
- Lauri Hanstin - JaniHans
- Silver Slinko - silverxd
- Anna Borisova - Anchovy-est
- Kairo Korgend - KairoKorgend

## Short description of your team workflow

### Branches

We use a three-tier branching strategy:

- **`main`** - Production-ready code. Only contains stable, tested features that are ready for deployment.
- **`dev`** - Testing and integration branch. Used to test features before they go to production. May contain experimental or work-in-progress code.
- **`feature/*`** or **`bugfix/*`** - Short-lived branches for individual features or bug fixes. Always created from `main` (never from `dev`).

**Branch naming convention:**

- Features: `feature/<issue-number>-<brief-description>` (e.g., `feature/42-user-authentication`)
- Bug fixes: `bugfix/<issue-number>-<brief-description>` (e.g., `bugfix/15-fix-login-redirect`)
- Hotfixes: `hotfix/<issue-number>-<brief-description>` (for urgent production fixes)

### Reviews

All changes to `dev` require code review:

1. When a feature/bugfix is complete, create a PR from the feature branch to `dev`
2. Assign at least one team member as a reviewer
3. Address all review comments and requested changes
4. Once approved, merge to `dev` for testing

If issues are found after merging to `dev`:

- Implement the fixes
- Create a new PR to `dev` and repeat the review process

### Merges

Our dual-PR workflow ensures only working features reach production:

1. **Feature → Dev:** Create a PR from the feature branch to `dev` with assigned reviewers. Once approved and merged, test the feature in the development environment.

2. **Feature → Main:** After verifying the feature works correctly in `dev`, create a separate PR from the **same feature branch** to `main`. This allows us to merge only proven, working features to production while keeping experimental or broken code isolated in `dev`.

**Handling merge conflicts:**

The PR author is responsible for resolving all merge conflicts:

- If conflicts occur during the PR to `dev` or `main`, the author must:
  1. Check out the target branch (`dev` or `main`) locally and pull the latest changes
  2. Start a merge from the feature branch into the target branch locally (e.g., `git merge feature/123-my-feature`)
  3. Resolve all conflicts locally
  4. Test that the code still works after conflict resolution
  5. Complete the merge and push to the target branch, or push the resolved feature branch if using PR merge strategies

This approach keeps `main` stable and production-ready while allowing `dev` to serve as a true testing ground where features can be validated before promotion to production.

## Merge Strategies Used

### 1. Squash Merge
- **Used in**: PR #1
- **Why**: Consolidate initial setup commits into one clean commit

### 2. Regular Merge Commit
- **Used in**: PR #2, #3, #5
- **Why**: Preserve full commit history for ongoing feature development

### 3. Rebase-Based Merge
- **Used in**: PR #4 (to main)
- **Why**: Keep main branch linear and clean for production

## Problems Encountered

### Merge Conflict in TEAM.md
- **Issue**: feature/1-team13-lauri conflicted with dev branch (Kairo and Lauri both edited TEAM.md)
- **Resolution**: Lauri resolved locally using `git merge`, combined both sets of changes
- **Lesson**: Coordinate on file edits; communicate about parallel work

## Notes for contributors

- Always pull latest changes before creating a feature branch
- Use clear commit messages
- Resolve conflicts locally before pushing
- Assign reviewers to your PRs
- Don't approve your own code
