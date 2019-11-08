/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.m2repo.utils.FileNameUtilities.isDeployedPom;
import static org.guvnor.m2repo.utils.FileNameUtilities.isJar;
import static org.guvnor.m2repo.utils.FileNameUtilities.isKJar;

@ApplicationScoped
public class GuvnorM2Repository {

    private static final Logger log = LoggerFactory.getLogger(GuvnorM2Repository.class);

    private static final int BUFFER_SIZE = 1024;
    protected static final String META_INF = "META-INF";
    private static final String MAVEN = "maven";
    private static final String META_INF_MAVEN = META_INF + "/" + MAVEN;
    private static final String POM = "pom";
    private static final String POM_XML = POM + ".xml";
    private static final String POM_PROPERTIES = POM + ".properties";

    private static final String GROUP_ID_NAME = "groupId";
    private static final String ARTIFACT_ID_NAME = "artifactId";
    private static final String VERSION_NAME = "version";
    protected static final String KMODULE_XML = "kmodule.xml";
    protected static final String KIE_DEPLOYMENT_DESCRIPTOR_XML = "kie-deployment-descriptor.xml";

    private final List<ArtifactRepository> repositories = new ArrayList<>();
    private final List<ArtifactRepository> pomRepositories = new ArrayList<>();
    private ArtifactRepositoryService artifactRepositoryFactory;

    public GuvnorM2Repository() {
    }

    @Inject
    public GuvnorM2Repository(ArtifactRepositoryService factory) {
        this.artifactRepositoryFactory = factory;
    }

    @PostConstruct
    public void init() {
        setM2Repos();
    }

    private void setM2Repos() {

        this.repositories.addAll(this.artifactRepositoryFactory.getRepositories());
        this.pomRepositories.addAll(this.artifactRepositoryFactory.getPomRepositories());
    }

    public String getM2RepositoryDir(String repositoryName) {
        return this.getM2RepositoryRootDir(repositoryName).replaceAll(Matcher.quoteReplacement(File.separator)+"$",
                                                                      "");
    }

    public String getM2RepositoryRootDir(String repositoryName) {

        String defaultName = ArtifactRepositoryService.GLOBAL_M2_REPO_NAME;
        if (repositoryName != null && !repositoryName.isEmpty()) {
            defaultName = repositoryName;
        }

        final String name = defaultName;

        ArtifactRepository repository = getArtifactRepository(name);

        String rootDir = repository.getRootDir();
        if (!repository.getRootDir().endsWith(File.separator)) {
            return rootDir + File.separator;
        } else {
            return rootDir;
        }
    }

    private ArtifactRepository getArtifactRepository(String name) {
        return this.repositories
                .stream()
                .filter(artifactRepository -> artifactRepository.getName().equals(name))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format("Repository %s not found",
                                                                                  name)));
    }

    public String getRepositoryURL(String repositoryName) {
        File file = new File(getM2RepositoryRootDir(repositoryName));
        return "file://" + file.getAbsolutePath();
    }
    
    public void deployArtifact(final InputStream jarStream,
                               final GAV gav,
                               final boolean includeAdditionalRepositories) {
        deployArtifact(jarStream, gav, includeAdditionalRepositories, (repo) -> true);
    }

    public void deployArtifact(final InputStream jarStream,
                               final GAV gav,
                               final boolean includeAdditionalRepositories,
                               final Predicate<ArtifactRepository> filter) {
        //Write JAR to temporary file for deployment
        File jarFile = new File(System.getProperty("java.io.tmpdir"),
                                toFileName(gav,
                                           "jar"));

        try {

            inputStreamToFile(jarStream,
                              jarFile);

            //Write pom.xml to JAR if it doesn't already exist
            String pomXML = loadPomFromJar(new File(jarFile.getPath()));
            if (pomXML == null) {
                pomXML = generatePOM(gav);
                jarFile = appendFileToJar(pomXML,
                                          getPomXmlPath(gav),
                                          jarFile.getPath());
            }

            //Write pom.properties to JAR if it doesn't already exist
            String pomProperties = loadGAVFromJarInternal(new File(jarFile.getPath()));
            if (pomProperties == null) {
                pomProperties = generatePomProperties(gav);
                jarFile = appendFileToJar(pomProperties,
                                          getPomPropertiesPath(gav),
                                          jarFile.getPath());
            }

            deployArtifact(gav,
                           pomXML,
                           jarFile,
                           includeAdditionalRepositories,
                           filter);
        } finally {
            try {
                jarFile.delete();
            } catch (Exception e) {
                log.warn("Unable to remove temporary file '" + jarFile.getAbsolutePath() + "'");
            }
        }
    }

    public void deployPom(final InputStream pomStream,
                          final GAV gav) {
        //Write POM to temporary file for deployment
        File pomFile = new File(System.getProperty("java.io.tmpdir"), toFileName(gav, POM));

        try {

            inputStreamToFile(pomStream,
                              pomFile);

            deployPom(gav,
                      pomFile);
        } finally {
            try {
                pomFile.delete();
            } catch (Exception e) {
                log.warn("Unable to remove temporary file '" + pomFile.getAbsolutePath() + "'");
            }
        }
    }

    private void inputStreamToFile(final InputStream inputStream,
                                   final File file) {

        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);

            final byte[] buf = new byte[BUFFER_SIZE];
            int byteRead = 0;
            while ((byteRead = inputStream.read(buf)) != -1) {
                fos.write(buf,
                          0,
                          byteRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    log.error("Error occurred when trying to close stream",
                              e);
                }
            }
        }
    }

    public void deployParentPom(final GAV gav) {
        //Write pom.xml to temporary file for deployment
        final File pomXMLFile = new File(System.getProperty("java.io.tmpdir"),
                                         toFileName(gav,
                                                    POM_XML));

        try {

            String pomXML = generateParentPOM(gav);
            writeStringIntoFile(pomXML,
                                pomXMLFile);
            //pom.xml Artifact
            Artifact pomXMLArtifact = new DefaultArtifact(gav.getGroupId(),
                                                          gav.getArtifactId(),
                                                          POM,
                                                          gav.getVersion());
            pomXMLArtifact = pomXMLArtifact.setFile(pomXMLFile);

            final Artifact finalPomXMLArtifact = pomXMLArtifact;
            this.pomRepositories.forEach(artifactRepository -> {
                artifactRepository.deploy(pomXML,
                                          finalPomXMLArtifact);
            });
        } finally {
            try {
                pomXMLFile.delete();
            } catch (Exception e) {
                log.warn("Unable to remove temporary file '" + pomXMLFile.getAbsolutePath() + "'");
            }
        }
    }

    private void deployArtifact(final GAV gav,
                                final String pomXML,
                                final File jarFile,
                                final boolean includeAdditionalRepositories,
                                final Predicate<ArtifactRepository> filter) {
        //Write pom.xml to temporary file for deployment
        final File pomXMLFile = new File(System.getProperty("java.io.tmpdir"), toFileName(gav, POM_XML));

        try {

            writeStringIntoFile(pomXML,
                                pomXMLFile);

            //JAR Artifact
            Artifact jarArtifact = new DefaultArtifact(gav.getGroupId(),
                                                       gav.getArtifactId(),
                                                       "jar",
                                                       gav.getVersion());
            jarArtifact = jarArtifact.setFile(jarFile);

            //pom.xml Artifact
            Artifact pomXMLArtifact = new SubArtifact(jarArtifact, "", POM);
            pomXMLArtifact = pomXMLArtifact.setFile(pomXMLFile);

            final Artifact finalJarArtifact = jarArtifact;
            final Artifact finalPomXMLArtifact = pomXMLArtifact;
            this.repositories.stream().filter(filter).forEach((repository) -> repository.deploy(pomXML,
                                                                        finalJarArtifact,
                                                                        finalPomXMLArtifact));

            //Only deploy to additional repositories if required. This flag is principally for Unit Tests
            if (!includeAdditionalRepositories) {
                return;
            }
        } finally {
            try {
                pomXMLFile.delete();
            } catch (Exception e) {
                log.warn("Unable to remove temporary file '" + pomXMLFile.getAbsolutePath() + "'");
            }
        }
    }

    private void writeStringIntoFile(final String string,
                                     final File file) {
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            IOUtils.write(string,
                          fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    log.error("Error occurred trying to close a stream",
                              e);
                }
            }
        }
    }

    private void deployPom(final GAV gav,
                           final File pomFile) {
        //POM Artifact
        Artifact pomArtifact = new DefaultArtifact(gav.getGroupId(),
                                                   gav.getArtifactId(),
                                                   POM,
                                                   gav.getVersion());
        pomArtifact = pomArtifact.setFile(pomFile);

        final Artifact finalPomArtifact = pomArtifact;
        this.pomRepositories.forEach(artifactRepository -> {
            artifactRepository.deploy(null,
                                      finalPomArtifact);
        });
    }

    /**
     * Finds files within the repository.
     * @return an collection of java.io.File with the matching files
     */
    public Collection<File> listFiles() {
        return listFiles(null);
    }

    /**
     * Finds files within the repository with the given filters.
     * @param filters filter to apply when finding files. The filter is used to create a wildcard matcher, ie., "*filter*.*", in which "*" is
     * to represent a multiple wildcard characters.
     * @return an collection of java.io.File with the matching files
     */
    public List<File> listFiles(final String filters) {
        return listFiles(filters,
                         null);
    }

    /**
     * Finds files within the repository with the given filters and formats.
     * @param filters filter to apply when finding files. The filter is used to create a wildcard matcher, ie., "*filter*.*", in which "*" is
     * to represent a multiple wildcard characters.
     * @param fileFormats file formats to apply when finding files, ie., [ "jar", "kjar" ].
     * @return an collection of java.io.File with the matching files
     */
    public List<File> listFiles(final String filters,
                                List<String> fileFormats) {
        final List<String> wildcards = new ArrayList<String>();
        String wildcardPrefix = "";

        if (filters != null) {
            wildcardPrefix = "*" + filters;
        }

        if (fileFormats == null) {
            fileFormats = new ArrayList<String>();
            fileFormats.add("jar");
            fileFormats.add("kjar");
            fileFormats.add(POM);
        }

        for (String fileFormat : fileFormats) {
            wildcards.add(wildcardPrefix + "*." + fileFormat);
        }

        final List<File> files = new ArrayList<File>(getFiles(wildcards));

        return files;
    }

    public List<Artifact> listArtifacts(final String filters,
                                        List<String> fileFormats) {
        final List<String> wildcards = new ArrayList<String>();
        String wildcardPrefix = "";

        if (filters != null) {
            wildcardPrefix = "*" + filters;
        }

        if (fileFormats == null) {
            fileFormats = new ArrayList<String>();
            fileFormats.add("jar");
            fileFormats.add("kjar");
            fileFormats.add(POM);
        }

        for (String fileFormat : fileFormats) {
            wildcards.add(wildcardPrefix + "*." + fileFormat);
        }

        final List<Artifact> files = new ArrayList<>(getArtifacts(wildcards));

        return files;
    }

    protected Collection<File> getFiles(final List<String> wildcards) {
        return this.repositories.stream()
                .flatMap(artifactRepository -> artifactRepository.listFiles(wildcards).stream())
                .collect(Collectors.toList());
    }

    protected Collection<Artifact> getArtifacts(final List<String> wildcards) {
        return this.repositories.stream()
                .flatMap(artifactRepository -> artifactRepository.listArtifacts(wildcards).stream())
                .collect(Collectors.toList());
    }

    public String getPomText(final String path) {
        ArtifactRepository repository = this.getArtifactRepository(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
        final File file = new File(repository.getRootDir(),
                                   path);

        final String normalizedPath = file.toPath().normalize().toString();
        if (isJar(normalizedPath) || isKJar(normalizedPath)) {
            return loadPomFromJar(file);
        } else if (isDeployedPom(normalizedPath)) {
            return loadPom(file);
        } else {
            throw new RuntimeException("Not a valid jar, kjar or pom file: " + path);
        }
    }

    public String getKModuleText(final String path) {
        return loadKieTextFromJar(path, META_INF, KMODULE_XML);
    }

    public String getKieDeploymentDescriptorText(final String path) {
        return loadKieTextFromJar(path, META_INF, KIE_DEPLOYMENT_DESCRIPTOR_XML);
    }

    protected String loadKieTextFromJar(final String jarFilePath, final String subPath, final String fileName) {
        ArtifactRepository repository = this.getArtifactRepository(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
        final File file = new File(repository.getRootDir(),
                                   jarFilePath);


        final String normalizedPath = file.toPath().normalize().toString();
        if (isJar(normalizedPath) || isKJar(normalizedPath)) {
            return loadFileTextFromJar(file, subPath, fileName);
        }
        return null;
    }

    private static String loadPomFromJar(final File file) {
        return loadFileTextFromJar(file, META_INF_MAVEN, POM_XML);
    }

    protected static String loadFileTextFromJar(final File jarFile, String path, String fileName) {
        if (jarFile != null && fileName != null && !fileName.isEmpty()) {
            try (ZipFile zip = new ZipFile(jarFile)) {
                for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    if (entry.getName().startsWith(path) && entry.getName().endsWith(fileName)) {
                        return zipEntryToString(zip,
                                                entry);
                    }
                }
            } catch (ZipException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    private static String loadPom(final File file) {
        try (InputStream is = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (int c = isr.read(); c != -1; c = isr.read()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public GAV loadGAVFromJar(final String jarPath) {
        ArtifactRepository repository = this.getArtifactRepository(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
        File zip = new File(repository.getRootDir(),
                            jarPath);

        try {
            final String pomProperties = loadGAVFromJarInternal(zip);

            final Properties props = new Properties();
            props.load(new StringReader(pomProperties));

            final String groupId = props.getProperty(GROUP_ID_NAME);
            final String artifactId = props.getProperty(ARTIFACT_ID_NAME);
            final String version = props.getProperty(VERSION_NAME);

            return new GAV(groupId,
                           artifactId,
                           version);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private String loadGAVFromJarInternal(final File file) {
        try {
            ZipFile zip = new ZipFile(file);

            for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();

                if (entry.getName().startsWith(META_INF_MAVEN) && entry.getName().endsWith(POM_PROPERTIES)) {
                    return zipEntryToString(zip,
                                            entry);
                }
            }
        } catch (ZipException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public static String loadPomFromJar(final InputStream jarInputStream) {
        try {

            InputStream is = getInputStreamFromJar(jarInputStream, META_INF_MAVEN, POM_XML);
            StringBuilder sb = new StringBuilder();
            for (int c = is.read(); c != -1; c = is.read()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public static String loadPomPropertiesFromJar(final InputStream jarInputStream) {
        try {

            InputStream is = getInputStreamFromJar(jarInputStream, META_INF_MAVEN, POM_PROPERTIES);
            StringBuilder sb = new StringBuilder();
            for (int c = is.read(); c != -1; c = is.read()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private static InputStream getInputStreamFromJar(final InputStream jarInputStream,
                                                     final String prefix,
                                                     final String suffix) throws IOException {
        ZipInputStream zis = new ZipInputStream(jarInputStream);
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            final String entryName = entry.getName();
            if (entryName.startsWith(prefix) && entryName.endsWith(suffix)) {
                return new ReaderInputStream(new InputStreamReader(zis, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            }
        }

        throw new FileNotFoundException("Could not find '" + prefix + "/*/" + suffix + "' in the jar.");
    }

    private File appendFileToJar(final String content,
                                 final String contentPath,
                                 final String jarPath) {
        File originalJarFile = new File(jarPath);
        File appendedJarFile = new File(jarPath + ".tmp");

        try (ZipFile war = new ZipFile(originalJarFile);
             ZipOutputStream append = new ZipOutputStream(new FileOutputStream(appendedJarFile))) {

            // first, copy contents from existing war
            copyEntriesFromExistingWar(war,
                                       append);

            // append pom.xml
            ZipEntry e = new ZipEntry(contentPath);
            append.putNextEntry(e);
            append.write(content.getBytes());
            append.closeEntry();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return appendedJarFile;
    }

    private void copyEntriesFromExistingWar(final ZipFile war,
                                            final ZipOutputStream append) throws IOException {
        Enumeration<? extends ZipEntry> entries = war.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            append.putNextEntry(e);
            if (!e.isDirectory()) {
                IOUtil.copy(war.getInputStream(e),
                            append);
            }
            append.closeEntry();
        }
    }

    protected String toFileName(final GAV gav,
                                final String fileName) {
        return gav.getGroupId() + "-" + gav.getArtifactId() + "-" + gav.getVersion() + "-" + Math.random() + "." + fileName;
    }

    public String generatePOM(final GAV gav) {
        Model model = new Model();
        model.setGroupId(gav.getGroupId());
        model.setArtifactId(gav.getArtifactId());
        model.setVersion(gav.getVersion());
        model.setModelVersion("4.0.0");

        StringWriter stringWriter = new StringWriter();
        try {
            new MavenXpp3Writer().write(stringWriter,
                                        model);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return stringWriter.toString();
    }

    public static String generatePomProperties(final GAV gav) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(GROUP_ID_NAME + "=");
        sBuilder.append(gav.getGroupId());
        sBuilder.append("\n");

        sBuilder.append(ARTIFACT_ID_NAME + "=");
        sBuilder.append(gav.getArtifactId());
        sBuilder.append("\n");

        sBuilder.append(VERSION_NAME + "=");
        sBuilder.append(gav.getVersion());
        sBuilder.append("\n");

        return sBuilder.toString();
    }

    public String getPomXmlPath(final GAV gav) {
        return META_INF_MAVEN + "/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/" + POM_XML;
    }

    public String getPomPropertiesPath(final GAV gav) {
        return META_INF_MAVEN + "/" + gav.getGroupId() + "/" + gav.getArtifactId() + "/" + POM_PROPERTIES;
    }

    public String generateParentPOM(final GAV gav) {
        Model model = new Model();
        model.setGroupId(gav.getGroupId());
        model.setArtifactId(gav.getArtifactId());
        model.setVersion(gav.getVersion());
        model.setPackaging(POM);
        model.setModelVersion("4.0.0");

        StringWriter stringWriter = new StringWriter();
        try {
            new MavenXpp3Writer().write(stringWriter,
                                        model);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return stringWriter.toString();
    }

    public boolean containsArtifact(final GAV gav) {
        return containsArtifact(gav, (repo) -> true);
    }
    
    public boolean containsArtifact(final GAV gav,
                                    final Predicate<ArtifactRepository> filter) {
        return this.repositories.stream()
                .filter(filter)
                .anyMatch(artifactRepository -> artifactRepository.containsArtifact(gav));
    }

    public File getArtifactFileFromRepository(final GAV gav) {
        final List<File> artifacts = this.repositories
                .stream()
                .map(artifactRepository -> artifactRepository.getArtifactFileFromRepository(gav))
                .filter(artifact -> artifact != null)
                .collect(Collectors.toList());
        return artifacts.get(0);
    }

    protected static String zipEntryToString(ZipFile zip,
                                             ZipEntry entry) throws IOException {
        final InputStream is = zip.getInputStream(entry);
        final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (int c = isr.read(); c != -1; c = isr.read()) {
            sb.append((char) c);
        }
        return sb.toString();
    }
}
