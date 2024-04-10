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
import merge from "webpack-merge";

export const baseConfig: (
  webpackEnv: { transpileOnly: boolean; sourceMaps: boolean },
  common: webpack.Configuration
) => StorybookConfig = (webpackEnv, common) => {
  console.log("Storybook base :: Webpack env :: transpileOnly: " + webpackEnv.transpileOnly);
  console.log("Storybook base :: Webpack env :: sourceMap: " + webpackEnv.sourceMaps);

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
      "@storybook/addon-controls",
      "@storybook/addon-docs",
      "@storybook/addon-highlight",
      "@storybook/addon-links",
      "@storybook/addon-measure",
      "@storybook/addon-outline",
      "@storybook/addon-toolbars",
      "@storybook/addon-viewport",
      "@storybook/addon-webpack5-compiler-babel",
    ],
    webpackFinal: async (config) => {
      return merge(config, common);
    },
  };
};
