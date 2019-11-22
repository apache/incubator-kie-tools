/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.repositories;

/**
 * Magic Strings for common environment parameter names
 */
public class EnvironmentParameters {

    public static final String SCHEME = "scheme";

    public static final String SPACE = "space";

    public static final String MANAGED = "managed";

    public static final String ORIGIN = "origin";

    public static final String BRANCHES = "branches";

    public static final String SUBDIRECTORY = "subdirectory";

    public static final String USER_NAME = "username";

    public static final String PASSWORD = "password";

    public static final String SECURE_PREFIX = "secure:";

    public static final String CRYPT_PREFIX = "crypt:";

    public static final String CRYPT_PASSWORD = CRYPT_PREFIX + PASSWORD;

    public static final String INIT = "init";

    public static final String AVOID_INDEX = "avoid_index";

    public static final String MIRROR = "mirror";
}
