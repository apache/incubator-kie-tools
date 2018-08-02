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

/**
 * Constant with common maven cli args, used to avoid the creation of strings and mispelled args
 */
public class MavenCLIArgs {

    public static final String CLEAN = "clean";

    public static final String COMPILE = "compile";

    public static final String DEFAULT_COMPILE = "default-compile";

    public static final String VALIDATE = "validate";

    public static final String TEST = "test";

    public static final String PACKAGE = "package";

    public static final String VERIFY = "verify";

    public static final String INSTALL = "install";

    public static final String DEPLOY = "deploy";

    public static final String VERSION = "-v";

    public static final String DEBUG = "-X";

    public static final String OFFLINE = "-o";

    public static final String LOG = "-l";

    public static final String SKIP_TEST = "-DskipTests";

    public static final String ALTERNATE_USER_SETTINGS = "-s";

    public static final String FAIL_NEVER = "-fn";

    public static final String DEPENDENCY_RESOLVE = "dependency:resolve";
}
