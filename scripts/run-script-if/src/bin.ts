#!/usr/bin/env node

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { spawn, spawnSync } from "child_process";
import * as path from "path";

function log(logFunction: (commandName: string, ...args: any[]) => void, isSilent: boolean, ...args: any[]) {
  if (!isSilent) {
    logFunction(`[run-script-if]`, ...args);
  }
}

type LogType = typeof log;

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
        type: "array",
        description: "Name of the environment variables which value will be compared to --eq.",
        default: [],
      },
      bool: {
        type: "array",
        description: "Boolean value to be used as condition",
        default: [],
      },
      eq: {
        default: "true",
        alias: "equals",
        type: "string",
        description: "Value to be compared with the condition supplied. Both --bool and --env.",
      },
      operator: {
        default: "and",
        description: "Comparison operator",
        type: "string",
        options: ["and", "or"] as const,
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
    .check((argv) => {
      argv.bool && argv.bool.every(evalBoolStringArg);
      evalBoolStringArg(argv["ignore-errors"]);
      const shouldRunIfEmpty = evalBoolStringArg(argv["true-if-empty"]) === "true";

      if (argv.bool.length && shouldRunIfEmpty) {
        throw new Error("Conditions with --bool cannot be used with --true-if-empty");
      }

      return true;
    })
    .parseSync();

  const envVarNames = argv.env;
  const envVarValues = envVarNames.map((envVarName) => ({ name: envVarName, value: process.env[envVarName] }));
  const shouldRunIfEmpty = evalBoolStringArg(argv["true-if-empty"]) === "true";
  const ignoreErrors = evalBoolStringArg(argv["ignore-errors"]) === "true";
  const boolStringConditions = argv.bool.map(evalBoolStringArg);
  const operator = argv.operator;

  const conditions = [];
  if (operator === "and") {
    boolStringConditions.length &&
      conditions.push(boolStringConditions.every((boolStringCondition) => boolStringCondition === argv.eq));
    if (shouldRunIfEmpty) {
      envVarValues.length &&
        conditions.push(
          envVarValues.every((envVarValue) => envVarValue.value === undefined || envVarValue.value === "")
        );
    } else {
      envVarValues.length && conditions.push(envVarValues.every((envVarValue) => envVarValue.value === argv.eq));
    }
  } else {
    boolStringConditions.length &&
      conditions.push(boolStringConditions.some((boolStringCondition) => boolStringCondition === argv.eq));
    if (shouldRunIfEmpty) {
      envVarValues.length &&
        conditions.push(
          envVarValues.some((envVarValue) => envVarValue.value === undefined || envVarValue.value === "")
        );
    } else {
      envVarValues.length && conditions.push(envVarValues.some((envVarValue) => envVarValue.value === argv.eq));
    }
  }

  const condition = argv.force || (operator === "and" ? conditions.every(Boolean) : conditions.some(Boolean));

  const commandStringsToRun = condition ? argv.then : argv.else;
  const isSilent = argv.silent;

  if (envVarValues.length)
    log(
      console.info,
      isSilent,
      envVarValues.map((envVarValue) => LOGS.envVarSummary(envVarValue))
    );
  if (argv.bool.length) log(console.info, isSilent, LOGS.boolSummary());
  if (shouldRunIfEmpty) log(console.info, isSilent, LOGS.trueIfEmptyEnabled());
  if (ignoreErrors) log(console.info, isSilent, LOGS.ignoreErrorsEnabled());
  if (argv.force) log(console.info, isSilent, LOGS.forceEnabled());
  log(console.info, isSilent, LOGS.operatorSummary(operator));
  log(console.info, isSilent, LOGS.conditionSummary(condition));

  await runCommandStrings(log, { ignoreErrors, isSilent }, commandStringsToRun)
    .catch(async (e) => {
      const catchCommands = argv.catch;
      if (catchCommands.length <= 0) {
        throw e;
      }

      log(console.error, isSilent, LOGS.runningCatchCommands());
      await runCommandStrings(log, { ignoreErrors: false, isSilent }, catchCommands).catch(() => {
        //ignore
      });

      throw e;
    })
    .finally(async () => {
      const finallyCommands = argv.finally;
      if (finallyCommands.length <= 0) {
        return;
      }

      log(console.error, isSilent, LOGS.runningFinallyCommands());
      await runCommandStrings(log, { ignoreErrors: false, isSilent }, finallyCommands).catch(() => {
        //ignore
      });
    });
}

function evalBoolStringArg(boolArg: never | string | number) {
  let ret;
  if (process.platform === "win32" && boolArg && typeof boolArg === "string" && boolArg.startsWith("$")) {
    const output = spawnSync(boolArg, [], { stdio: "pipe", ...shell() });
    ret = String(output.stdout).trim().toLowerCase();
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

async function runCommandStrings(
  log: LogType,
  { ignoreErrors, isSilent }: { ignoreErrors: boolean; isSilent: boolean },
  commandStringsToRun: string[]
) {
  if (commandStringsToRun.length > 0) {
    log(console.info, isSilent, LOGS.running(commandStringsToRun));
  } else {
    log(console.info, isSilent, LOGS.runningZero());
  }

  let nCommandsFinished = 0;

  for (const runningCommandString of commandStringsToRun) {
    await new Promise((res, rej) => {
      log(console.info, isSilent, LOGS.runningCommand(runningCommandString));
      const command = spawnCommandString(runningCommandString);

      // Never reject when 'ignoreErrors' is true.
      const ret = ignoreErrors ? res : rej;

      command.on("error", (data) => {
        logCommandError(log, commandStringsToRun, nCommandsFinished, runningCommandString, ignoreErrors, isSilent);
        console.error(data.toString());
        ret({ code: 1 });
      });

      command.on("exit", (code) => {
        if (code !== 0) {
          logCommandError(log, commandStringsToRun, nCommandsFinished, runningCommandString, ignoreErrors, isSilent);
          ret({ code });
          return;
        }

        nCommandsFinished += 1;
        log(console.info, isSilent, LOGS.finishCommand(runningCommandString));
        res({ code });
      });
    });
  }
}

function spawnCommandString(commandString: string) {
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

function logCommandError(
  log: LogType,
  commandStringsToRun: string[],
  nCommandsFinished: number,
  runningCommandString: string,
  ignoreErrors: boolean,
  isSilent: boolean
) {
  const commandsLeft = commandStringsToRun.length - nCommandsFinished - 1;
  if (commandsLeft > 0) {
    const skippedCommands = commandStringsToRun.splice(nCommandsFinished + 1);
    if (ignoreErrors) {
      log(
        console.error,
        isSilent,
        LOGS.ignoredErrorOnMiddleCommand(runningCommandString, commandsLeft, skippedCommands)
      );
    } else {
      log(console.error, isSilent, LOGS.errorOnMiddleCommand(runningCommandString, commandsLeft, skippedCommands));
    }
  } else {
    if (ignoreErrors) {
      log(console.error, isSilent, LOGS.ignoredErrorOnLastCommand(runningCommandString));
    } else {
      log(console.error, isSilent, LOGS.errorOnLastCommand(runningCommandString));
    }
  }
}

const LOGS = {
  runningCommand: (commandString: string) => {
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
  finishCommand: (commandString: string) => {
    return `Finished '${commandString}'`;
  },
  errorOnLastCommand: (cmd: string) => {
    return `Error executing '${cmd}'.`;
  },
  ignoredErrorOnLastCommand: (cmd: string) => {
    return `Error executing '${cmd}'. Ignoring.`;
  },
  skipping: (commandStrings: string[]) => {
    return `Skipping ${commandStrings.length} command(s): ['${commandStrings.join("', '")}']`;
  },
  running: (commandStrings: string[]) => {
    return `Running ${commandStrings.length} command(s): ['${commandStrings.join("', '")}']`;
  },
  errorOnMiddleCommand: (commandString: string, commandsLeft: number, skippedCommandStrings: string[]) => {
    const skippedCommandStringsLog = `'${skippedCommandStrings.join("', '")}'`;
    return `Error executing '${commandString}'. Stopping and skipping ${commandsLeft} command(s): [${skippedCommandStringsLog}]`;
  },
  ignoredErrorOnMiddleCommand: (commandString: string, commandsLeft: number, skippedCommandStrings: string[]) => {
    const skippedCommandStringsLog = `'${skippedCommandStrings.join("', '")}'`;
    return `Error executing '${commandString}'. Ignoring and continuing with ${commandsLeft} remaining command(s): [${skippedCommandStringsLog}]`;
  },
  boolSummary: () => {
    return `Boolean condition supplied.`;
  },
  envVarSummary: (envVarValue: { name: string | number; value: string | undefined }) => {
    let envVarValueLog;
    if (envVarValue.value === "") {
      envVarValueLog = `not set ("")`;
    } else if (envVarValue.value === undefined) {
      envVarValueLog = "not set";
    } else {
      envVarValueLog = `'${envVarValue}'`;
    }

    return `Environment variable '${envVarValue.name}' is ${envVarValueLog}.`;
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
  operatorSummary: (operator: string) => {
    return `Conditions are checked using the operator '${operator}'.`;
  },
  conditionSummary: (condition: boolean) => {
    const clause = condition ? "_then_" : "_else_";
    return `Condition is '${condition}'. Running ${clause} command(s).`;
  },
};

main().catch((e) => {
  process.exit(e.code);
});
