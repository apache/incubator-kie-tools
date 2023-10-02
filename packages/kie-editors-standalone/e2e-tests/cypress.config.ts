import { defineConfig } from "cypress";

export default defineConfig({
  fixturesFolder: "./cypress/fixtures",
  screenshotsFolder: "../dist-e2e-tests/screenshots",
  videosFolder: "../dist-e2e-tests/videos",
  reporter: "junit",
  reporterOptions: {
    mochaFile: "../dist-e2e-tests/junit-report-[hash].xml",
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
