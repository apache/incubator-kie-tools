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
package org.kie.workbench.common.services.backend.maven.plugins.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.dependency.fromDependencies.BuildClasspathMojo;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

/**
 * This goal will output a classpath string of dependencies from the local repository to a memory map.
 * Original version:
 * https://github.com/apache/maven-plugins/blob/trunk/maven-dependency-plugin/src/main/java/org/apache/maven/plugins/dependency/fromDependencies/BuildClasspathMojo.java
 * IMPORTANT: Preserve the structure for an easy update when the maven version will be updated
 */
// CHECKSTYLE_OFF: LineLength
@Mojo(name = "build-classpath", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
// CHECKSTYLE_ON: LineLength
public class BuildInMemoryClasspathMojo
        extends BuildClasspathMojo {

    /**
     * Key used to share the string classpath in the kieMap
     */
    private final String STRING_CLASSPATH_KEY = "stringClasspathKey";

    @Parameter(property = "mdep.fileSeparator", defaultValue = "")
    private String fileSeparator;

    /**
     * Override the char used between path folders. The system-dependent path-separator character. This field is
     * initialized to contain the first character of the value of the system property path.separator. This character is
     * used to separate filenames in a sequence of files given as a path list. On UNIX systems, this character is ':';
     * on Microsoft Windows systems it is ';'.
     * @since 2.0
     */
    @Parameter(property = "mdep.pathSeparator", defaultValue = "")
    private String pathSeparator;

    /**
     * Replace the absolute path to the local repo with this property. This field is ignored it prefix is declared. The
     * value will be forced to "${M2_REPO}" if no value is provided AND the attach flag is true.
     * @since 2.0
     */
    @Parameter(property = "mdep.localRepoProperty", defaultValue = "")
    private String localRepoProperty;

    /**
     * This container is the same accessed in the KieMavenCli in the kie-wb-common
     */
    @Inject
    private PlexusContainer container;

    /**
     * Main entry into mojo. Gets the list of dependencies and iterates to create a classpath.
     * @throws MojoExecutionException with a message if an error occurs.
     * @see #getResolvedDependencies(boolean)
     */
    @Override
    protected void doExecute()
            throws MojoExecutionException {
        // initialize the separators.
        boolean isFileSepSet = StringUtils.isNotEmpty(fileSeparator);
        boolean isPathSepSet = StringUtils.isNotEmpty(pathSeparator);

        // don't allow them to have absolute paths when they attach.
        if (StringUtils.isEmpty(localRepoProperty)) {
            localRepoProperty = "${M2_REPO}";
        }

        Set<Artifact> artifacts = getResolvedDependencies(true);

        if (artifacts == null || artifacts.isEmpty()) {
            getLog().info("No dependencies found.");
        }

        List<Artifact> artList = new ArrayList<Artifact>(artifacts);

        StringBuilder sb = new StringBuilder();
        Iterator<Artifact> i = artList.iterator();

        if (i.hasNext()) {
            appendArtifactPath(i.next(), sb);

            while (i.hasNext()) {
                sb.append(isPathSepSet ? this.pathSeparator : File.pathSeparator);
                appendArtifactPath(i.next(), sb);
            }
        }

        String cpString = sb.toString();

        // if file separator is set, I need to replace the default one from all
        // the file paths that were pulled from the artifacts
        if (isFileSepSet) {
            // Escape file separators to be used as literal strings
            final String pattern = Pattern.quote(File.separator);
            final String replacement = Matcher.quoteReplacement(fileSeparator);
            cpString = cpString.replaceAll(pattern, replacement);
        }

        storeClasspathFile(cpString);
    }



    /**
     * It stores the specified string into the kieMap.
     * @param cpString the string to be written into the map.
     */
    private void storeClasspathFile(String cpString) {
        if (container != null && cpString != null && cpString.length() > 0) {
            Optional<Map<String, Object>> optionalKieMap = getKieMap();
            if (optionalKieMap.isPresent()) {
                String compilationID = getCompilationID(optionalKieMap);
                shareStringClasspathWithMap(compilationID, cpString);
            } else {
                getLog().error("Kie Map not present");
            }
        }
    }

    private Optional<Map<String, Object>> getKieMap() {
        try {
            /**
             * Retrieve the map passed into the Plexus container by the MavenEmbedder from the MavenIncrementalCompiler in the kie-wb-common
             */
            Map<String, Object> kieMap = (Map<String,Object>) container.lookup(Map.class,
                                                                "java.util.HashMap",
                                                                "kieMap");
            return Optional.of(kieMap);
        } catch (ComponentLookupException cle) {
            getLog().info("kieMap not present with compilationID and container present");
            return Optional.empty();
        }
    }

    private String getCompilationID(Optional<Map<String, Object>> optionalKieMap) {
        Object compilationIDObj = optionalKieMap.get().get("compilation.ID");
        if (compilationIDObj != null) {
            return compilationIDObj.toString();
        } else {
            getLog().error("compilation.ID key not present in the shared map using thread name:"
                                   + Thread.currentThread().getName());
            return Thread.currentThread().getName();
        }
    }

    private void shareStringClasspathWithMap(String compilationID, String classpath) {
        Optional<Map<String, Object>> optionalKieMap = getKieMap();
        if (optionalKieMap.isPresent() && classpath != null) {
            /*Standard for the kieMap keys -> compilationID + dot + class name or name of the variable if is a String */
            StringBuilder stringClasspathKey = new StringBuilder(compilationID).append(".").append(STRING_CLASSPATH_KEY);
            Object o = optionalKieMap.get().get(stringClasspathKey.toString());
            if (o != null){
                Set<String> depsModules = (Set<String>) o;
                depsModules.add(classpath);
            }else{
                Set<String> depsModules = new HashSet<>();
                depsModules.add(classpath);
                optionalKieMap.get().put(stringClasspathKey.toString(), depsModules);
            }
            getLog().info("String Classpath available in the map shared with the Maven Embedder with key:" + stringClasspathKey.toString());
        }else{
            getLog().info("No String Classpath to share in the map with the Maven Embedder with key");
        }
    }
}