package org.guvnor.ala.source.git.config;

import org.guvnor.ala.config.SourceConfig;

/**
 * Interface that represent the specific of the Git Configuration data.
 */
public interface GitConfig extends SourceConfig {

    /**
     * Standard attribute name for setting the repository name. Pipeline inputs that wants to set the repository name
     * should use this parameter name.
     */
    String REPO_NAME = "repo-name";

    /**
     * Standard attribute name for setting the create repository option. Pipeline inputs that wants to set the create
     * repository option should use this parameter name.
     */
    String CREATE_REPO = "create-repo";

    /**
     * Standard attribute name for setting the origin address. Pipeline inputs that wants to configure the origin
     * address should use this parameter name.
     */
    String ORIGIN = "origin";

    /**
     * Standard attribute name for setting the branch to use. Pipeline inputs that wants to configure the branch to use
     * should use this parameter name.
     */
    String BRANCH = "branch";

    /**
     * Standard attribute name for setting the output path for storing the repository. Pipeline inputs that wants to
     * configure the output path should use this parameter name.
     */
    String OUT_DIR = "out-dir";

    /**
     * Get the Repository Name
     * @return a String with the repository name if provided, if not it will default to resolve the
     * expression ${input.repo-name} from the Pipeline Input map.
     */
    default String getRepoName() {
        return "${input." + REPO_NAME + "}";
    }

    /**
     * Get String to find out if we need to create the repo or not.
     * @return String true/false. If no set it will default to resolve the expression ${input.create-repo} from the
     * Pipeline Input map.
     */
    default String getCreateRepo() {
        return "${input." + CREATE_REPO + "}";
    }

    /**
     * Get the Origin address
     * @return String with the Origin name if provided, if not it will default to resolve the expression ${input.origin}
     * from the Pipeline Input map.
     */
    default String getOrigin() {
        return "${input." + ORIGIN + "}";
    }

    /**
     * Get the Branch Name of the repository that will be used
     * @return String with the Branch name if provided, if not it will default to resolve the expression ${input.branch}
     * from the Pipeline Input map.
     */
    default String getBranch() {
        return "${input." + BRANCH + "}";
    }

    /**
     * Get the OutPath where the repo is going to be stored
     * @return String with the OutPath if provided, if not it will default to
     * resolve the expression ${input.out-dir} from the Pipeline Input map
     */
    default String getOutPath() {
        return "${input." + OUT_DIR + "}";
    }
}
