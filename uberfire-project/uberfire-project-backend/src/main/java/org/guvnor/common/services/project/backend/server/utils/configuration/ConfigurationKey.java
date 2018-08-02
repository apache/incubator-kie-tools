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

package org.guvnor.common.services.project.backend.server.utils.configuration;

/**
 * Keys used to configure the Maven compiler in the kie-wb-common
 *
 * <p>
 *  COMPILER (jdt) jdt or javac
 *  SOURCE_VERSION  (1.8) configured with jvm source code version
 *  TARGET_VERSION  (1.8) configured with jvm target version
 *  <p>
 *  FAIL_ON_ERROR (false) configured with false to continue the build on the correct classes and skip the build of classes with errors
 *  MAVEN_COMPILER_PLUGIN_GROUP (org.apache.maven.plugins) configured with default maven compiler group to disabled it
 *  MAVEN_COMPILER_PLUGIN_ARTIFACT (maven-compiler-plugin) configured with default maven compiler ArtifactID to disabled it
 *  MAVEN_COMPILER_PLUGIN_VERSION (3.7.0) configured with default maven compiler version to disabled it
 *  <p>
 *  TAKARI_COMPILER_PLUGIN_GROUP (io.takari.maven.plugins) configured with takari GroupID
 *  TAKARI_COMPILER_PLUGIN_ARTIFACT (takari-lifecycle-plugin) configured with takari ArtifactID
 *  TAKARI_COMPILER_PLUGIN_VERSION (${version.io.takari.maven.plugins})configured with a placeholder and set with the correct value in the maven build with takari version
 *  <p>
 *  KIE_PLUGIN_GROUP (org.kie) configured with the GroupID of kie plugin from the Integration prj
 *  KIE_MAVEN_PLUGIN_ARTIFACT  (kie-maven-plugin) configured with the artifactID of the kie-maven-plugin
 *  KIE_TAKARI_PLUGIN_ARTIFACT (kie-maven-plugin) configured with the artifactID of the kie-takari-plugin
 *  <p>
 *  KIE_VERSION (${version.org.kie}) configured with a placeholder and set with the correct value in the maven build
 * */
public enum ConfigurationKey {

    COMPILER,
    SOURCE_VERSION,
    TARGET_VERSION,
    FAIL_ON_ERROR,

    MAVEN_COMPILER_PLUGIN_GROUP,
    MAVEN_COMPILER_PLUGIN_ARTIFACT,
    MAVEN_COMPILER_PLUGIN_VERSION,

    TAKARI_COMPILER_PLUGIN_GROUP,
    TAKARI_COMPILER_PLUGIN_ARTIFACT,
    TAKARI_COMPILER_PLUGIN_VERSION,

    KIE_PLUGIN_GROUP,
    KIE_MAVEN_PLUGIN_ARTIFACT,
    KIE_TAKARI_PLUGIN_ARTIFACT,

    KIE_VERSION;
}
