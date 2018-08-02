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
package org.kie.workbench.common.services.backend.compiler.configuration;

/***
 * It contains all the types of decorator of a MavenCompiler provided with Kie projects
 */
public enum KieDecorator {

    NONE,
    CLASSPATH_DEPS_AFTER_DECORATOR,
    JGIT_BEFORE,
    KIE_AFTER,
    KIE_AND_CLASSPATH_AFTER_DEPS,
    LOG_OUTPUT_AFTER,
    JGIT_BEFORE_AND_LOG_AFTER,
    JGIT_BEFORE_AND_KIE_AFTER,
    JGIT_BEFORE_AND_KIE_AND_LOG_AFTER,
    JGIT_BEFORE_AND_KIE_AND_LOG_AND_CLASSPATH_AFTER,
    KIE_LOG_AND_CLASSPATH_DEPS_AFTER,
    KIE_AND_LOG_AFTER
}
