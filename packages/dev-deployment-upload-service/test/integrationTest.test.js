const execSync = require("child_process").execSync;

describe("Test built images individually", async () => {
  beforeAll(() => {
    execSync("pnpm start-test-servers && wait-on -t 2m http://localhost:8092/upload-status");
  });
  it("buildtime install", async () => {
    // TODO
  });
  it("runtime install", async () => {
    // TODO
  });
});
