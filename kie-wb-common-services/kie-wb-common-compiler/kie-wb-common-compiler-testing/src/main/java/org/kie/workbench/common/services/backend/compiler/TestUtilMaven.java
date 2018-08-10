package org.kie.workbench.common.services.backend.compiler;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtilMaven {

    private static Logger logger = LoggerFactory.getLogger(TestUtilMaven.class);
    private static final String JENKINS_SETTINGS_XML_FILE = "JENKINS_SETTINGS_XML_FILE";

    public static String getMavenRepo() throws Exception {
        List<String> repos = Arrays.asList("M2_REPO", "MAVEN_REPO_LOCAL", "MAVEN_REPO", "M2_REPO_LOCAL");
        String mavenRepo = "";
        for (String repo : repos) {
            if (System.getenv(repo) != null) {
                mavenRepo = System.getenv(repo);
                break;
            }
        }
        return StringUtils.isEmpty(mavenRepo) ? createMavenRepo().toAbsolutePath().toString() : mavenRepo;
    }

    public static java.nio.file.Path createMavenRepo() throws Exception {
        java.nio.file.Path mavenRepository = java.nio.file.Paths.get(System.getProperty("user.home"), ".m2/repository");
        if (!java.nio.file.Files.exists(mavenRepository)) {
            logger.info("Creating a m2_repo into " + mavenRepository);
            if (!java.nio.file.Files.exists(java.nio.file.Files.createDirectories(mavenRepository))) {
                logger.error("Folder not writable to create Maven repo{}", mavenRepository);
                throw new Exception("Folder not writable to create Maven repo:"+mavenRepository);
            }
        }
        return mavenRepository;
    }


    public static String getSettingsFile() {
        String jenkinsFile = System.getenv().get(JENKINS_SETTINGS_XML_FILE);
        if (jenkinsFile != null) {
            logger.info("Using settings.xml file provided by JENKINS:{}", jenkinsFile);
            return jenkinsFile;
        } else {
            logger.info("Using local settings.xml file.");
            return new File("src/test/settings.xml").getAbsolutePath();
        }
    }

}
