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
package org.kie.workbench.common.services.backend.compiler.impl.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.drools.core.util.IoUtils;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.CommonConstants;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

/**
 * Utils to parse files to create Classloaders
 */
public class CompilerClassloaderUtils {

    protected static final Logger logger = LoggerFactory.getLogger(CompilerClassloaderUtils.class);

    private CompilerClassloaderUtils() {
    }

    /***
     * It run a maven build-classpath in memory and return a classloader with only this deps
     * @param prjPath
     * @param localRepo
     * @return
     */
    public static Optional<ClassLoader> getClassloaderFromAllDependencies(String prjPath,
                                                                          String localRepo,
                                                                          String settingsXmlPath) {

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_BUILD_CLASSPATH));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(URI.create(CommonConstants.FILE_URI + prjPath)));
        CompilationRequest req;
        if (settingsXmlPath != null) {
            req = new DefaultCompilationRequest(localRepo,
                                                info,
                                                new String[]{MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH, MavenCLIArgs.ALTERNATE_USER_SETTINGS + settingsXmlPath},
                                                Boolean.FALSE);
        } else {
            req = new DefaultCompilationRequest(localRepo,
                                                info,
                                                new String[]{MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH},
                                                Boolean.FALSE);
        }
        CompilationResponse res = compiler.compile(req);
        if (res.isSuccessful()) {
            /** Maven dependency plugin is not able to append the modules classpath using an absolute path in -Dmdep.outputFile,
             it override each time and at the end only the last writted is present in  the file,
             for this reason we use a relative path and then we read each file present in each module to build a unique classpath file
             * */
            if (!res.getDependencies().isEmpty()) {
                Optional<ClassLoader> urlClassLoader = CompilerClassloaderUtils.createClassloaderFromStringDeps(res.getDependencies());
                if (urlClassLoader != null) {
                    return urlClassLoader;
                }
            }
        }
        return Optional.empty();
    }

    public static Map<String, byte[]> getMapClasses(String path,
                                                    Map<String, byte[]> store) {

        final List<String> keys = IoUtils.recursiveListFile(new File(path),
                                                            "",
                                                            filterClasses());
        final Map<String, byte[]> classes = new HashMap<>(keys.size() + store.size());

        for (String item : keys) {
            byte[] bytez = getBytes(path + CommonConstants.SEPARATOR + item);
            String fqn = item.substring(item.lastIndexOf(CommonConstants.MAVEN_TARGET) + CommonConstants.MAVEN_TARGET.length());
            classes.put(fqn,
                        bytez);
        }
        if (!store.isEmpty()) {
            for (Map.Entry<String, byte[]> entry : store.entrySet()) {
                classes.put(entry.getKey(),
                            entry.getValue());
            }
        }
        return classes;
    }

    public static Predicate<File> filterClasses() {
        return f -> f.toString().contains(CommonConstants.MAVEN_TARGET) &&
                !f.toString().contains(CommonConstants.META_INF) &&
                !FilenameUtils.getName(f.toString()).startsWith(CommonConstants.DOT);
    }

    public static Optional<ClassLoader> loadDependenciesClassloaderFromProject(String prjPath,
                                                                               String localRepo) {
        List<String> poms =
                MavenUtils.searchPoms(Paths.get(URI.create(CommonConstants.FILE_URI + prjPath)));
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    public static Optional<ClassLoader> loadDependenciesClassloaderFromProject(List<String> poms,
                                                                               String localRepo) {
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    public static Optional<ClassLoader> getClassloaderFromProjectTargets(List<String> pomsPaths) {
        List<URL> urls = getTargetModulesURL(pomsPaths);
        return buildResult(urls);
    }

    public static List<URL> buildUrlsFromArtifacts(String localRepo,
                                                   List<Artifact> artifacts) throws MalformedURLException {
        List<URL> urls = new ArrayList<>(artifacts.size());
        for (Artifact artifact : artifacts) {
            StringBuilder sb = new StringBuilder(CommonConstants.FILE_URI);
            sb.append(localRepo).append(CommonConstants.SEPARATOR).append(artifact.getGroupId()).
                    append(CommonConstants.SEPARATOR).append(artifact.getVersion()).append(CommonConstants.SEPARATOR).append(artifact.getArtifactId()).
                    append("-").append(artifact.getVersion()).append(CommonConstants.DOT).append(artifact.getType());
            URL url = new URL(sb.toString());
            urls.add(url);
        }
        return urls;
    }

    public static List<URL> getDependenciesURL(List<String> poms,
                                               String localRepo) {
        List<Artifact> artifacts = MavenUtils.resolveDependenciesFromMultimodulePrj(poms);
        List<URL> urls = Collections.emptyList();
        try {
            urls = buildUrlsFromArtifacts(localRepo,
                                          artifacts);
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return urls;
    }

    public static List<URL> getTargetModulesURL(List<String> pomsPaths) {
        if (pomsPaths != null && pomsPaths.size() > 0) {
            List<URL> targetModulesUrls = new ArrayList(pomsPaths.size());
            try {
                for (String pomPath : pomsPaths) {
                    Path path = Paths.get(URI.create(CommonConstants.FILE_URI + pomPath));
                    StringBuilder sb = new StringBuilder(CommonConstants.FILE_URI)
                            .append(path.getParent().toAbsolutePath().toString())
                            .append(CommonConstants.SEPARATOR).append(CommonConstants.MAVEN_TARGET);
                    targetModulesUrls.add(new URL(sb.toString()));
                }
            } catch (MalformedURLException ex) {
                logger.error(ex.getMessage());
            }
            return targetModulesUrls;
        } else {
            return Collections.emptyList();
        }
    }

    private static Optional<ClassLoader> buildResult(List<URL> urls) {
        if (urls.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    public static Optional<ClassLoader> createClassloaderFromStringDeps(List<String> depsProject) {
        List<URL> deps = readAllDepsAsUrls(depsProject);
        if (deps.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(deps.toArray(new URL[deps.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    public static List<URL> readAllDepsAsUrls(List<String> prjDeps) {
        List<URL> deps = new ArrayList<>();
        for (String dep : prjDeps) {
            try {
                deps.add(new URL(dep));
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());
            }
        }
        return deps;
    }

    public static List<URI> readAllDepsAsUris(List<String> prjDeps) {
        List<URI> deps = new ArrayList<>();
        for (String dep : prjDeps) {
            try {
                deps.add(new URI(dep));
            } catch (URISyntaxException e) {
                logger.error(e.getMessage());
            }
        }
        return deps;
    }

    public static List<String> readItemsFromClasspathString(Set<String> depsModules) {

        Set<String> items = new HashSet<>();
        Iterator<String> iter = depsModules.iterator();
        while (iter.hasNext()) {
            StringTokenizer token = new StringTokenizer(iter.next());
            while (token.hasMoreElements()) {
                String item = token.nextToken(":");
                if (item.endsWith(CommonConstants.JAVA_ARCHIVE_RESOURCE_EXT)) {
                    StringBuilder sb = new StringBuilder(CommonConstants.FILE).append(item);
                    items.add(sb.toString());
                }
            }
        }
        return new ArrayList<>(items);
    }

    public static List<String> getStringFromTargets(Path prjPath) {
        return getStringFromTargetWithStream(prjPath,
                                             CommonConstants.JAVA_CLASS_EXT,
                                             CommonConstants.DROOLS_EXT,
                                             CommonConstants.GDROOLS_EXT,
                                             CommonConstants.RDROOLS_EXT,
                                             CommonConstants.XML_EXT,
                                             CommonConstants.SCENARIO_EXT);
    }

    public static List<String> getStringFromTargetWithStream(Path pathIn,
                                                             String... extensions) {
        java.nio.file.Path prjPath = java.nio.file.Paths.get(pathIn.toAbsolutePath().toString());
        List<String> joined = Collections.emptyList();
        try (Stream<java.nio.file.Path> stream = java.nio.file.Files.walk(prjPath)) {
            joined = stream
                    .map(String::valueOf)
                    .filter(path -> Stream.of(extensions).anyMatch(path::endsWith) && path.contains(CommonConstants.MAVEN_TARGET))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            logger.error(ex.getMessage(),
                         ex);
        }
        return joined;
    }

    public static List<URI> processScannedFilesAsURIs(List<String> classPathFiles) {
        List<URI> deps = new ArrayList<>();
        for (String file : classPathFiles) {
            if (FilenameUtils.getName(file).startsWith(".")) {
                continue;
            }
            if (file.endsWith(CommonConstants.JAVA_ARCHIVE_RESOURCE_EXT)) {
                //the jar is added as is with file:// prefix
                if (file.startsWith(CommonConstants.FILE_URI)) {
                    deps.add(URI.create(file));
                } else {
                    deps.add(URI.create(CommonConstants.FILE_URI + file));
                }
            }
        }
        return deps;
    }

    public static List<URL> processScannedFilesAsURLs(List<String> classPathFiles) {
        List<URL> deps = new ArrayList<>();
        try {
            for (String file : classPathFiles) {
                if (FilenameUtils.getName(file).startsWith(CommonConstants.DOT)) {
                    continue;
                }
                if (file.endsWith(CommonConstants.JAVA_ARCHIVE_RESOURCE_EXT)) {
                    //the jar/class is added as is with file:// prefix
                    if (file.startsWith(CommonConstants.FILE_URI)) {
                        deps.add(new URL(file));
                    } else {
                        deps.add(new URL(CommonConstants.FILE_URI + file));
                    }
                }
            }
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return deps;
    }

    private static byte[] getBytes(String pResourceName) {
        try {
            File resource = new File(pResourceName);
            return resource.exists() ? IoUtils.readBytesFromInputStream(new FileInputStream(pResourceName)) : null;
        } catch (IOException e) {
            throw new RuntimeException("Unable to get bytes for: " + new File(pResourceName) + " " + e.getMessage());
        }
    }

    public static Set<String> filterPathClasses(Collection<String> paths,
                                                String mavenRepoPath) {
        return paths.stream().collect(new FilterPathClassesCollector(mavenRepoPath));
    }

    public static List<String> filterClassesByPackage(Collection<String> items,
                                                      String packageName) {
        return items.stream().collect(new FilterClassesByPackageCollector(packageName));
    }

    public static Class<?> getClass(String pkgName,
                                    String className,
                                    ClassLoader classloader) {
        try {
            String input;
            if (pkgName != null && pkgName.trim().length() != 0) {
                input = className;
            } else {
                return null;
            }
            Class<?> clazz = classloader.loadClass(input);
            return clazz;
        } catch (ClassNotFoundException var4) {
            return null;
        }
    }
}