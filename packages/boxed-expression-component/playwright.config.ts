import { defineConfig } from "@playwright/test";
import { playwrightBase } from "@kie-tools-core/tests-base/playwright.config";
import { env } from "./env";

const buildEnv: any = env;

export default defineConfig({
  ...playwrightBase,
  use: {
    ...playwrightBase.use,
    baseURL: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
  },

  /* Run your local dev server before starting the tests */ /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm start",
    url: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
    reuseExistingServer: !process.env.CI || true,
  },
});
