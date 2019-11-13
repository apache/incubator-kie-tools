/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.m2repo.backend.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.appformer.maven.integration.Aether;
import org.appformer.maven.integration.MavenRepository;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.guvnor.common.services.project.model.GAV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It reads all the jars present in the WEB-INF/lib
 * to create a Map with entries of GAV and path of the dependency
 */
@WebListener
public class M2ServletContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(M2ServletContextListener.class);

    private static final String FORCE_OFFLINE = "kie.maven.offline.force";

    static final String GROUP_ID = "groupId";
    static final String ARTIFACT_ID = "artifactId";
    static final String VERSION = "version";
    private static final String JAR_EXT = ".jar";
    private static final String WEB_INF_FOLDER = "WEB-INF";
    private static final String LIB_FOLDER = "lib";
    private static final String JAR_ARTIFACT = "jar";
    private static final String JARS_FOLDER = File.separator + WEB_INF_FOLDER + File.separator + LIB_FOLDER + File.separator;
    private static final String MAVEN_META_INF = "META-INF/maven";
    private final Path tempDir;
    private final FixNotMavenizedArtifactInfo fixNotMavenizedArtifact = new FixNotMavenizedArtifactInfo();

    public M2ServletContextListener() {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("pom-extract");
        } catch (IOException e) {
            logger.error(e.getMessage(),
                         e);
        }
        this.tempDir = tempDir;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (Boolean.valueOf(System.getProperty(FORCE_OFFLINE, "false"))) {
            logger.info("M2ServletContextListener contextInitialized started");
            final ServletContext ctx = servletContextEvent.getServletContext();
            final String jarsPath = ctx.getRealPath(JARS_FOLDER);
            final long startTime = System.nanoTime();
            final int jarsDeployed = deployJarsFromWar(jarsPath);
            final long endTime = System.nanoTime();
            final long totalTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
            logger.info("M2ServletContextListener contextInitialized deployed {} jars in {} sec ",
                        jarsDeployed,
                        totalTime);
        } else {
            logger.debug("M2ServletContextListener not executed, offline `{}` options set to false.", FORCE_OFFLINE);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    int deployJarsFromWar(final String path) {
        int i = 0;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(path))) {
            for (Path p : ds) {
                if (p.toString().endsWith(JAR_EXT)) {
                    deployJar(p.toAbsolutePath().toString());
                    i++;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return i;
    }

    GAV deployJar(final String file) {
        GAV gav = new GAV();
        Properties props = readZipFile(file);
        if (!props.isEmpty()) {
            gav = new GAV(props.getProperty(GROUP_ID),
                          props.getProperty(ARTIFACT_ID),
                          props.getProperty(VERSION));
            deploy(gav, file);
        }
        return gav;
    }

    private Properties readZipFile(String zipFilePath) {
        try {
            final ZipFile zipFile = new ZipFile(zipFilePath);
            final Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                if (!entry.isDirectory()
                        && entry.getName().startsWith(MAVEN_META_INF)
                        && entry.getName().endsWith("pom.properties")) {
                    try (BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry))) {
                        Properties props = new Properties();
                        props.load(bis);
                        return props;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOError :{}", e.getMessage(), e);
        }

        return fixNotMavenizedArtifact.getProperties(zipFilePath);
    }

    public void deploy(final GAV gav,
                       final String jarPath) {

        final Artifact artifact = MavenRepository.getMavenRepository().resolveArtifact(new AFReleaseIdImpl(gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));

        if (artifact != null) {
            logger.info("Maven Artifact {} already exists in local Maven repository.", gav.toString());
            return;
        }

        logger.warn("Maven Artifact {} deployed from WEB-INF.", gav.toString());

        Artifact jarArtifact = new DefaultArtifact(gav.getGroupId(),
                                                   gav.getArtifactId(),
                                                   JAR_ARTIFACT,
                                                   gav.getVersion());
        final File jarFile = new File(jarPath);
        jarArtifact = jarArtifact.setFile(jarFile);

        Artifact pom = null;
        try {
            final ZipFile jarZipFile = new ZipFile(jarArtifact.getFile());
            boolean foundPom = false;
            Path target = null;
            final Enumeration<? extends ZipEntry> entries = jarZipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith("pom.xml")) {
                    foundPom = true;
                    final String jarFileName = jarArtifact.getFile().toPath().getFileName().toString();
                    Path pomDir = Files.createDirectory(tempDir.resolve(jarFileName));
                    target = Files.createFile(pomDir.resolve(jarFileName.substring(0,
                                                                                   jarFileName.length() - 4) + ".pom"));
                    InputStream stream = jarZipFile.getInputStream(entry);
                    java.nio.file.Files.copy(stream,
                                             target,
                                             StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
            if (!foundPom) {
                final String pomFileName = jarFile.toPath().getFileName().toString().replace(".jar", ".pom");
                final String result = fixNotMavenizedArtifact.buildPom(gav);
                target = Files.createFile(tempDir.resolve(pomFileName));
                java.nio.file.Files.write(target, result.getBytes());
            }
            pom = new SubArtifact(jarArtifact, null, "pom").setFile(target.toFile());
        } catch (final Exception ex) {
            pom = null;
            logger.error(ex.getMessage(), ex);
        }

        try {
            final InstallRequest installRequest = new InstallRequest();
            installRequest.addArtifact(jarArtifact);
            if (pom != null) {
                installRequest.addArtifact(pom);
            }

            Aether.getAether().getSystem().install(Aether.getAether().getSession(), installRequest);
        } catch (InstallationException e) {
            logger.error(e.getMessage(), e);
        }
    }
}