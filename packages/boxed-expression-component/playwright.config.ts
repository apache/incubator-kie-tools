import { devices, defineConfig } from "@playwright/test";
import { env } from "./env";

const buildEnv: any = env;

const ciReporters = [
  ["github"],
  ["junit", { outputFile: "./dist-tests/playwright-junit-report.xml" }],
  ["html", { outputFolder: "./dist-tests", open: "never" }],
];

export default defineConfig({
  testDir: "./tests/e2e",
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 2 : undefined,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: process.env.CI
    ? [
        ["github"],
        ["junit", { outputFile: "./dist-tests/playwright-junit-report.xml" }],
        ["html", { outputFolder: "./dist-tests", open: "never" }],
      ]
    : [["html", { outputFolder: "./dist-tests", open: "never" }], ["list"]],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    baseURL: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,

    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    screenshot: "only-on-failure",
    /* automatically record video on retry  */
    video: "on-first-retry",
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "on-first-retry",

    locale: "en-US",
  },

  /* Configure projects for major browsers */
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
      fullyParallel: true,
    },

    // {
    //   name: "firefox",
    //   use: { ...devices["Desktop Firefox"] },
    // },

    {
      name: "webkit",
      use: { ...devices["Desktop Safari"], permissions: ["clipboard-read"] },
    },

    {
      name: "Google Chrome",
      use: { ...devices["Desktop Chrome"], channel: "chrome", permissions: ["clipboard-read"] },
    },
  ],
  /* Run your local dev server before starting the tests */ /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm start",
    url: `http://localhost:${buildEnv.boxedExpressionComponent.dev.port}`,
    reuseExistingServer: !process.env.CI || true,
  },
});
