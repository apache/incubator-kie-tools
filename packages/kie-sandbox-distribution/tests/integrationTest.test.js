const kieSandboxDistributionEnv = require("../env");

const env = kieSandboxDistributionEnv.env;
const kieSandboxUrl = `http://127.0.0.1:${env.kieSandboxDistribution.kieSandbox.port}`;
const corsProxyUrl = `http://127.0.0.1:${env.kieSandboxDistribution.corsProxy.port}`;
const extendedServicesUrl = `http://127.0.0.1:${env.kieSandboxDistribution.extendedServices.port}`;

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
