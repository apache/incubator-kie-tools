/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.kie.workbench.common.services.backend.compiler.AFClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOClassLoaderProviderImpl implements AFClassLoaderProvider {

    protected static final Logger logger = LoggerFactory.getLogger(NIOClassLoaderProviderImpl.class);
    protected final DirectoryStream.Filter<Path> dotFileFilter = new DotFileFilter();
    protected String JAVA_ARCHIVE_RESOURCE_EXT = ".jar";
    protected String FILE_URI = "file://";

    public static void searchCPFiles(Path file,
                                     List<String> classPathFiles,
                                     String... extensions) {
        try {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
                for (Path p : ds) {
                    if (Files.isDirectory(p)) {
                        searchCPFiles(p,
                                      classPathFiles,
                                      extensions);
                    } else if (Stream.of(extensions).anyMatch(p.toString()::endsWith)) {
                        classPathFiles.add(p.toAbsolutePath().toString());
                    }
                }
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
    }

    /**
     * Execute a maven run to create the classloaders with the dependencies in the Poms, transitive inclueded
     */
    public Optional<ClassLoader> getClassloaderFromAllDependencies(String prjPath,
                                                                   String localRepo) {
        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(
                Decorator.NONE);
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(prjPath));
        StringBuilder sb = new StringBuilder(MavenConfig.MAVEN_DEP_PLUGING_OUTPUT_FILE).append(MavenConfig.CLASSPATH_FILENAME).append(MavenConfig.CLASSPATH_EXT);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(localRepo,
                                                                     info,
                                                                     new String[]{MavenConfig.DEPS_BUILD_CLASSPATH, sb.toString()},
                                                                     new HashMap<>(),
                                                                     Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.isSuccessful()) {
            /** Maven dependency plugin is not able to append the modules' classpath using an absolute path in -Dmdep.outputFile,
             it override each time and at the end only the last writted is present in  the file,
             for this reason we use a relative path and then we read each file present in each module to build a unique classpath file
             * */
            Optional<ClassLoader> urlClassLoader = createClassloaderFromCpFiles(prjPath);
            if (urlClassLoader != null) {
                return urlClassLoader;
            }
        }
        return Optional.empty();
    }

    /**
     * Load the dependencies from the Poms
     */
    public Optional<ClassLoader> loadDependenciesClassloaderFromProject(String prjPath,
                                                                        String localRepo) {
        List<String> poms = new ArrayList<>();
        NIOMavenUtils.searchPoms(Paths.get(prjPath),
                                 poms);
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    /**
     * Load the dependencies from the Poms
     */
    public Optional<ClassLoader> loadDependenciesClassloaderFromProject(List<String> poms,
                                                                        String localRepo) {
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    public Optional<ClassLoader> getClassloaderFromProjectTargets(List<String> targets,
                                                                  Boolean loadIntoClassloader) {
        List<URL> urls = loadIntoClassloader ? getTargetModulesURL(targets) : getTargetModulesURL(targets);
        return buildResult(urls);
    }

    private List<URL> buildUrlsFromArtifacts(String localRepo,
                                             List<Artifact> artifacts) throws MalformedURLException {
        List<URL> urls = new ArrayList<>(artifacts.size());
        for (Artifact artifact : artifacts) {
            StringBuilder sb = new StringBuilder("file://");
            sb.append(localRepo).append("/").append(artifact.getGroupId()).
                    append("/").append(artifact.getVersion()).append("/").append(artifact.getArtifactId()).
                    append("-").append(artifact.getVersion()).append(".").append(artifact.getType());
            URL url = new URL(sb.toString());
            urls.add(url);
        }
        return urls;
    }

    private List<URL> getDependenciesURL(List<String> poms,
                                         String localRepo) {
        List<Artifact> artifacts = NIOMavenUtils.resolveDependenciesFromMultimodulePrj(poms);
        List<URL> urls = Collections.emptyList();
        try {
            urls = buildUrlsFromArtifacts(localRepo,
                                          artifacts);
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return urls;
    }

    private List<URL> getTargetModulesURL(List<String> pomsPaths) {
        if (pomsPaths != null && pomsPaths.size() > 0) {
            List<URL> urls = new ArrayList<>();
            try {
                for (String pomPath : pomsPaths) {
                    Path path = Paths.get(pomPath);
                    if (path != null) {
                        StringBuilder sb = new StringBuilder("file://")
                                .append(path.getParent().toAbsolutePath().toString())
                                .append("/target/classes/");
                        urls.add(new URL(sb.toString()));
                    }
                }
            } catch (MalformedURLException ex) {
                logger.error(ex.getMessage());
            }
            return urls;
        } else {
            return Collections.emptyList();
        }
    }

    private Optional<ClassLoader> buildResult(List<URL> urls) {
        if (urls.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    private Optional<ClassLoader> buildResult(List<URL> urls,
                                              ClassLoader parent) {
        if (urls.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]),
                                                               parent);
            return Optional.of(urlClassLoader);
        }
    }

    private Optional<ClassLoader> createClassloaderFromCpFiles(String prjPath) {
        List<URL> deps = readAllCpFilesAsUrls(prjPath,
                                              MavenConfig.CLASSPATH_EXT);
        if (deps.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(deps.toArray(new URL[deps.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    private List<URL> readAllCpFilesAsUrls(String prjPath,
                                           String extension) {
        List<String> classPathFiles = new ArrayList<>();
        searchCPFiles(Paths.get(prjPath),
                      classPathFiles,
                      extension);
        if (!classPathFiles.isEmpty()) {
            List<URL> deps = new ArrayList<>();
            for (String file : classPathFiles) {
                deps.addAll(readFileAsURL(file));
            }
            if (!deps.isEmpty()) {

                return deps;
            }
        }
        return Collections.emptyList();
    }

    private List<URL> loadFiles(List<String> pomsPaths) {
        List<URL> targetModulesUrls = getTargetModulesURL(pomsPaths);
        if (!targetModulesUrls.isEmpty()) {
            List<URL> targetFiles = addFilesURL(targetModulesUrls);
            return targetFiles;
        }
        return Collections.emptyList();
    }

    private List<URI> readFileAsURI(String filePath) {

        BufferedReader br = null;
        List<URI> urls = new ArrayList<>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),
                                                          "UTF-8"));
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(sCurrentLine,
                                                            ":");
                while (token.hasMoreTokens()) {
                    StringBuilder sb = new StringBuilder("file://").append(token.nextToken());
                    urls.add(new URI(sb.toString()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                Files.delete(Paths.get(filePath));
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return urls;
    }

    private List<URL> readFileAsURL(String filePath) {

        BufferedReader br = null;
        List<URL> urls = new ArrayList<>();
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),
                                                          "UTF-8"));
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(sCurrentLine,
                                                            ":");
                while (token.hasMoreTokens()) {
                    StringBuilder sb = new StringBuilder("file://").append(token.nextToken());
                    urls.add(new URL(sb.toString()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                Files.delete(Paths.get(filePath));
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return urls;
    }

    public Optional<List<URI>> getURISFromAllDependencies(String prjPath) {
        List<String> classPathFiles = new ArrayList<>();
        searchCPFiles(Paths.get(prjPath),
                      classPathFiles,
                      MavenConfig.CLASSPATH_EXT,
                      JAVA_ARCHIVE_RESOURCE_EXT);
        if (!classPathFiles.isEmpty()) {
            List<URI> deps = processScannedFiles(classPathFiles);
            if (!deps.isEmpty()) {
                return Optional.of(deps);
            }
        }
        return Optional.empty();
    }

    private List<URI> processScannedFiles(List<String> classPathFiles) {
        List<URI> deps = new ArrayList<>();
        for (String file : classPathFiles) {
            if (file.endsWith(MavenConfig.CLASSPATH_EXT)) {
                //the .cpath will be processed to extract the deps of each module
                deps.addAll(readFileAsURI(file));
            } else if (file.endsWith(JAVA_ARCHIVE_RESOURCE_EXT)) {
                //the jar is added as is with file:// prefix
                deps.add(URI.create(FILE_URI + file));
            }
        }
        return deps;
    }

    private List<URL> addFilesURL(List<URL> targetModulesUrls) {
        List<URL> targetFiles = new ArrayList<>(targetModulesUrls.size());
        for (URL url : targetModulesUrls) {
            try {
                targetFiles.addAll(visitFolders(Files.newDirectoryStream(Paths.get(url.toURI()))));
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
        }
        return targetFiles;
    }

    private List<URL> visitFolders(final DirectoryStream<Path> directoryStream) {
        List<URL> urls = new ArrayList<>();
        try {
            for (final Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    visitFolders(Files.newDirectoryStream(path));
                } else {
                    //Don't process dotFiles
                    if (!dotFileFilter.accept(path)) {
                        try {
                            urls.add(path.toUri().toURL());
                        } catch (MalformedURLException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
        return urls;
    }
}
