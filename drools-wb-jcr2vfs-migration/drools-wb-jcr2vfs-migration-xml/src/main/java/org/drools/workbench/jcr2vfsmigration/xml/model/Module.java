/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.xml.model;

import java.util.HashMap;
import java.util.Map;

public class Module {

    private ModuleType type;
    private String uuid;
    private String name;
    private String normalizedPackageName;
    private String packageHeaderInfo;
    private Map<String, String> catRules;

    public Module( ModuleType type,
                   String uuid,
                   String name,
                   String normalizedPackageName,
                   String packageHeaderInfo,
                   Map<String, String> catRules ) {
        this.type = type;
        this.uuid = uuid;
        this.name = name;
        this.normalizedPackageName = normalizedPackageName;
        this.packageHeaderInfo = packageHeaderInfo;
        this.catRules = catRules != null ? catRules : new HashMap<String, String>();
    }

    public ModuleType getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedPackageName() {
        return normalizedPackageName;
    }

    public String getPackageHeaderInfo() {
        return packageHeaderInfo;
    }

    public Map<String, String> getCatRules() {
        return catRules;
    }
}
