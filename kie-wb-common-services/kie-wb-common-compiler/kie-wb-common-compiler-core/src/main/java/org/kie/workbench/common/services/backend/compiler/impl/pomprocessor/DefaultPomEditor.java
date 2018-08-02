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

package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationProvider;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.StandardOpenOption;

/***
 * IS the main actor in the changes to the build tag in the poms
 */
public class DefaultPomEditor implements PomEditor {

    public final String POM = "pom";
    public final String TRUE = "true";
    public final String POM_NAME = "pom.xml";
    public final String KJAR_EXT = "kjar";
    protected final Logger logger = LoggerFactory.getLogger(DefaultPomEditor.class);
    protected String FILE_URI = "file://";
    protected Map<ConfigurationKey, String> conf;
    protected MavenXpp3Reader reader;
    protected MavenXpp3Writer writer;
    protected Set<PomPlaceHolder> history;

    public DefaultPomEditor(Set<PomPlaceHolder> history,
                            ConfigurationProvider config) {
        conf = config.loadConfiguration();
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
        this.history = history;
    }

    public Set<PomPlaceHolder> getHistory() {
        return Collections.unmodifiableSet(history);
    }

    @Override
    public Boolean cleanHistory() {
        history.clear();
        return Boolean.TRUE;
    }

    public PomPlaceHolder readSingle(Path pom) {
        PomPlaceHolder holder = new PomPlaceHolder();
        try {
            Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
            holder = new PomPlaceHolder(pom.toAbsolutePath().toString(),
                                        model.getArtifactId(),
                                        model.getGroupId(),
                                        model.getVersion(),
                                        model.getPackaging());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return holder;
    }

    public boolean write(Path pom,
                         CompilationRequest request) {

        try {
            Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
            if (model == null) {
                logger.error("Model null from pom file:",
                             pom.toString());
                return false;
            }

            PomPlaceHolder pomPH = new PomPlaceHolder(pom.toAbsolutePath().toString(),
                                                      model.getArtifactId(),
                                                      model.getGroupId(),
                                                      model.getVersion(),
                                                      model.getPackaging(),
                                                      Files.readAllBytes(pom));

            if (!history.contains(pomPH)) {

                PluginPresents plugs = updatePom(model);
                request.getInfo().lateAdditionKiePluginPresent(plugs.isKiePluginPresent());
                if (!request.skipProjectDependenciesCreationList()) {
                    // we add the mvn cli args to run the dependency:build-classpath
                    String args[] = addCreateClasspathMavenArgs(request.getKieCliRequest().getArgs(), request);
                    request.getKieCliRequest().setArgs(args);
                }
                if (plugs.pomOverwriteRequired()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    writer.write(baos,
                                 model);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Pom changed:{}",
                                     new String(baos.toByteArray(),
                                                StandardCharsets.UTF_8));
                    }

                    Path pomParent = Paths.get(URI.create(
                            new StringBuffer().
                                    append(FILE_URI).
                                    append(pom.getParent().toAbsolutePath().toString()).
                                    append("/").
                                    append(POM_NAME).toString()));
                    Files.delete(pomParent);
                    Files.write(pomParent,
                                baos.toByteArray(),
                                StandardOpenOption.CREATE_NEW);//enhanced pom
                }
                history.add(pomPH);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }



    /* Pom's Plugin manipulation methods*/

    private PluginPresents updatePom(Model model) {

        Build build = model.getBuild();
        if (build == null) {  //pom without build tag
            model.setBuild(new Build());
            build = model.getBuild();
        }

        PluginsContainer dto = checkPlugins(model, build,
                                            conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP),
                                            conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT),
                                            conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP),
                                            conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT),
                                            conf.get(ConfigurationKey.KIE_PLUGIN_GROUP),
                                            conf.get(ConfigurationKey.KIE_MAVEN_PLUGIN_ARTIFACT),
                                            conf.get(ConfigurationKey.KIE_TAKARI_PLUGIN_ARTIFACT));

        return updatePOMModel(build, dto);
    }

    private PluginsContainer checkPlugins(Model model, Build build, String MAVEN_COMPILER_GROUP_ID, String MAVEN_COMPILER_ARTIFACT_ID, String TAKARI_COMPILER_GROUP_ID, String TAKARI_COMPILER_ARTIFACT_ID, String KIE_PLUGIN_GROUP_ID, String KIE_PLUGIN_ARTIFACT_ID, String KIE_TAKARI_PLUGIN_ARTIFACT_ID) {
        PluginsContainer dto = new PluginsContainer();
        if (model.getPackaging().equals(KJAR_EXT)) {
            dto.setKiePluginPresent(Boolean.TRUE);
        }
        Integer i = 0;
        for (Plugin plugin : build.getPlugins()) {
            // check if is present the default maven compiler
            if (plugin.getGroupId().equals(MAVEN_COMPILER_GROUP_ID) && plugin.getArtifactId().equals(MAVEN_COMPILER_ARTIFACT_ID)) {
                dto.setDefaultCompilerPluginPresent(Boolean.TRUE);
                dto.setDefaultMavenCompilerPosition(i);
            }

            //check if is present the alternative maven compiler
            if (plugin.getGroupId().equals(TAKARI_COMPILER_GROUP_ID) && plugin.getArtifactId().equals(TAKARI_COMPILER_ARTIFACT_ID)) {
                dto.setAlternativeCompilerPluginPresent(Boolean.TRUE);
                dto.setAlternativeCompilerPosition(i);
            }

            //check if is present the kie maven plugin
            if (plugin.getGroupId().equals(KIE_PLUGIN_GROUP_ID) && plugin.getArtifactId().equals(KIE_PLUGIN_ARTIFACT_ID)) {
                dto.setKiePluginPresent(Boolean.TRUE);
                dto.setKieMavenPluginPosition(i);
            }

            if (plugin.getGroupId().equals(KIE_PLUGIN_GROUP_ID) && plugin.getArtifactId().equals(KIE_TAKARI_PLUGIN_ARTIFACT_ID)) {
                dto.setKieTakariPresent(Boolean.TRUE);
            }
            i++;
        }
        return dto;
    }

    private DefaultPluginPresents updatePOMModel(Build build, PluginsContainer dto) {

        checkAlternativeCompilerPlugin(build, dto);

        checkDefaultCompilerPlugin(build, dto);

        checkCompilerPluginsPositions(build, dto);

        changeKieMavenIntoKieTakariPlugin(build, dto);

        return new DefaultPluginPresents(dto.getDefaultCompilerPluginPresent(),
                                         dto.getAlternativeCompilerPluginPresent(),
                                         dto.getKiePluginPresent(),
                                         dto.getOverwritePOM());
    }

    private void changeKieMavenIntoKieTakariPlugin(Build build, PluginsContainer dto) {
        // Change the kie-maven-plugin into kie-takari-plugin
        if (dto.getKiePluginPresent() && !dto.getKieTakariPresent()) {
            List<Plugin> plugins = build.getPlugins();
            Plugin kieMavenPlugin = build.getPlugins().get(dto.getKieMavenPluginPosition());

            if (kieMavenPlugin.getArtifactId().equals(conf.get(ConfigurationKey.KIE_PLUGIN_GROUP))) {
                Plugin kieTakariPlugin = MavenAPIUtil.getPlugin(kieMavenPlugin.getGroupId(),
                                                                conf.get(ConfigurationKey.KIE_TAKARI_PLUGIN_ARTIFACT),
                                                                kieMavenPlugin.getVersion(),
                                                                Boolean.parseBoolean(kieMavenPlugin.getExtensions()));
                plugins.set(dto.getKieMavenPluginPosition(), kieTakariPlugin);
                build.setPlugins(plugins);
                dto.setOverwritePOM(Boolean.TRUE);
            }
        }
    }

    private void checkCompilerPluginsPositions(Build build, PluginsContainer dto) {
        if (dto.getDefaultCompilerPluginPresent() && dto.getAlternativeCompilerPluginPresent()) {
            if (dto.getDefaultMavenCompilerPosition() <= dto.getAlternativeCompilerPosition()) {
                //swap the positions
                Plugin defaultMavenCompiler = build.getPlugins().get(dto.getDefaultMavenCompilerPosition());
                Plugin alternativeCompiler = build.getPlugins().get(dto.getAlternativeCompilerPosition());
                build.getPlugins().set(dto.getDefaultMavenCompilerPosition(), alternativeCompiler);
                build.getPlugins().set(dto.getAlternativeCompilerPosition(), defaultMavenCompiler);
                dto.setOverwritePOM(Boolean.TRUE);
            }
        }
    }

    private void checkDefaultCompilerPlugin(Build build, PluginsContainer dto) {
        if (!dto.getDefaultCompilerPluginPresent()) {
            //if default maven compiler is not present we add the skip and phase none  to avoid its use
            Plugin disabledDefaultCompiler = MavenAPIUtil.getPlugin(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP),
                                                                    conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT),
                                                                    conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION));

            MavenAPIUtil.disableMavenCompilerAlreadyPresent(disabledDefaultCompiler);
            build.addPlugin(disabledDefaultCompiler);
            dto.setDefaultCompilerPluginPresent(Boolean.TRUE);
            dto.setOverwritePOM(Boolean.TRUE);
        }
    }

    private void checkAlternativeCompilerPlugin(Build build, PluginsContainer dto) {
        if (!dto.getAlternativeCompilerPluginPresent()) {
            build.addPlugin(MavenAPIUtil.getNewCompilerPlugin(conf));
            dto.setAlternativeCompilerPluginPresent(Boolean.TRUE);
            dto.setOverwritePOM(Boolean.TRUE);
        }
    }

    private String[] addCreateClasspathMavenArgs(String[] args, CompilationRequest req) {
        String[] newArgs = Arrays.copyOf(args, args.length + 2);
        newArgs[args.length] = MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH;
        newArgs[args.length + 1] = MavenConfig.MAVEN_DEP_PLUGING_LOCAL_REPOSITORY + req.getMavenRepo();
        return newArgs;
    }
}
