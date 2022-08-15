#!/usr/bin/env node

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const yargs = require("yargs/yargs");
const { hideBin } = require("yargs/helpers");
const { spawn, spawnSync } = require("child_process");

async function main() {
  const argv = yargs(hideBin(process.argv))
    .epilog(
      `
CLI tool to help executing shell scripts conditionally with a friendly syntax on Linux, macOS, and Windows.


__NOTE FOR WINDOWS USAGE__:
Because 'run-script-if' was created with pnpm/Yarn/NPM scripts, environment variables and Command Substitution syntax (\`$(expr)\`) in mind, 'run-script-if' will force the provided commands to be executed on PowerShell.

This is because pnpm, Yarn, and NPM default to the CMD shell on Windows, making it not ideal for Command Substitution-dependent commands. 

Apart from using it on commands, it's also possible to use the Command Substitution syntax on boolean conditions, like:

$ run-script-if --bool "$(my-custom-command --isEnabled)" --then "echo 'Hello'"
    `
    )
    .strict()
    .options({
      env: {
        type: "string",
        description: "Name of the environment variables which value will be compared to --eq.",
      },
      bool: {
        type: "string",
        description: "Boolean value to be used as condition",
      },
      eq: {
        default: "true",
        alias: "equals",
        type: "string",
        description: "Value to be compared with the condition supplied. Both --bool and --env.",
      },
      then: {
        array: true,
        required: true,
        type: "string",
        description: "Command(s) to execute if the condition is true.",
      },
      else: {
        default: [],
        array: true,
        required: false,
        type: "string",
        description: "Command(s) to execute if the condition is false.",
      },
      "true-if-empty": {
        default: "false",
        type: "string",
        description: "If the environment variable is not set, makes the condition be true.",
      },
      "ignore-errors": {
        default: "false",
        type: "string",
        description: "Ignore non-zero exit values when running command(s).",
      },
      silent: {
        default: false,
        type: "boolean",
        description: "Hide info logs from output. Logs from commands will still show.",
      },
      force: {
        default: false,
        type: "boolean",
        description: "Makes condition be true. Runs command(s) supplied to --then.",
      },
      catch: {
        default: [],
        array: true,
        required: false,
        type: "string",
        description: "Command(s) to execute at the end of execution if one of the commands being executed fails.",
      },
      finally: {
        default: [],
        array: true,
        required: false,
        type: "string",
        description:
          "Command(s) to execute at the end of execution. Provided commands will run even if one of the commands being executed fails.",
      },
    })
    .check((argv, options) => {
      if (argv.bool && argv.env) {
        throw new Error("Conditions must either be --bool or --env");
      }

      evalBoolStringArg(argv.bool);
      evalBoolStringArg(argv["ignore-errors"]);
      evalBoolStringArg(argv["true-if-empty"]);

      if (argv.bool && evalBoolStringArg(argv["true-if-empty"]) === "true") {
        throw new Error("Conditions with --bool cannot be used with --true-if-empty");
      }

      if (!(argv.bool || argv.env)) {
        throw new Error("Conditions must be either --bool or --env");
      }

      return true;
    }).argv;

  const log = (logFunction, ...args) => {
    if (!argv.silent) {
      logFunction(`[run-script-if]`, ...args);
    }
  };

  const envVarName = argv.env;
  const envVarValue = process.env[envVarName];
  const shouldRunIfEmpty = evalBoolStringArg(argv["true-if-empty"]) === "true";
  const ignoreErrors = evalBoolStringArg(argv["ignore-errors"]) === "true";
  const boolStringCondition = evalBoolStringArg(argv.bool);

  const condition =
    // --bool conditions are true if equals to --eq
    boolStringCondition === argv.eq ||
    // env var value is logically empty and --true-if-empty is enabled
    ((envVarValue === undefined || envVarValue === "") && shouldRunIfEmpty) ||
    // env var value is equal to the --eq argument
    envVarValue === argv.eq ||
    // env var value is ignored and the --then commands are executed
    argv.force;

  const commandStringsToRun = condition ? argv.then : argv.else;

  if (envVarName) log(console.info, LOGS.envVarSummary(envVarName, envVarValue));
  if (argv.bool) log(console.info, LOGS.boolSummary());
  if (shouldRunIfEmpty) log(console.info, LOGS.trueIfEmptyEnabled());
  if (ignoreErrors) log(console.info, LOGS.ignoreErrorsEnabled());
  if (argv.force) log(console.info, LOGS.forceEnabled());
  log(console.info, LOGS.conditionSummary(condition));

  await runCommandStrings(log, { ignoreErrors }, commandStringsToRun)
    .catch(async (e) => {
      const catchCommands = argv.catch;
      if (catchCommands.length <= 0) {
        throw e;
      }

      log(console.error, LOGS.runningCatchCommands());
      await runCommandStrings(log, { ignoreErrors: false }, catchCommands).catch(() => {
        //ignore
      });

      throw e;
    })
    .finally(async () => {
      let finallyCommands = argv.finally;
      if (finallyCommands.length <= 0) {
        return;
      }

      log(console.error, LOGS.runningFinallyCommands());
      await runCommandStrings(log, { ignoreErrors: false }, finallyCommands).catch(() => {
        //ignore
      });
    });
}

function evalBoolStringArg(boolArg) {
  let ret;
  if (process.platform === "win32" && boolArg && boolArg.startsWith("$")) {
    const output = spawnSync(boolArg, [], { stdio: "pipe", ...shell() });
    ret = String(output.stdout).trim();
  } else {
    ret = boolArg;
  }

  if (ret && ret !== "true" && ret !== "false") {
    throw new Error(
      `Boolean argument provided, but value is '${ret}'. Boolean arguments values must be either 'true' or 'false'.`
    );
  }

  return ret;
}

async function runCommandStrings(log, { ignoreErrors }, commandStringsToRun) {
  if (commandStringsToRun.length > 0) {
    log(console.info, LOGS.running(commandStringsToRun));
  } else {
    log(console.info, LOGS.runningZero());
  }

  let nCommandsFinished = 0;

  for (const runningCommandString of commandStringsToRun) {
    await new Promise((res, rej) => {
      log(console.info, LOGS.runningCommand(runningCommandString));
      const command = spawnCommandString(runningCommandString);

      // Never reject when 'ignoreErrors' is true.
      const ret = ignoreErrors ? res : rej;

      command.on("error", (data) => {
        logCommandError(log, commandStringsToRun, nCommandsFinished, runningCommandString, ignoreErrors);
        console.error(data.toString());
        ret({ code: 1 });
      });

      command.on("exit", (code) => {
        if (code !== 0) {
          logCommandError(log, commandStringsToRun, nCommandsFinished, runningCommandString, ignoreErrors);
          ret({ code });
          return;
        }

        nCommandsFinished += 1;
        log(console.info, LOGS.finishCommand(runningCommandString));
        res({ code });
      });
    });
  }
}

function spawnCommandString(commandString) {
  const bin = commandString.split(" ")[0];
  const args = commandString
    .split(" ")
    .slice(1)
    .filter((arg) => arg.trim().length > 0);
  return spawn(bin, args, { stdio: "inherit", ...shell() });
}

function shell() {
  return process.platform === "win32" ? { shell: "powershell.exe" } : {};
}

function logCommandError(log, commandStringsToRun, nCommandsFinished, runningCommandString, ignoreErrors) {
  const commandsLeft = commandStringsToRun.length - nCommandsFinished - 1;
  if (commandsLeft > 0) {
    const skippedCommands = commandStringsToRun.splice(nCommandsFinished + 1);
    if (ignoreErrors) {
      log(console.error, LOGS.ignoredErrorOnMiddleCommand(runningCommandString, commandsLeft, skippedCommands));
    } else {
      log(console.error, LOGS.errorOnMiddleCommand(runningCommandString, commandsLeft, skippedCommands));
    }
  } else {
    if (ignoreErrors) {
      log(console.error, LOGS.ignoredErrorOnLastCommand(runningCommandString));
    } else {
      log(console.error, LOGS.errorOnLastCommand(runningCommandString));
    }
  }
}

const LOGS = {
  runningCommand: (commandString) => {
    return `Running '${commandString}'`;
  },
  runningZero: () => {
    return `No commands to run.`;
  },
  runningFinallyCommands: () => {
    return `Execution finished. Running _finally_ command(s).`;
  },
  runningCatchCommands: () => {
    return `There are errors. Running _catch_ command(s).`;
  },
  finishCommand: (commandString) => {
    return `Finished '${commandString}'`;
  },
  errorOnLastCommand: (cmd) => {
    return `Error executing '${cmd}'.`;
  },
  ignoredErrorOnLastCommand: (cmd) => {
    return `Error executing '${cmd}'. Ignoring.`;
  },
  skipping: (commandStrings) => {
    return `Skipping ${commandStrings.length} command(s): ['${commandStrings.join("', '")}']`;
  },
  running: (commandStrings) => {
    return `Running ${commandStrings.length} command(s): ['${commandStrings.join("', '")}']`;
  },
  errorOnMiddleCommand: (commandString, commandsLeft, skippedCommandStrings) => {
    const skippedCommandStringsLog = `'${skippedCommandStrings.join("', '")}'`;
    return `Error executing '${commandString}'. Stopping and skipping ${commandsLeft} command(s): [${skippedCommandStringsLog}]`;
  },
  ignoredErrorOnMiddleCommand: (commandString, commandsLeft, skippedCommandStrings) => {
    const skippedCommandStringsLog = `'${skippedCommandStrings.join("', '")}'`;
    return `Error executing '${commandString}'. Ignoring and continuing with ${commandsLeft} remaining command(s): [${skippedCommandStringsLog}]`;
  },
  boolSummary: () => {
    return `Boolean condition supplied.`;
  },
  envVarSummary: (envVarName, envVarValue) => {
    let envVarValueLog;
    if (envVarValue === "") {
      envVarValueLog = `not set ("")`;
    } else if (envVarValue === undefined) {
      envVarValueLog = "not set";
    } else {
      envVarValueLog = `'${envVarValue}'`;
    }

    return `Environment variable '${envVarName}' is ${envVarValueLog}.`;
  },
  trueIfEmptyEnabled: () => {
    return `--true-if-empty is enabled.`;
  },
  forceEnabled: () => {
    return `--force is enabled.`;
  },
  ignoreErrorsEnabled: () => {
    return `--ignore-errors is enabled.`;
  },
  conditionSummary: (condition) => {
    const clause = condition ? "_then_" : "_else_";
    return `Condition is '${condition}'. Running ${clause} command(s).`;
  },
};

main().catch((e) => {
  process.exit(e.code);
});
