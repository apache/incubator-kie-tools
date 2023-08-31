import { config as baseConfig } from "@kie-tools/storybook-base/main";

const config = {
  ...baseConfig,
  staticDirs: ["../stories/assets"],
};

export default config;
