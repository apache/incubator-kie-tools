import { defineConfig } from "cypress";

export default defineConfig({
  fixturesFolder: "fixtures",
  screenshotsFolder: "../dist-e2e-tests/screenshots",
  videosFolder: "../dist-e2e-tests/videos",
  chromeWebSecurity: false,
  video: true,
  defaultCommandTimeout: 10000,
  reporter: "junit",
  reporterOptions: {
    mochaFile: "../dist-e2e-tests/junit-report-[hash].xml",
    testsuitesTitle: "PMML Editor",
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
