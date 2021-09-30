/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.shared.model;

/**
 * Holds Constants for the Import ZIP
 *
 */
public class ImportDefinitions {

    public static final String DATASET_PREFIX = "dashbuilder/datasets";
    public static final String DATASET_DEF_PREFIX = DATASET_PREFIX + "/definitions";
    public static final String DATASET_SUFFIX = ".dset";
    
    public static final String PERSPECTIVE_PREFIX = "dashbuilder/perspectives";
    public static final String PERSPECTIVE_SUFFIX = "perspective_layout";

    public static final String NAVIGATION_PREFIX = "dashbuilder/navigation";
    public static final String NAVIGATION_FILE = NAVIGATION_PREFIX + "/navigation/navtree.json";
    
    public static final String COMPONENT_PREFIX = "dashbuilder/components";

    private ImportDefinitions() {

    }

}