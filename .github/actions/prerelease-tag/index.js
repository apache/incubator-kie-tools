const core = require('@actions/core');

try {
    const tag = core.getInput('branch').replace("-prerelease");
    core.setOutput("tag", tag);
} catch (error) {
    core.setFailed(error.message);
}