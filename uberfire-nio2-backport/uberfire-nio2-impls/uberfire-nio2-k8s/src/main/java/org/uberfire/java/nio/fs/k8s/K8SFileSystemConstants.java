/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.util.regex.Pattern;

public class K8SFileSystemConstants {
    public static final String CFG_MAP_LABEL_FSOBJ_TYPE_KEY = "k8s.fs.nio.java.uberfire.org/fsobj-type";
    public static final String CFG_MAP_LABEL_FSOBJ_APP_KEY = "k8s.fs.nio.java.uberfire.org/fsobj-app";
    public static final String CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX = "k8s.fs.nio.java.uberfire.org/fsobj-name-";
    public static final String CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY = "k8s.fs.nio.java.uberfire.org/fsobj-size";
    public static final String CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY = "k8s.fs.nio.java.uberfire.org/fsobj-lastModifiedTimestamp";
    public static final String CFG_MAP_FSOBJ_NAME_PREFIX = "k8s-fsobj-";
    public static final String CFG_MAP_FSOBJ_CONTENT_KEY = "fsobj-content";

    public static final String K8S_FS_MAX_CAPACITY_PROPERTY_NAME = "org.uberfire.java.nio.fs.k8s.max.file.size";
    public static final String K8S_FS_APP_PROPERTY_NAME = "org.uberfire.java.nio.fs.k8s.app";
    public static final String K8S_FS_APP_DEFAULT_VALUE = "unknown";

    public static final Pattern K8S_FS_NAME_RESTRICATION = Pattern.compile("(([A-Za-z0-9.][-A-Za-z0-9_.]*)?[A-Za-z0-9])?");
    public static final String K8S_FS_HIDDEN_FILE_INDICATOR = ".";
    public static final String K8S_FS_HIDDEN_FILE_INDICATOR_SUFFIX = "1";
    
    public static final String K8S_FS_SCHEME = "k8s"; 
    public static final String K8S_FS_NO_IMPL = "Not implemented";

    public static final int K8S_FS_NAME_MAX_LENGTH = 63;
}
