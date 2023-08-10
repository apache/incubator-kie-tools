import type { StorybookConfig } from "@storybook/react-webpack5";
import { env } from "./env";
const buildEnv: any = env;

const importsNotUsedAsValues = buildEnv.live ? { importsNotUsedAsValues: "preserve" } : {};
const liveRoloadLoader = buildEnv.live
  ? [
      {
        loader: require.resolve("@kie-tools-core/webpack-base/multi-package-live-reload-loader.js"),
      },
    ]
  : [];

const config: StorybookConfig = {
  stories: ["../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)"],
  framework: {
    name: "@storybook/react-webpack5",
    options: {},
  },
  docs: {
    autodocs: "tag",
  },
  webpackFinal: async (config) => {
    config.module?.rules?.push({
      test: /\.tsx?$/,
      use: [
        {
          loader: require.resolve("ts-loader"),
          options: {
            transpileOnly: buildEnv.transpileOnly,
            compilerOptions: {
              ...importsNotUsedAsValues,
              sourceMap: buildEnv.sourceMap,
            },
          },
        },
        ...liveRoloadLoader,
      ],
    });
    return config;
  },
};
export default config;
