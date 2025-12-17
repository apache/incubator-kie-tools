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

const buildEnv: any = env;

function basePlaywrightEnv(): Record<string, string> {
  return {
    STREAM_NAME: buildEnv.root.streamName,
    PLAYWRIGHT_BASE__enableGoogleChromeTestsForAppleSilicon:
      buildEnv.playwrightBase.enableGoogleChromeTestsForAppleSilicon,
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
  console.error("\x1b[31m[playwright-base] %s\x1b[0m", error instanceof Error ? error.message : String(error));
}

function isAppleSilicon(): boolean {
  return process.platform === "darwin" && process.arch === "arm64";
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
      .scriptName("@kie-tools/playwright-base")
      .epilog(`CLI tool to help using Playwright`)
      .example(`playwright-base container`, "Run Playwright test suite")
      .command(
        "container",
        "Containerization Playwright commands",
        (yargs) => {
          return yargs
            .command(
              "run",
              "Run Playwright test suite using containers.",
              (yargs) => {
                yargs
                  .option("ci", {
                    type: "boolean",
                    default: process.env.CI === "true" || process.env.CI === "1",
                    describe: "Use CI compose override",
                  })
                  .option("additional-env", {
                    type: "string",
                    default: "",
                    describe: "Additional envs to be forwarded to docker compose file",
                  })
                  .option("container-name", {
                    type: "string",
                    default: "",
                    describe: "Container name specified on the docker-compose file",
                    demandOption: true,
                  })
                  .option("container-workdir", {
                    type: "string",
                    default: "",
                    describe: "Container workdir. e.g. incubator-kie-tools/packages/<package_name>",
                    demandOption: true,
                  });
              },
              (argv) => {
                const extraEnv = collectAdditionalEnv(argv);
                // If Google Chrome is enabled check if tests are enabled for Apple Silicon
                const isGoogleChromeEnabled =
                  buildEnv.playwrightBase.enableGoogleChromeProject === true
                    ? isAppleSilicon() && buildEnv.playwrightBase.enableGoogleChromeTestsForAppleSilicon === true
                      ? "true"
                      : !isAppleSilicon()
                        ? "true"
                        : "false"
                    : "false";

                dockerComposeUp(!!argv.ci, {
                  PLAYWRIGHT_BASE__enableGoogleChromeProject: isGoogleChromeEnabled,
                  ...extraEnv,
                });
                console.info(
                  `[playwright-base] docker compose up done. Env
CI=${!!argv.ci}
PLAYWRIGHT_BASE__enableGoogleChromeTestsForAppleSilicon=${buildEnv.playwrightBase.enableGoogleChromeTestsForAppleSilicon}
PLAYWRIGHT_BASE__enableChromiumProject=${buildEnv.playwrightBase.enableChromiumProject}
PLAYWRIGHT_BASE__enableGoogleChromeProject=${isGoogleChromeEnabled}
PLAYWRIGHT_BASE__enableWebkitProject=${buildEnv.playwrightBase.enableWebkitProject}
PLAYWRIGHT_BASE__projectTimeout=${buildEnv.playwrightBase.projectTimeout}
PLAYWRIGHT_BASE__expectTimeout=${buildEnv.playwrightBase.expectTimeout}
PLAYWRIGHT_BASE__maxDiffPixelRatio=${buildEnv.playwrightBase.maxDiffPixelRatio}
PLAYWRIGHT_BASE__retries=${buildEnv.playwrightBase.retries}
PLAYWRIGHT_BASE__workers=${buildEnv.playwrightBase.workers}
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
              "Start test container shell",
              (yargs) => {
                yargs
                  .option("additional-env", {
                    type: "string",
                    default: "",
                    describe: "Additional envs to be forwarded to docker compose file",
                  })
                  .option("container-name", {
                    type: "string",
                    default: "",
                    describe: "Container name specified on the docker-compose file",
                    demandOption: true,
                  })
                  .option("container-workdir", {
                    type: "string",
                    default: "",
                    describe: "Container workdir. e.g. incubator-kie-tools/packages/<package_name>",
                    demandOption: true,
                  });
              },
              (argv) => {
                const extraEnv = collectAdditionalEnv(argv);
                const isGoogleChromeEnabled =
                  buildEnv.playwrightBase.enableGoogleChromeProject === true
                    ? isAppleSilicon() && buildEnv.playwrightBase.enableGoogleChromeTestsForAppleSilicon === true
                      ? "true"
                      : !isAppleSilicon()
                        ? "true"
                        : "false"
                    : "false";
                dockerComposeUp(false, {
                  PLAYWRIGHT_BASE__enableGoogleChromeProject: isGoogleChromeEnabled,
                  ...extraEnv,
                });
                console.info(
                  `[playwright-base] docker compose up done. Env
CI=${!!argv.ci}
PLAYWRIGHT_BASE__enableChromiumProject=${buildEnv.playwrightBase.enableChromiumProject}
PLAYWRIGHT_BASE__enableGoogleChromeProject=${isGoogleChromeEnabled}
PLAYWRIGHT_BASE__enableWebkitProject=${buildEnv.playwrightBase.enableWebkitProject}
PLAYWRIGHT_BASE__projectTimeout=${buildEnv.playwrightBase.projectTimeout}
PLAYWRIGHT_BASE__expectTimeout=${buildEnv.playwrightBase.expectTimeout}
PLAYWRIGHT_BASE__maxDiffPixelRatio=${buildEnv.playwrightBase.maxDiffPixelRatio}
PLAYWRIGHT_BASE__retries=${buildEnv.playwrightBase.retries}
PLAYWRIGHT_BASE__workers=${buildEnv.playwrightBase.workers}
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
              "Delete all container related to Playwright",
              () => {},
              () => {
                execSync(`docker compose -f ./tests-e2e/__containerization__/playwright-docker-compose.yml down`, {
                  stdio: "inherit",
                  env: mergeEnv(),
                });
              }
            )
            .demandCommand(1, "Please specify a subcommand: run | shell | clean");
        },
        () => {
          console.info(`[playwright-base] Choose between options`);
        }
      )
      .demandCommand(1, "Please specify a subcommand: container")
      .alias("h", "help")
      .parse();
  } catch (e) {
    prettyPrintError(e);
    exit(1, e);
  }
}

main();
