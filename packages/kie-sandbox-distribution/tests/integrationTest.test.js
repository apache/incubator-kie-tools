const kieSandboxDistributionEnv = require("../env");
const execSync = require("child_process").execSync;

const env = kieSandboxDistributionEnv.env;
const kieSandboxUrl = `http://127.0.0.1:${env.kieSandboxDistribution.kieSandbox.exposedPort}`;
const corsProxyUrl = `http://127.0.0.1:${env.kieSandboxDistribution.corsProxy.exposedPort}`;
const extendedServicesUrl = `http://127.0.0.1:${env.kieSandboxDistribution.extendedServices.exposedPort}`;

describe("Test built images individually", async () => {
  it("cors-proxy built image", () => {
    const imageFullTag = `${kieSandboxDistributionEnv.env.kieSandboxDistribution.corsProxy.imageRegistry}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.corsProxy.imageAccount}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.corsProxy.imageName}:\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.corsProxy.imageTag}`;

    expect(execSync(`docker image inspect ${imageFullTag} > /dev/null`)).toMatchSnapshot();
  });
  it("cors-proxy homepage", async () => {
    expect(await (await fetch(corsProxyUrl)).text()).toMatchSnapshot();
  });
  it("cors-proxy ping", async () => {
    expect(await (await fetch(`${corsProxyUrl}/ping`)).text()).toBe("pong");
  });
  it("extended-services built image", () => {
    const imageFullTag = `${kieSandboxDistributionEnv.env.kieSandboxDistribution.extendedServices.imageRegistry}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.extendedServices.imageAccount}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.extendedServices.imageName}:\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.extendedServices.imageTag}`;

    expect(execSync(`docker image inspect ${imageFullTag} > /dev/null`)).toMatchSnapshot();
  });
  it("extended-services homepage", async () => {
    expect(await (await fetch(extendedServicesUrl)).text()).toMatchSnapshot();
  });
  it("extended-services ping", async () => {
    expect(await (await fetch(`${extendedServicesUrl}/ping`)).json()).toHaveProperty("proxy");
  });
  it("kie-sandbox built image", () => {
    const imageFullTag = `${kieSandboxDistributionEnv.env.kieSandboxDistribution.kieSandbox.imageRegistry}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.kieSandbox.imageAccount}/\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.kieSandbox.imageName}:\
${kieSandboxDistributionEnv.env.kieSandboxDistribution.kieSandbox.imageTag}`;

    expect(execSync(`docker image inspect ${imageFullTag} > /dev/null`)).toMatchSnapshot();
  });
  it("kie-sandbox homepage", async () => {
    expect(await (await fetch(kieSandboxUrl)).text()).toContain('<script src="index.js"></script>');
  });
});
