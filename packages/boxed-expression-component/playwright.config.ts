import { defineConfig } from "@playwright/test";
import playwirghtBaseConfig from "@kie-tools/playwright-base/playwright.config";
import merge from "lodash/merge";
import { env } from "./env";

const buildEnv: any = env;

export default defineConfig(
  merge(playwirghtBaseConfig, {
    use: {
      baseURL: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
    },
    /* Run your local dev server before starting the tests */ /* Run your local dev server before starting the tests */
    webServer: {
      command: "pnpm start",
      url: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
      reuseExistingServer: !process.env.CI || true,
    },
  })
);
