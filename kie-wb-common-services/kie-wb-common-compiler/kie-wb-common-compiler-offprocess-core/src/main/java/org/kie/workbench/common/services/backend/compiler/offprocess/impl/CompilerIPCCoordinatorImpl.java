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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationPropertiesStrategy;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.offprocess.ClientIPC;
import org.kie.workbench.common.services.backend.compiler.offprocess.CompilerIPCCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinator of the build executed in a separate process and the cleint to read the result
 */
public class CompilerIPCCoordinatorImpl implements CompilerIPCCoordinator {

    private Logger logger = LoggerFactory.getLogger(CompilerIPCCoordinatorImpl.class);
    private static final String placeholder = "<maven_repo>";
    private static final String mavenModuleName = "kie-wb-common-compiler-offprocess-core";
    private static final String classpathFile = "offprocess.classpath.template";
    private String javaHome;
    private String javaBin;
    private String classpathTemplate;
    private ResponseSharedMap responseMap;
    private ClientIPC clientIPC;
    private QueueProvider provider;
    private String queueName;
    private String kieVersion;

    public CompilerIPCCoordinatorImpl(QueueProvider provider) {
        this.kieVersion = getKieVersion();
        this.queueName = provider.getAbsolutePath();
        this.provider = provider;
        responseMap = new ResponseSharedMap();
        clientIPC = new ClientIPCImpl(responseMap, provider);
        javaHome = System.getProperty("java.home");
        javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        try {
            classpathTemplate = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(classpathFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public CompilationResponse compile(CompilationRequest req) {
        return internalBuild(req.getMavenRepo(),
                             req.getInfo().getPrjPath().toAbsolutePath().toString(),
                             getAlternateSettings(req.getOriginalArgs()),  req.getRequestUUID());
    }


    private String getKieVersion(){
        ConfigurationPropertiesStrategy prop = new ConfigurationPropertiesStrategy();
        Map<ConfigurationKey, String> conf = prop.loadConfiguration();
        return conf.get(ConfigurationKey.KIE_VERSION);
    }


    private String getAlternateSettings(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(MavenCLIArgs.ALTERNATE_USER_SETTINGS)) {
                return arg.substring(2);
            }
        }
        return "";
    }

    private CompilationResponse internalBuild(String mavenRepo, String projectPath, String alternateSettingsAbsPath, String uuid) {
        String classpath = classpathTemplate.replace(placeholder, mavenRepo);
        try {
            invokeServerBuild(mavenRepo, projectPath, uuid, classpath, alternateSettingsAbsPath, queueName);
            if(logger.isDebugEnabled()) {
                logger.debug("invokeServerBuild completed");
            }
            return getCompilationResponse(uuid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new DefaultKieCompilationResponse(false, "");
        }
    }

    private CompilationResponse getCompilationResponse(String uuid) {
        KieCompilationResponse res = clientIPC.getResponse(uuid);
        if (res != null) {
            return res;
        } else {
            return new DefaultKieCompilationResponse(true, "");
        }
    }

    private void invokeServerBuild(String mavenRepo, String projectPath, String uuid, String classpath, String alternateSettingsAbsPath, String queueName) throws Exception {
        String[] commandArrayServer =
                {
                        javaBin,
                        "-cp",
                        getClasspathIncludedCurrentModuleDep(mavenRepo, classpath),
                        "-Dorg.uberfire.nio.git.daemon.enabled=false",
                        "-Dorg.uberfire.nio.ssh.daemon.enabled=false",
                        ServerIPCImpl.class.getCanonicalName(),
                        uuid,
                        projectPath,
                        mavenRepo,
                        alternateSettingsAbsPath,
                        queueName
                };
        if (logger.isDebugEnabled()) {
            logger.debug("************************** \n Invoking server in a separate process with args: \n{} \n{} \n{} \n{} \n{} \n{} \n{} \n{} \n**************************", commandArrayServer);
        }
        ProcessBuilder serverPb = new ProcessBuilder(commandArrayServer);
        serverPb.directory(new File(projectPath));
        serverPb.redirectErrorStream(true);
        serverPb.inheritIO();
        writeStdOut(serverPb);
    }

    private String getClasspathIncludedCurrentModuleDep(String mavenRepo, String classpath){
        StringBuilder sb = new StringBuilder();
        this.getClass().getPackage();
        sb.append(mavenRepo).
                append(File.separator).append("org").
                append(File.separator).append("kie").
                append(File.separator).append("workbench").
                append(File.separator).append("services").
                append(File.separator).append(mavenModuleName).
                append(File.separator).append(kieVersion).
                append(File.separator).append(mavenModuleName).
                append("-").append(kieVersion).append(".jar").append(":").
                append(classpath);
        return  sb.toString();
    }

    private void writeStdOut(ProcessBuilder builder) throws Exception {
        Process process = builder.start();
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null && (!line.contains("BUILD SUCCESS") || !line.contains("BUILD FAILURE"))) {
            if (logger.isInfoEnabled()) {
                logger.info(line);
            }
        }
        if (line != null) {
            if (logger.isInfoEnabled()) {
                logger.info(line);
            }
            return;
        }
    }
}
