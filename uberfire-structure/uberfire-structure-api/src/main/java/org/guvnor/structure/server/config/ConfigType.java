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

package org.guvnor.structure.server.config;

public enum ConfigType {

    GLOBAL(".global", "global", false),
    @Deprecated ORGANIZATIONAL_UNIT(".organizationalunit", "organizationalunits", false), // Required for migration purposes.
    SPACE(".space", "spaces", false),
    REPOSITORY(".repository", "repositories", true),
    PROJECT(".project", "projects", false),
    EDITOR(".editor", "editors", false),
    DEPLOYMENT(".deployment", "deployments", false);

    private String ext;

    private String dir;

    private boolean hasNamespace;

    ConfigType(final String ext,
               final String dir,
               final boolean hasNamespace) {
        this.ext = ext;
        this.dir = dir;
        this.hasNamespace = hasNamespace;
    }

    public String getExt() {
        return this.ext;
    }

    public String getDir() {
        return dir;
    }

    public boolean hasNamespace() {
        return hasNamespace;
    }
}
