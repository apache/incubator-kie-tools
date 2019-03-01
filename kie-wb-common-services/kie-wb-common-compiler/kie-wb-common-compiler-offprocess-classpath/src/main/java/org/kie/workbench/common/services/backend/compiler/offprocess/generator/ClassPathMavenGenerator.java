/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.compiler.offprocess.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Executed by Maven with the exec-maven-plugin after the execution of the maven-dependency-plugin.
 * <p>The dependency plugin create the offprocess.cpath file, this class read this file and replace the initial path
 * to the mavenrepo used with a placeholder (<maven_repo>).</p>
 * <p>This placeholder wil be replaced with the maven repo used,
 * and sent as a param by the compiler-offprocess module when
 * a offprocess build will be required.</p>
 */
public class ClassPathMavenGenerator {

    private static Logger logger = LoggerFactory.getLogger(ClassPathMavenGenerator.class);
    private static final String servicesMod = "kie-wb-common-services",
    compilerMod = "kie-wb-common-compiler",
    offprocessMod = "kie-wb-common-compiler-offprocess-classpath",
    cpathPathFile = "offprocess.cpath",
    classPathFile = "offprocess.classpath.template",
    TARGET = "target",
    MAVEN_REPO_PLACEHOLDER = "<maven_repo>",
    JAR_EXT = ".jar",
    SEP = File.separator;

    public static void main(String[] args) throws Exception {
        String kieVersion = args[0];
        String baseDir = args[1];

        String content = new String(Files.readAllBytes(Paths.get(baseDir + SEP + cpathPathFile)));
        String replaced = content.replace(getMavenRepo(), MAVEN_REPO_PLACEHOLDER);
        replaced = replaceTargetInTheClassPathFile(kieVersion, replaced);

        Path offProcessModule = Paths.get(baseDir + SEP + "target" + SEP + "classes" + SEP + classPathFile);
        write(offProcessModule.toAbsolutePath().toString(), replaced);

        logger.info("\n************************************\nSaving {} to {} \n************************************\n\n", classPathFile, offProcessModule.toAbsolutePath().toString());
    }

    private static String replaceTargetInTheClassPathFile(String kieVersion, String replaced) {
        String[] deps = replaced.split(":");
        int i = 0;
        for(String dep : deps){
            if(dep.contains(TARGET)){
                cleanFromTarget(kieVersion, deps, i, dep);
            }
            i++;
        }
        return String.join(":", deps);
    }

    private static void cleanFromTarget(String kieVersion, String[] deps, int i, String dep) {
        String tmp = dep.substring(dep.lastIndexOf(TARGET) + 6);
        String jarTmp = tmp.substring(0, tmp.indexOf(JAR_EXT));
        String artifact = jarTmp.substring(jarTmp.lastIndexOf(File.separator)+1);
        String artifactNoVersionTmp = artifact.replace(kieVersion,"");
        String artifactNoVersion = artifactNoVersionTmp.substring(0,artifactNoVersionTmp.length() - 1);
        deps[i] = composeNewDependencyString(kieVersion, artifactNoVersion);
    }

    private static String composeNewDependencyString(String kieVersion, String artifactNoVersion) {
        StringBuilder sbi = new StringBuilder();
        sbi.append(MAVEN_REPO_PLACEHOLDER).
                append(SEP).
                append("org").append(SEP).append("kie").append(SEP).append("workbench").append(SEP).append("services").
                append(SEP).
                append(artifactNoVersion).append(SEP).append(kieVersion).append(SEP).append(artifactNoVersion).append("-").append(kieVersion).append(JAR_EXT);
        return sbi.toString();
    }

    private static void write(String filename, String content) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }

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

    public static Path createMavenRepo() throws Exception {
        Path mavenRepository = Paths.get(System.getProperty("user.home"), ".m2/repository");
        if (!Files.exists(mavenRepository)) {
            logger.info("Creating a m2_repo into " + mavenRepository);
            if (!Files.exists(Files.createDirectories(mavenRepository))) {
                logger.error("Folder not writable to create Maven repo{}", mavenRepository);
                throw new Exception("Folder not writable to create Maven repo:"+mavenRepository);
            }
        }
        return mavenRepository;
    }
}
