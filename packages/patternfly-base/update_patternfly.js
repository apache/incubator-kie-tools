const exec = require("child_process").exec;

const UPDATE_PATTERNFLY_COMMAND =
  "yarn add @patternfly/patternfly @patternfly/react-charts @patternfly/react-core @patternfly/react-icons";
const LINK_DEPENDENCIES_COMMAND = "(cd ../../; npx lerna bootstrap)";
const BUILD_PROJECT_COMMAND = "(cd ../../; npx lerna run build:dev)";
const UPDATE_SNAPSHOTS_COMMAND = "(cd ../../; npx lerna run test --stream -- -u)";

async function executeCommand(command) {
  return new Promise((resolve, reject) => {
    const execution = exec(command);

    execution.stdout.pipe(process.stdout);
    execution.stderr.pipe(process.stderr);

    execution.on("close", (code) => {
      if (code === 0) {
        resolve();
      }
      reject(`child process exited with code ${code}`);
    });
  });
}

function updateDependencies() {
  return executeCommand(UPDATE_PATTERNFLY_COMMAND)
    .then(() => executeCommand(LINK_DEPENDENCIES_COMMAND))
    .then(() => executeCommand(BUILD_PROJECT_COMMAND));
}

function updateSnapshots() {
  return executeCommand(UPDATE_SNAPSHOTS_COMMAND);
}

function usageExample() {
  console.log("Usage yarn update [options]");
  console.log("Options:");
  console.log("-d, --dependencies-only    Update Patternfly dependencies");
  console.log("-s, --test-snapshots-only  Update project test snapshots");
}

function start() {
  if (process.argv.length > 3) {
    console.log("Can't handle more than one argument.");
    usageExample();
    return;
  }

  const [, , argument] = process.argv;

  console.log("Updating...");
  switch (argument) {
    case "-d":
    case "--dependencies-only": {
      updateDependencies()
        .then(() => console.log("Success"))
        .catch((err) => console.error("Error", err));
      break;
    }
    case "-s":
    case "--test-snapshots-only": {
      updateSnapshots()
        .then(() => console.log("Success"))
        .catch((err) => console.error("Error", err));
      break;
    }
    case undefined: {
      updateDependencies()
        .then(() => updateSnapshots())
        .then(() => console.log("Success"))
        .catch((err) => console.error("Error", err));
      break;
    }
    default: {
      console.log("Invalid argument.");
      usageExample();
    }
  }
}

start();
