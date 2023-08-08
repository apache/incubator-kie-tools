import { defineConfig } from "@playwright/test";
import { playwirghtBase } from "@kie-tools/playwright-base/playwright.config";
import { env } from "./env";

const buildEnv: any = env;

export default defineConfig({
  ...playwirghtBase,
  /* Run your local dev server before starting the tests */ /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm start",
    url: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
    reuseExistingServer: !process.env.CI || true,
  },
});
