class PipelineVars implements Serializable {

    String githubRepositoryOrg = 'apache';
    String githubRepositoryName = 'kie-tools';
    String githubRepositorySlug = 'apache/kie-tools';

    String quayPushCredentialsId = 'quay-io-kie-tools-token';
    String openshiftCredentialsId = 'openshift-kie-tools-token';
    String kieToolsBotGithubCredentialsId = 'kie-tools-bot-gh';
    String kieToolsBotGithubTokenCredentialsId = 'kie-tools-bot-gh-token';
    String kieToolsGithubCodeQLTokenCredentialsId = 'kie-tools-gh-codeql-token';
    String chromeStoreCredentialsId = 'kie-tools-chome-store';
    String chromeStoreRefreshTokenCredentialsId = 'kie-tools-chome-store-refresh-token';
    String npmTokenCredentialsId = 'kie-tools-npm-token';
    String buildKiteTokenCredentialsId = 'kie-tools-build-kite-token';

    String defaultArtifactsTempDir = 'artifacts-tmp';

}

return new PipelineVars();
