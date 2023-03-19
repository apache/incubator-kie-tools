import { defineConfig } from "cypress";

export default defineConfig({
  screenshotsFolder: "../dist-it-tests/screenshots",
  videosFolder: "../dist-it-tests/videos",
  chromeWebSecurity: false,
  video: true,
  defaultCommandTimeout: 10000,
  reporter: "junit",
  reporterOptions: {
    testsuitesTitle: "Boxed Expression",
    mochaFile: "../dist-it-tests/junit-report-[hash].xml",
    testCaseSwitchClassnameAndName: true,
    suiteTitleSeparatedBy: ".",
    useFullSuiteTitle: true,
  },
  e2e: {
    setupNodeEvents(on, config) {
      return require("./cypress/plugins/index.js")(on, config);
    },
    specPattern: "e2e/**/*.cy.{js,jsx,ts,tsx}",
    supportFile: "support/e2e.ts",
  },
  retries: {
    runMode: 1,
    openMode: 0,
  },
});
