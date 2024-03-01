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
import { env } from "./env";
const buildEnv: any = env;

export const config: StorybookConfig = {
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
  ],
  webpackFinal: async (config) => {
    if (process.env.STORYBOOK_BASE_WRAPPER_INTERNAL__liveReload) {
      config.module?.rules?.push({
        test: /\.tsx?$/,
        use: [
          {
            loader: require.resolve("ts-loader"),
            options: {
              transpileOnly: buildEnv.transpileOnly,
              compilerOptions: {
                importsNotUsedAsValues: "preserve",
                sourceMap: buildEnv.sourceMap,
              },
            },
          },
          {
            loader: require.resolve("@kie-tools-core/webpack-base/multi-package-live-reload-loader.js"),
          },
        ],
      });
    }

    return config;
  },
};
