const kieSandboxDistributionEnv = require("../env");
const execSync = require("child_process").execSync;

const env = kieSandboxDistributionEnv.env;
const kieSandboxUrl = `http://127.0.0.1:${env.kieSandboxDistribution.kieSandbox.exposedPort}`;
const corsProxyUrl = `http://127.0.0.1:${env.kieSandboxDistribution.corsProxy.exposedPort}`;
const extendedServicesUrl = `http://127.0.0.1:${env.kieSandboxDistribution.extendedServices.exposedPort}`;

describe("Test built images individually", async () => {
  it("cors-proxy homepage", async () => {
    expect(await (await fetch(corsProxyUrl)).text()).toMatchSnapshot();
  });
  it("cors-proxy ping", async () => {
    expect(await (await fetch(`${corsProxyUrl}/ping`)).text()).toBe("pong");
  });
  it("extended-services homepage", async () => {
    expect(await (await fetch(extendedServicesUrl)).text()).toMatchSnapshot();
  });
  it("extended-services ping", async () => {
    expect(await (await fetch(`${extendedServicesUrl}/ping`)).json()).toHaveProperty("proxy");
  });
  it("kie-sandbox homepage", async () => {
    expect(await (await fetch(kieSandboxUrl)).text()).toContain('<script src="index.js"></script>');
  });
});
