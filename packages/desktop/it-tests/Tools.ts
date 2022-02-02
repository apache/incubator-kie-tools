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

import { Application, SpectronClient } from "spectron";
import { join } from "path";
import { platform } from "os";

export async function initApp(): Promise<Application> {
  return await new Application({
    path: join(__dirname, "..", "node_modules", ".bin", "electron" + (platform() === "win32" ? ".cmd" : "")),
    args: [join(__dirname, "..")],
  }).start();
}

export async function waitLoading(client: SpectronClient) {
  // wait until loading popup disappears
  await client.$("[data-testid='loading-screen-div']");
  await client.$(".kie-tools--keyboard-shortcuts-icon");
}
