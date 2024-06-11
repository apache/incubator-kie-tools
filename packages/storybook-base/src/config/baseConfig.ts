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

import type { StorybookConfig } from "@storybook/react-webpack5";
import * as webpack from "webpack";
import * as path from "path";
import merge from "webpack-merge";

export const baseConfig: (
  webpackEnv: { transpileOnly: boolean; sourceMaps: boolean },
  commonConfig: webpack.Configuration
) => StorybookConfig = (webpackEnv, common) => {
  console.log("Storybook base :: Webpack env :: transpileOnly: " + webpackEnv.transpileOnly);
  console.log("Storybook base :: Webpack env :: sourceMap: " + webpackEnv.sourceMaps);
  console.log(require.resolve("@storybook/addon-controls"));
  return {
    typescript: {
      check: true,
    },
    core: {
      disableTelemetry: true,
    },
    stories: ["../stories/**/*.mdx", "../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)"],
    framework: {
      name: "@storybook/react-webpack5",
      options: {},
    },
    docs: {
      autodocs: "tag",
      defaultName: "Overview",
    },
    addons: [
      // Do not use addon package names directly,
      // due to the way `pnpm` structures node_modules directories,
      // we need to require them here, so that only `storybook-base` needs to declare them.
      path.dirname(require.resolve("@storybook/addon-controls/package.json")),
      path.dirname(require.resolve("@storybook/addon-docs/package.json")),
      path.dirname(require.resolve("@storybook/addon-highlight/package.json")),
      path.dirname(require.resolve("@storybook/addon-links/package.json")),
      path.dirname(require.resolve("@storybook/addon-measure/package.json")),
      path.dirname(require.resolve("@storybook/addon-outline/package.json")),
      path.dirname(require.resolve("@storybook/addon-toolbars/package.json")),
      path.dirname(require.resolve("@storybook/addon-viewport/package.json")),
      path.dirname(require.resolve("@storybook/addon-webpack5-compiler-babel/package.json")),
    ],
    webpackFinal: async (config) => {
      return merge(config, common);
    },
  };
};
