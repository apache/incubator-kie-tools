import { defineConfig } from "@playwright/test";
import playwirghtBaseConfig from "@kie-tools/playwright-base/playwright.config";
import merge from "lodash/merge";
import { env } from "./env";

const buildEnv: any = env;

const customConfig = defineConfig({
  use: {
    baseURL: `http://localhost:${buildEnv.boxedExpressionComponent.storybook.port}`,
  },
  /* Run your local dev server before starting the tests */ /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm start",
    url: `http://localhost:${buildEnv.boxedExpressionComponent.storybook.port}/iframe.html?id=expressions-boxedexpressioneditor--empty-expression`,
    reuseExistingServer: !process.env.CI || true,
  },
});

export default defineConfig(merge(playwirghtBaseConfig, customConfig));
