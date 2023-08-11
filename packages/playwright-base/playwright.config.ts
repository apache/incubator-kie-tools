import { devices, defineConfig } from "@playwright/test";

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
        ["junit", { outputFile: "./dist-e2e/junit-report-e2e.xml" }],
        ["html", { outputFolder: "./dist-e2e/reports/", open: "never" }],
        ["list"],
      ]
    : [["html", { outputFolder: "./dist-e2e/reports/", open: "never" }], ["list"]],
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

  outputDir: "dist-tests/output",

  /* Configure projects for major browsers */
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
      snapshotPathTemplate: "{testDir}/__screenshots__/{testFilePath}/{arg}-chromium-{ext}",
    },

    // {
    //   name: "firefox",
    //   use: { ...devices["Desktop Firefox"] },
    // },

    {
      name: "webkit",
      use: { ...devices["Desktop Safari"] },
      snapshotPathTemplate: "{testDir}/__screenshots__/{testFilePath}/{arg}-webkit-{ext}",
    },

    {
      name: "Google Chrome",
      use: { ...devices["Desktop Chrome"], channel: "chrome", permissions: ["clipboard-read"] },
      snapshotPathTemplate: "{testDir}/__screenshots__/{testFilePath}/{arg}-chrome-{ext}",
    },
  ],
});
