import type { StorybookConfig } from "@storybook/react-webpack5";

const config: StorybookConfig = {
  stories: ["../stories/**/*.stories.@(js|jsx|mjs|ts|tsx)"],
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/addon-onboarding",
    "@storybook/addon-interactions",
  ],
  framework: {
    name: "@storybook/react-webpack5",
    options: {},
  },
  docs: {
    autodocs: "tag",
  },
  webpackFinal: async (config, options) => {
    config.module?.rules?.push({
      test: /\.tsx?$/,
      use: [
        {
          loader: require.resolve("ts-loader"),
          options: {
            transpileOnly: undefined,
            compilerOptions: {
              importsNotUsedAsValues: "preserve",
              sourceMap: undefined,
            },
          },
        },
        {
          loader: require.resolve("@kie-tools-core/webpack-base/multi-package-live-reload-loader.js"),
        },
      ],
    });
    return config;
  },
};
export default config;
