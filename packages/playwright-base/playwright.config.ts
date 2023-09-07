import { devices, defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests/e2e",
  outputDir: "dist-e2e-tests/output",
  snapshotPathTemplate: "{testDir}/__screenshots__/{projectName}/{testFileDir}/{arg}{ext}",
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
        ["junit", { outputFile: "./dist-e2e-tests/junit-report-e2e.xml" }],
        ["html", { outputFolder: "./dist-e2e-tests/reports/", open: "never" }],
        ["list"],
      ]
    : [["html", { outputFolder: "./dist-e2e-tests/reports/", open: "never" }], ["list"]],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    screenshot: "only-on-failure",
    /* automatically record video on retry  */
    video: "on-first-retry",
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "on-first-retry",
    // Default locale;
    locale: "en-US",
  },
  /* Configure projects for major browsers */
  projects: [
    {
      timeout: 60000,
      name: "chromium",
      use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
    },

    // {
    //   name: "firefox",
    //   use: { ...devices["Desktop Firefox"] },
    // },

    {
      timeout: 60000,
      name: "webkit",
      use: { ...devices["Desktop Safari"], deviceScaleFactor: 1 },
    },

    {
      timeout: 60000,
      name: "Google Chrome",
      use: { ...devices["Desktop Chrome"], channel: "chrome", permissions: ["clipboard-read"] },
    },
  ],
});
