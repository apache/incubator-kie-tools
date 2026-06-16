# Contributing to Apache KIE Tools

Thank you for your interest in contributing to Apache KIE Tools! This guide outlines expectations for contributors opening pull requests to this monorepo.

## Quick Start

For detailed setup, build, and testing instructions, see:

- **[README.md](README.md)** - Build from source, bootstrap, and application-specific development
- **[repo/NIX_DEV_ENV.md](repo/NIX_DEV_ENV.md)** - Nix-based development environment (recommended)
- **[repo/MANUAL.md](repo/MANUAL.md)** - Monorepo structure, package conventions, and advanced topics

### Prerequisites

This is a **pnpm monorepo** requiring Node.js, pnpm, and additional tools depending on your contribution scope (Maven, Java, Go, Helm). See [README.md](README.md#step-0-install-the-necessary-tools) for the complete list and installation links.

**Recommended:** Use the provided [Nix Devbox configuration](repo/NIX_DEV_ENV.md) to automatically set up all required tools.

### Basic Workflow

1. **Fork and clone** the repository
2. **Set up your development environment** with `devbox shell`
3. **Bootstrap**: `pnpm bootstrap` (or `pnpm bootstrap -F <package>...` for specific packages)
4. **Build for development**: `pnpm -r build:dev` (or `pnpm -F <package>... build:dev`)
5. **Make your changes** following the guidelines below
6. **Build for production**: `pnpm -F <package>... build:prod` before submitting
7. **Submit a pull request** linked to a GitHub issue

For package-specific workflows and monorepo conventions, see [README.md](README.md#build-from-source) and [repo/MANUAL.md](repo/MANUAL.md).

---

## Pull Request Guidelines

### Before Submitting

**Required checklist:**

- [ ] Link your PR to an existing GitHub issue
- [ ] Run `pnpm bootstrap` to ensure code formatting and that the pnpm lockfile is updated
- [ ] Run `KIE_TOOLS_BUILD__buildContainerImages=true KIE_TOOLS_BUILD__runEndToEndTests=true pnpm -F <affected_package_1>... -F <affected_package_2>... build:prod` to verify your changes
- [ ] Ensure all tests pass (unit and E2E where applicable)
- [ ] Add Apache License 2.0 headers to new source files
- [ ] Update package README or documentation if behavior changes
- [ ] Verify no unintended files are modified (`git diff`)

### PR Scope and Expectations

- **Keep PRs focused and atomic** - one concern or feature per PR
- **Respect package boundaries** - understand workspace dependencies before making changes
- **Include relevant tests** - add or update tests for new features and bug fixes
- **Document breaking changes** - clearly describe any breaking changes in the PR description
- **Respond to review feedback promptly**

### Pull Request Titles

Use a consistent title format to help maintainers and contributors quickly understand the scope and purpose of your PR:

**Pattern:** `<issue-reference>: [<component>] <descriptive action>`

- **Issue reference:**
  - `kie-issues#xxxx:` for changes linked to a GitHub issue
  - `NO-ISSUE:` for maintenance, infrastructure, or minor changes without an associated issue
  - Security fixes should be discrete. Include the names of the dependencies that are being updated, but don't link or mention CVEs. Follow the the [Apache Security guidelines](https://www.apache.org/security/committers.html)
- **Component scope (optional):** Add a component identifier after the issue reference (e.g., `[BPMN Editor]`, `[DMN Editor]`, `[Extended Services]`)
- **Descriptive action:** Use imperative/descriptive wording, be specific about the affected area or outcome, keep it concise, and avoid trailing punctuation.

**Examples:**

- `kie-issues#1234: [BPMN Editor] Fix node alignment in process diagrams`
- `kie-issues#5678: Add support for DMN 1.4 specification`
- `NO-ISSUE: Update CI workflow to use Node.js 25`

---

## Code Quality Standards

### Formatting and Linting

- **Prettier** handles code formatting automatically via pre-commit hook
- **ESLint** validates TypeScript/JavaScript code during `build:prod` (or manually by running `pnpm lint`)
- Run `pnpm format:check` in the root of the repository to check formatting manually
- See [package.json](package.json) for available root-level scripts

### License Headers

All new source files **must** include Apache License 2.0 headers. CI validates this using Apache RAT. See existing files for the correct header format.

### Testing

- **Unit tests**: Run as part of `build:prod` using Jest
- **E2E tests**: Use Playwright, located in `tests-e2e/` directories
- **Test policy**: Add tests for new features and bug fixes; maintain or improve coverage

For E2E test setup, environment variables, and containerized testing, see:

- [README.md](README.md#build-from-source) - E2E environment variables
- [packages/playwright-base/README.md](packages/playwright-base/README.md) - Playwright configuration and containerized testing
- Individual package READMEs for package-specific test instructions

---

## CI Validation

CI is the authoritative validation for all PRs. It checks:

- Code formatting (Prettier)
- License headers (Apache RAT)
- Linting (ESLint)
- Unit tests (Jest)
- E2E tests (Playwright, Linux containers)
- Cross-platform builds (Ubuntu, macOS, Windows)

**If CI fails:** Reproduce the failure locally using the documented scripts before pushing fixes. The build is parameterized by environment variables - see the `pnpm bootstrap` output for the complete list.

---

## Documentation and Changelog

- **Update package READMEs** when adding features or changing behavior
- **Include usage examples** for new APIs
- **Document breaking changes** clearly in both PR description and relevant documentation
- **Consider changelog updates** for user-facing changes (breaking changes, new features, significant bug fixes)

---

## Getting Help

- **Repository Manual**: [repo/MANUAL.md](repo/MANUAL.md) - comprehensive monorepo documentation
- **GitHub Issues**: [apache/incubator-kie-issues/issues](https://github.com/apache/incubator-kie-issues/issues)
- **Apache KIE Website**: [kie.apache.org](http://kie.apache.org)
- **Apache KIE Zulip chat**: [kie.zulipchat.com](https://kie.zulipchat.com)

When reporting issues:

1. Search existing issues first
2. Provide clear reproduction steps
3. Include environment details (OS, Node version, etc.)
4. Share relevant error messages and logs

---

## License

By contributing to Apache KIE Tools, you agree that your contributions will be licensed under the [Apache License 2.0](./LICENSE).

---

Thank you for contributing to Apache KIE Tools! 🎉
