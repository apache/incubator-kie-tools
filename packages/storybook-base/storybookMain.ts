import type { StorybookConfig } from "@storybook/react-webpack5";
import { env } from "./env";
const buildEnv: any = env;

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
    if (buildEnv.storybookBase.live) {
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
export default config;
