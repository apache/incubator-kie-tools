const core = require('@actions/core');
const github = require('@actions/github');

try {
    const tag = core.getInput('branch').replace("-prerelease");
    core.setOutput("tag", tag);
} catch (error) {
    core.setFailed(error.message);
}