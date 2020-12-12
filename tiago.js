const { execSync, spawn } = require("child_process");
const { existsSync } = require("fs");

// Parameters
const appPackageName = "@kogito-tooling/online-editor";
const startAppCommand = "cd packages/online-editor && yarn start";
const refreshPackageCommand = "yarn build:fast";

// Finds dependency packages of `appPackageName`.
const packagePaths = execSync(
  `npx lerna exec "pwd" --scope ${appPackageName} --include-dependencies | grep -v "^lerna"`,
  {
    stdio: "pipe"
  }
)
  .toString()
  .trim()
  .split("\n");

// Finds all packages.
const allPackagePaths = execSync(`npx lerna exec "pwd" | grep -v "^lerna"`, {
  stdio: "pipe"
})
  .toString()
  .trim()
  .split("\n");

const notPackagePaths = allPackagePaths.filter(path => {
  if (packagePaths.indexOf(path) === -1) {
    return true;
  } else {
    return false;
  }
});

// Filter out the `appPackageName`'s path.
const dependencyPackagePaths = packagePaths.filter(path => {
  const packageJson = require(`${path}/package.json`);
  return packageJson.name !== appPackageName;
});

// Watches the `src` directory of every dependency package
dependencyPackagePaths.forEach(packagePath => {
  const pathToWatch = `${packagePath}/src`;

  if (!existsSync(pathToWatch)) {
    console.info(`Can't watch ${pathToWatch} because it doesn't exist`);
    return;
  }

  const packageJson = require(`${packagePath}/package.json`);

  const notPackages = notPackagePaths.map(s => {
    const packageJson = require(s + "/package.json");
    return packageJson.name;
  });

  const refreshCommand = `npx lerna exec '${refreshPackageCommand}' --scope ${
    packageJson.name
  } --ignore ${appPackageName} ${notPackages.map(path => "--ignore " + path).join(" ")} --include-dependents`;

  return spawn(`npx chokidar ${pathToWatch} -c "${refreshCommand}"`, [], {
    shell: true,
    stdio: "inherit"
  });
});

// Spawns the webapp
spawn(startAppCommand, [], {
  shell: true,
  stdio: "inherit"
});

// Holds the process running
setInterval(() => {}, 1 << 30);
