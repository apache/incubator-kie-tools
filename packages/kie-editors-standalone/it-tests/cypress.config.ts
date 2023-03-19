import { defineConfig } from "cypress";

export default defineConfig({
  fixturesFolder: "./cypress/fixtures",
  screenshotsFolder: "../dist-it-tests/screenshots",
  videosFolder: "../dist-it-tests/videos",
  reporter: "junit",
  reporterOptions: {
    mochaFile: "../dist-it-tests/junit-report-[hash].xml",
    testsuitesTitle: "BPMN and DMN Standalone Editors",
    testCaseSwitchClassnameAndName: true,
    suiteTitleSeparatedBy: ".",
    useFullSuiteTitle: true,
    rootSuiteTitle: "@kie-tools/kie-editors-standalone",
  },
  video: true,
  e2e: {
    specPattern: "./cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
  },
  retries: {
    runMode: 1,
    openMode: 0,
  },
});
