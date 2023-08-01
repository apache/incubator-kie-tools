import { devices, defineConfig } from "@playwright/test";
import { env } from "./env";

const buildEnv: any = env;

export default defineConfig({
  testDir: "./e2e",
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
        ["junit", { outputFile: "./dist-e2e/junit-report-e2e.xml" }],
        ["html", { outputFolder: "./dist-e2e/reports/", open: "never" }],
        ["list"],
      ]
    : [["html", { outputFolder: "./dist-e2e/reports/", open: "never" }], ["list"]],
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

    headless: true,
  },

  outputDir: "dist-e2e/output",

  /* Configure projects for major browsers */
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
    },

    // {
    //   name: "firefox",
    //   use: { ...devices["Desktop Firefox"] },
    // },

    {
      name: "webkit",
      use: { ...devices["Desktop Safari"] },
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
