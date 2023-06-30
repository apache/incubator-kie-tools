const kieSandboxDistributionEnv = require("../env");

const env = kieSandboxDistributionEnv.env;

const buildContainerImages = env.containerImages.build;
const runTests = env.tests.run;

console.log(buildContainerImages && runTests ? "true" : "false");
