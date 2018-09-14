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

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.appformer.maven.integration.Aether;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It reads all the jars present in the WEB-INF/lib
 * to create a Map with entries of GAV and path of the dependency
 */
@WebListener
public class M2ServletContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(M2ServletContextListener.class);

    private static final String JAR_EXT = ".jar";
    private static final String WEB_INF_FOLDER = "WEB-INF";
    private static final String LIB_FOLDER = "lib";
    private static final String JAR_ARTIFACT = "jar";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String JARS_FOLDER = File.separator + WEB_INF_FOLDER + File.separator + LIB_FOLDER + File.separator;
    private static final String MAVEN_META_INF = "META-INF" + File.separator + "maven";
    private final Path tempDir;

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
        logger.info("M2ServletContextListener contextInitialized started");
        ServletContext ctx = servletContextEvent.getServletContext();
        String jarsPath = ctx.getRealPath(JARS_FOLDER);
        long startTime = System.nanoTime();
        int jarsDeployed = deployJarsFromWar(jarsPath);
        long endTime = System.nanoTime();
        long totalTime = TimeUnit.NANOSECONDS.toSeconds(endTime - startTime);
        logger.info("M2ServletContextListener contextInitialized deployed {} jars in {} sec ",
                    jarsDeployed,
                    totalTime);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    private int deployJarsFromWar(final String path) {
        int i = 0;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(path))) {
            RepositorySystemSession session = newSession(newRepositorySystem());
            for (Path p : ds) {
                if (p.toString().endsWith(JAR_EXT)) {
                    deployJar(p.toAbsolutePath().toString(),
                              session);
                    i++;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),
                         e);
        }
        return i;
    }

    public GAV deployJar(final String file,
                         final RepositorySystemSession session) {
        GAV gav = new GAV();
        Properties props = readZipFile(file);
        if (!props.isEmpty()) {
            gav = new GAV(props.getProperty(GROUP_ID),
                          props.getProperty(ARTIFACT_ID),
                          props.getProperty(VERSION));
            deploy(gav,
                   file,
                   session);
        }
        return gav;
    }

    private Properties readZipFile(String zipFilePath) {
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<? extends ZipEntry> e = zipFile.entries();
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
            logger.error("IOError :{}",
                         e.getMessage(),
                         e);
        }
        return new Properties();
    }

    public void deploy(final GAV gav,
                       final String jarPath,
                       final RepositorySystemSession session) {
        Artifact jarArtifact = new DefaultArtifact(gav.getGroupId(),
                                                   gav.getArtifactId(),
                                                   JAR_ARTIFACT,
                                                   gav.getVersion());
        jarArtifact = jarArtifact.setFile(new File(jarPath));

        Artifact pom = null;
        try {
            final ZipFile jarFile = new ZipFile(jarArtifact.getFile());
            Path target = null;
            final Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith("pom.xml")) {
                    final String jarFileName = jarArtifact.getFile().toPath().getFileName().toString();
                    Path pomDir = Files.createDirectory(tempDir.resolve(jarFileName));
                    target = Files.createFile(pomDir.resolve(jarFileName.substring(0,
                                                                                   jarFileName.length() - 4) + ".pom"));
                    InputStream stream = jarFile.getInputStream(entry);
                    java.nio.file.Files.copy(stream,
                                             target,
                                             StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
            }
            pom = new SubArtifact(jarArtifact,
                                  null,
                                  "pom").setFile(target.toFile());
        } catch (Exception ex) {
            pom = null;
            logger.error(ex.getMessage(),
                         ex);
        }

        try {
            final InstallRequest installRequest = new InstallRequest();
            installRequest.addArtifact(jarArtifact);
            if (pom != null) {
                installRequest.addArtifact(pom);
            }
            InstallResult result = Aether.getAether().getSystem().install(session,
                                                                          installRequest);
        } catch (InstallationException e) {
            logger.error(e.getMessage(),
                         e);
        }
    }

    private RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(ArtifactRepositoryPreference.getGlobalM2RepoDirWithFallback());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session,
                                                                           localRepo));
        return session;
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class,
                           BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class,
                           FileTransporterFactory.class);
        locator.addService(TransporterFactory.class,
                           HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }
}
