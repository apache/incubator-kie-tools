const kieSandboxDistributionEnv = require("../env");

async function main() {
  const errors = [];
  const env = kieSandboxDistributionEnv.env;
  const kieSandboxUrl = `http://localhost:${env.kieSandboxDistribution.kieSandbox.port}`;
  const gitCorsProxyUrl = `http://localhost:${env.kieSandboxDistribution.gitCorsProxy.port}/ping`;
  const extendedServicesUrl = `http://localhost:${env.kieSandboxDistribution.extendedServices.port}/ping`;
  console.log("Testing if images are up:");
  console.log(`KIE Sandbox on ${kieSandboxUrl}`);
  try {
    await fetch(kieSandboxUrl);
    console.log("OK!");
  } catch (e) {
    console.log("Failed!");
    errors.push({ kieSandbox: e });
  }
  console.log(`Git CORS Proxy on ${gitCorsProxyUrl}`);
  try {
    await fetch(gitCorsProxyUrl);
    console.log("OK!");
  } catch (e) {
    console.log("Failed!");
    errors.push({ gitCorsProxy: e });
  }
  console.log(`Extended Services on ${extendedServicesUrl}`);
  try {
    await fetch(extendedServicesUrl);
    console.log("OK!");
  } catch (e) {
    console.log("Failed!");
    errors.push({ extendedServices: e });
  }

  if (errors.length) {
    console.error("Some services failed to respond.");
    console.error(JSON.stringify(errors, null, 2));
    process.exit(1);
  }

  console.log("Finished successfully!");
  process.exit(0);
}

main();
