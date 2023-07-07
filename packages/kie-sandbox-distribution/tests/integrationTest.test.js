const kieSandboxDistributionEnv = require("../env");

const env = kieSandboxDistributionEnv.env;
const kieSandboxUrl = `http://127.0.0.1:${env.kieSandboxDistribution.kieSandbox.port}`;
const gitCorsProxyUrl = `http://127.0.0.1:${env.kieSandboxDistribution.gitCorsProxy.port}`;
const extendedServicesUrl = `http://127.0.0.1:${env.kieSandboxDistribution.extendedServices.port}`;

describe("Test built images individually", async () => {
  it("git-cors-proxy homepage", async () => {
    expect(await (await fetch(gitCorsProxyUrl)).text()).toMatchSnapshot();
  });
  it("git-cors-proxy ping", async () => {
    expect(await (await fetch(`${gitCorsProxyUrl}/ping`)).text()).toBe("pong");
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
