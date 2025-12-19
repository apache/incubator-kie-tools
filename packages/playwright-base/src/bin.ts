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

import { execSync, spawnSync } from "child_process";
import yargs from "yargs/yargs";
import { terminalWidth, exit } from "yargs";
import { hideBin } from "yargs/helpers";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "../env";

const CLI_NAME = "pw-e2e-container";

const buildEnv: any = env;

function basePlaywrightEnv(): Record<string, string> {
  return {
    STREAM_NAME: buildEnv.root.streamName,
    PLAYWRIGHT_BASE__enableChromiumProject: buildEnv.playwrightBase.enableChromiumProject,
    PLAYWRIGHT_BASE__enableGoogleChromeProject: buildEnv.playwrightBase.enableGoogleChromeProject,
    PLAYWRIGHT_BASE__enableWebkitProject: buildEnv.playwrightBase.enableWebkitProject,
    PLAYWRIGHT_BASE__projectTimeout: buildEnv.playwrightBase.projectTimeout,
    PLAYWRIGHT_BASE__expectTimeout: buildEnv.playwrightBase.expectTimeout,
    PLAYWRIGHT_BASE__maxDiffPixelRatio: buildEnv.playwrightBase.maxDiffPixelRatio,
    PLAYWRIGHT_BASE__retries: buildEnv.playwrightBase.retries,
    PLAYWRIGHT_BASE__workers: buildEnv.playwrightBase.workers,
  };
}

function getPlaywrightBaseEnv() {
  return `PLAYWRIGHT_BASE__enableGoogleChromeTestsForAppleSilicon=${buildEnv.playwrightBase.enableGoogleChromeTestsForAppleSilicon}
PLAYWRIGHT_BASE__enableChromiumProject=${buildEnv.playwrightBase.enableChromiumProject}
PLAYWRIGHT_BASE__enableGoogleChromeProject=${buildEnv.playwrightBase.enableGoogleChromeProject}
PLAYWRIGHT_BASE__enableWebkitProject=${buildEnv.playwrightBase.enableWebkitProject}
PLAYWRIGHT_BASE__projectTimeout=${buildEnv.playwrightBase.projectTimeout}
PLAYWRIGHT_BASE__expectTimeout=${buildEnv.playwrightBase.expectTimeout}
PLAYWRIGHT_BASE__maxDiffPixelRatio=${buildEnv.playwrightBase.maxDiffPixelRatio}
PLAYWRIGHT_BASE__retries=${buildEnv.playwrightBase.retries}
PLAYWRIGHT_BASE__workers=${buildEnv.playwrightBase.workers}`;
}

type EnvMap = Record<string, string>;

function parseCommaEnvList(input: string): EnvMap {
  const out: EnvMap = {};
  const s = (input ?? "").trim();
  if (s.length === 0) return out;

  for (const raw of s.split(",")) {
    const pair = raw.trim();
    if (!pair) continue;

    const eq = pair.indexOf("=");
    if (eq <= 0) {
      throw new Error(`Invalid env pair "${pair}". Expected KEY=VALUE.`);
    }

    const key = pair.slice(0, eq).trim();
    const value = pair.slice(eq + 1).trim();

    if (!/^[A-Za-z_][A-Za-z0-9_]*$/.test(key)) {
      throw new Error(`Invalid env key "${key}". Must match [A-Za-z_][A-Za-z0-9_]*.`);
    }

    out[key] = value;
  }
  return out;
}

function collectAdditionalEnv(argv: any): EnvMap {
  const entries = ([] as string[]).concat(argv.additionalEnv ?? argv["additional-env"] ?? []);
  const merged: EnvMap = {};
  for (const entry of entries) {
    const parsed = parseCommaEnvList(entry);
    Object.assign(merged, parsed);
  }
  return merged;
}

function prettyPrintError(error: unknown) {
  console.error(`\x1b[31m[${CLI_NAME}] %s\x1b[0m`, error instanceof Error ? error.message : String(error));
}

function mergeEnv(overrides: EnvMap = {}): NodeJS.ProcessEnv {
  return { ...process.env, ...basePlaywrightEnv(), ...overrides };
}

function dockerComposeUp(ci: boolean, overrides: Record<string, string> = {}) {
  const composeBase = "./tests-e2e/__containerization__/playwright-docker-compose.yml";
  const composeCi = "./tests-e2e/__containerization__/playwright-docker-compose.ci.yml";
  const files = ci ? [`-f`, composeBase, `-f`, composeCi] : [`-f`, composeBase];

  execSync(`docker compose ${files.join(" ")} up -d`, { stdio: "inherit", env: mergeEnv(overrides) });
}

async function main() {
  try {
    await yargs(hideBin(process.argv))
      .version(false)
      .wrap(Math.min(150, terminalWidth()))
      .scriptName(CLI_NAME)
      .epilog(
        `A CLI tool to assist running containerized Playwright tests.
It loads environment variables and passes them through the docker-compose file, and helps with starting the test suite, opening an interactive shell inside the container, and cleaning up.`
      )
      .example(
        "containerized-playwright run --container-name my_playwright_container --container-workdir incubator-kie-tools/packages/my-package\n",
        "Run the Playwright test suite locally (no CI override) using the specified container and workdir.\n"
      )
      .example(
        "containerized-playwright run --ci --container-name my_playwright_container --container-workdir incubator-kie-tools/packages/my-package\n",
        "Run the Playwright test suite in CI mode (applies CI docker-compose override).\n"
      )
      .example(
        "containerized-playwright run --container-name e2e --container-workdir incubator-kie-tools/packages/foo --additional-env BUILD_ID=123,REPORT_DIR=/tmp/reports\n",
        "Run with extra environment variables forwarded to docker-compose (comma-separated KEY=VALUE pairs).\n"
      )
      .example(
        "containerized-playwright run --container-name e2e --container-workdir incubator-kie-tools/packages/foo --additional-env FOO=bar --additional-env COMMIT_SHA=deadbeef\n",
        "Pass multiple --additional-env options; later pairs override earlier keys if duplicated.\n"
      )
      .example(
        "CI=true containerized-playwright run --container-name e2e --container-workdir incubator-kie-tools/packages/foo\n",
        "Leverage CI environment variable to auto-enable CI mode (equivalent to --ci when CI=true or CI=1).\n"
      )
      .example(
        "containerized-playwright shell --container-name my_playwright_container --container-workdir incubator-kie-tools/packages/my-package\n",
        "Start the container (if needed) and open an interactive bash shell in the package workdir.\n"
      )
      .example(
        "containerized-playwright shell --container-name e2e --container-workdir incubator-kie-tools/packages/foo --additional-env DEBUG=true\n",
        "Open an interactive shell with extra environment variables forwarded to docker-compose.\n"
      )
      .example(
        "containerized-playwright shell --container-name e2e --container-workdir incubator-kie-tools/packages/foo --additional-env FOO=bar,BAZ=qux\n",
        "Forward multiple env variables in a single --additional-env option using comma-separated pairs.\n"
      )
      .example(
        "containerized-playwright clean\n",
        "Stop and remove Playwright-related containers using the base docker-compose file.\n"
      )
      .command(
        "run",
        "Run the Playwright test suite inside Docker containers. This command will start the required containers using docker-compose and execute the Playwright tests in the specified container workdir.",
        (yargs) => {
          yargs
            .option("ci", {
              type: "boolean",
              default: process.env.CI === "true" || process.env.CI === "1",
              describe: "Enable CI mode by applying the CI-specific docker-compose override file.",
            })
            .option("additional-env", {
              type: "string",
              default: "",
              describe:
                "Comma-separated KEY=VALUE pairs of additional environment variables to forward to docker-compose. Can be repeated.",
            })
            .option("container-name", {
              type: "string",
              default: "",
              describe: "Name of the container as defined in the docker-compose file. Required.",
              demandOption: true,
            })
            .option("container-workdir", {
              type: "string",
              default: "",
              describe:
                "Path inside the container where Playwright tests are located. Example: incubator-kie-tools/packages/<package_name>. Required.",
              demandOption: true,
            });
        },
        (argv) => {
          const extraEnv = collectAdditionalEnv(argv);
          dockerComposeUp(!!argv.ci, { ...extraEnv });
          console.info(
            `[${CLI_NAME}] docker compose up done. Env
CI=${!!argv.ci}
${getPlaywrightBaseEnv()}
extraEnv=${JSON.stringify(extraEnv)}
`
          );
          execSync(
            `docker exec -i ${argv["container-name"]} /bin/bash -c "cd ${argv["container-workdir"]} && pnpm test-e2e:run"`,
            {
              stdio: "inherit",
            }
          );
        }
      )
      .command(
        "shell",
        "Open an interactive shell inside the Playwright test container. This command starts the required container using docker-compose and launches a shell in the specified workdir inside the container.",
        (yargs) => {
          yargs
            .option("additional-env", {
              type: "string",
              default: "",
              describe:
                "Comma-separated KEY=VALUE pairs of additional environment variables to forward to docker-compose. Can be repeated.",
            })
            .option("container-name", {
              type: "string",
              default: "",
              describe: "Name of the container as defined in the docker-compose file. Required.",
              demandOption: true,
            })
            .option("container-workdir", {
              type: "string",
              default: "",
              describe:
                "Path inside the container where Playwright tests are located. Example: incubator-kie-tools/packages/<package_name>. Required.",
              demandOption: true,
            });
        },
        (argv) => {
          const extraEnv = collectAdditionalEnv(argv);
          dockerComposeUp(!!argv.ci, { ...extraEnv });
          console.info(
            `[${CLI_NAME}] docker compose up done. Env
${getPlaywrightBaseEnv()}
extraEnv=${JSON.stringify(extraEnv)}
`
          );

          const result = spawnSync(
            "docker",
            [
              "exec",
              "-it",
              argv["container-name"] as any,
              "/bin/bash",
              "-c",
              `cd ${argv["container-workdir"]} && exec /bin/bash`,
            ],
            {
              stdio: "inherit",
              env: mergeEnv(),
            }
          );
          if (result.status !== 0) {
            throw new Error(`docker exec shell exited with code ${result.status}`);
          }
        }
      )
      .command(
        "clean",
        "Stop and remove all Playwright-related containers created by docker-compose. This command runs 'docker compose down' using the base Playwright compose file.",
        () => {},
        () => {
          execSync(`docker compose -f ./tests-e2e/__containerization__/playwright-docker-compose.yml down`, {
            stdio: "inherit",
            env: mergeEnv(),
          });
        }
      )
      .demandCommand(1, "Please specify a command: run | shell | clean")
      .alias("h", "help")
      .parse();
  } catch (e) {
    prettyPrintError(e);
    exit(1, e);
  }
}

main();
