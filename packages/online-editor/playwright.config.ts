import { defineConfig } from "@playwright/test";
import playwirghtBaseConfig from "@kie-tools/playwright-base/playwright.config";
import merge from "lodash/merge";
import { env } from "./env";

const buildEnv: any = env;

const customConfig = defineConfig({
  expect: {
    timeout: 15 * 1000,
  },
  use: {
    viewport: { width: 1600, height: 1200 },
    baseURL: `https://localhost:${buildEnv.onlineEditor.dev.port}`,
    ignoreHTTPSErrors: true,
  } /* Run your local dev server before starting the tests */,
  /* Run your local dev server before starting the tests */
  // webServer: {
  //   command: "pnpm start",
  //   url: `https://localhost:${buildEnv.onlineEditor.dev.port}`,
  //   reuseExistingServer: !process.env.CI || true,
  // },
});

export default defineConfig(merge(playwirghtBaseConfig, customConfig));
