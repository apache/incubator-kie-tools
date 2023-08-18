import { config as baseConfig } from "@kie-tools/storybook-base/main";

const config = {
  ...baseConfig,
  docs: {
    autodocs: "tag",
    defaultName: "Overview",
  },
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/addon-onboarding",
    "@storybook/addon-interactions",
  ],
};

export default config;
