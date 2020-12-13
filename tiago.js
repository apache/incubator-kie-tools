const { execSync, spawn } = require("child_process");
const { existsSync } = require("fs");

function packageJson(path) {
  return require(`${path}/package.json`);
}

function watchSrcDirectoryOfPackage(path) {
  const pathToWatch = `${path}/src`;

  if (!existsSync(pathToWatch)) {
    console.info(`Can't watch ${pathToWatch} because it doesn't exist`);
    return;
  }

  const dependentPackagePaths = execSync(
    `npx lerna exec "pwd" --scope ${packageJson(path).name} --include-dependents | grep -v "^lerna"`,
    { stdio: "pipe" }
  )
    .toString()
    .trim()
    .split("\n");

  const packagesToRebuild = dependentPackagePaths
    .filter(p => appDependencyPackagePaths.indexOf(p) !== -1)
    .map(p => packageJson(p).name);

  const scopeList = `${packagesToRebuild.map(p => `--scope ${p}`).join(" ")}`;
  const refreshCommand = `npx lerna exec '${refreshPackageCommand}' ${scopeList} --stream`;

  return spawn(`npx chokidar ${pathToWatch} -c "${refreshCommand}"`, [], { shell: true, stdio: "inherit" });
}

//TODO: Turn these into parameters
const appPackageName = "@kogito-tooling/online-editor";
const startAppCommand = "cd packages/online-editor && yarn start";
const refreshPackageCommand = "yarn build:fast";

//
// MAIN
//
// Finds dependency packages of `appPackageName`; and
// Filter out the `appPackageName`'s path.
const appDependencyPackagePaths = execSync(
  `npx lerna exec "pwd" --scope ${appPackageName} --include-dependencies | grep -v "^lerna"`,
  { stdio: "pipe" }
)
  .toString()
  .trim()
  .split("\n")
  .filter(path => packageJson(path).name !== appPackageName);

appDependencyPackagePaths.forEach(watchSrcDirectoryOfPackage);

// Spawns the webapp
spawn(startAppCommand, [], { shell: true, stdio: "inherit" });

// Holds the process running
setInterval(() => {}, 1 << 30);

//TODO: Prevent App from recompiling when all modules are not done rebuilding yet.
