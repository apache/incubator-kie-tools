/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

const buildEnv = require("./env");
const path = require("path");
const yaml = require("js-yaml");
const fs = require("fs");

function main() {
  const EXTENDED_SERVICES_CONFIG_FILE = path.resolve("pkg/config/config.yaml");

  console.info("[extended-services-install] Updating Extended Services config file...");
  const config = yaml.load(fs.readFileSync(EXTENDED_SERVICES_CONFIG_FILE, "utf-8"));
  config.app.version = buildEnv.env.extendedServices.version;
  fs.writeFileSync(EXTENDED_SERVICES_CONFIG_FILE, yaml.dump(config));
  console.info("[extended-services-install] Done.");
}

main();
