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
package org.kie.workbench.common.services.backend.compiler.configuration;

/**
 * Common params used by Maven
 */
public class MavenConfig {

    public static final String DEPS_IN_MEMORY_BUILD_CLASSPATH ="org.kie.workbench.services:kie-wb-common-compiler-maven-plugins:build-classpath";

    public static final String MAVEN_DEP_PLUGING_LOCAL_REPOSITORY = "-Dmdep.localRepoProperty=";

    public static final String MAVEN_PLUGIN_CONFIGURATION = "configuration";

    public static final String MAVEN_COMPILER_ID = "compilerId";

    public static final String MAVEN_SKIP = "skip";

    public static final String MAVEN_SOURCE = "source";

    public static final String MAVEN_TARGET = "target";

    public static final String FAIL_ON_ERROR = "failOnError";

    public static final String MAVEN_SKIP_MAIN = "skipMain";

    public static final String MAVEN_DEFAULT_COMPILE = "default-compile";

    public static final String MAVEN_PHASE_NONE = "none";

    public static final String COMPILATION_ID = "compilation.ID";

    public static final String ARCHETYPE_GENERATE  = "archetype:generate";

    public static final String ARCHETYPE_GENERATE_BLANK = "-B";

    public static final String GROUP_ID = "-DgroupId=";

    public static final String ARTIFACT_ID = "-DartifactId=";

    public static final String ARCHETYPE_ARTIFACT_ID = "-DarchetypeArtifactId=";
}
