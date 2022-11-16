import { defineConfig } from "cypress";

export default defineConfig({
  fixturesFolder: "fixtures",
  screenshotsFolder: "../dist-it-tests/screenshots",
  videosFolder: "../dist-it-tests/videos",
  chromeWebSecurity: false,
  video: true,
  downloadsFolder: "downloads",
  defaultCommandTimeout: 10000,
  reporter: "junit",
  reporterOptions: {
    mochaFile: "../dist-it-tests/junit-report-[hash].xml",
    testsuitesTitle: "Serverless Logic Sandbox",
    testCaseSwitchClassnameAndName: true,
    suiteTitleSeparatedBy: ".",
    useFullSuiteTitle: true,
  },
  e2e: {
    specPattern: "e2e/**/*.cy.{js,jsx,ts,tsx}",
    supportFile: "support/e2e.ts",
  },
  retries: {
    runMode: 1,
    openMode: 0,
  },
});
