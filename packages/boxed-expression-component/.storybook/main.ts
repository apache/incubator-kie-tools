import baseConfig from "@kie-tools/storybook-base/storybookMain";

const config = {
  ...baseConfig,
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/addon-onboarding",
    "@storybook/addon-interactions",
  ],
};

export default config;
