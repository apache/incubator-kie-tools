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
    private String globalsString;
    private Map<String, String> catRules;
    private String assetExportFileName;

    public Module( ModuleType type,
                   String uuid,
                   String name,
                   String normalizedPackageName,
                   String packageHeaderInfo,
                   String globalsString,
                   Map<String, String> catRules,
                   String assetExportFileName ) {
        this.type = type;
        this.uuid = uuid;
        this.name = name;
        this.normalizedPackageName = normalizedPackageName != null ? normalizedPackageName : "";
        // todo check if null values should be allowed (see PackageImportHelper#l118/l138
        this.packageHeaderInfo = packageHeaderInfo != null ? packageHeaderInfo : "";
        this.globalsString = globalsString != null ? globalsString : "";
        this.catRules = catRules != null ? catRules : new HashMap<String, String>();
        // todo asset file name could be null, take into account when importing
        this.assetExportFileName = assetExportFileName;
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

    public String getGlobalsString() {
        return globalsString;
    }

    public Map<String, String> getCatRules() {
        return catRules;
    }

    public String getAssetExportFileName() {
        return assetExportFileName;
    }
}
