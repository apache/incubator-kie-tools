const core = require("@actions/core");

try {
  const tag = core.getInput("ref").replace("-prerelease", "").split("/").pop();
  core.setOutput("tag", tag);
} catch (error) {
  core.setFailed(error.message);
}
