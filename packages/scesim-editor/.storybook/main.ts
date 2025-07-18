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

import { baseConfig } from "@kie-tools/storybook-base/dist/config/baseConfig";
import common from "@kie-tools-core/webpack-base/webpack.common.config";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "../env";
const buildEnv: any = env; // build-env is not typed

const config = {
  ...baseConfig(
    process.env.CI ? buildEnv.webpack.prod : buildEnv.webpack.dev,
    common({ live: false, dev: process.env.CI ? false : true })
  ),
};

export default config;
