const findWorkspacePackages = require("@pnpm/find-workspace-packages").default;
const execSync = require("child_process").execSync;

const inputVersion = process.argv[2];
const execOpts = { stdio: "inherit" };
const workspaceDir = ".";

console.info("Checking version of packages against input version " + inputVersion);

async function main() {
  const packages = await findWorkspacePackages(workspaceDir);
  packages.forEach((p) => {
    if (p.manifest.version != inputVersion) {
      throw new Error("version mis-match for " + p.dir);
    }
  });
  execSync(`pnpm run --if-present -r verify-version ` + inputVersion, execOpts);
  console.info("All packages are up to date with version " + inputVersion);
}
main();
