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
package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

class PluginsContainer {

    Boolean defaultCompilerPluginPresent = Boolean.FALSE;
    Boolean alternativeCompilerPluginPresent = Boolean.FALSE;
    Boolean kiePluginPresent = Boolean.FALSE;
    Boolean kieTakariPresent = Boolean.FALSE;
    Boolean overwritePOM = Boolean.FALSE;
    Integer alternativeCompilerPosition = 0;
    Integer defaultMavenCompilerPosition = 0;
    Integer kieMavenPluginPosition = 0;

    public Boolean getOverwritePOM() {
        return overwritePOM;
    }

    public void setOverwritePOM(Boolean overwritePOM) {
        this.overwritePOM = overwritePOM;
    }

    public Boolean getDefaultCompilerPluginPresent() {
        return defaultCompilerPluginPresent;
    }

    public void setDefaultCompilerPluginPresent(Boolean defaultCompilerPluginPresent) {
        this.defaultCompilerPluginPresent = defaultCompilerPluginPresent;
    }

    public Boolean getAlternativeCompilerPluginPresent() {
        return alternativeCompilerPluginPresent;
    }

    public void setAlternativeCompilerPluginPresent(Boolean alternativeCompilerPluginPresent) {
        this.alternativeCompilerPluginPresent = alternativeCompilerPluginPresent;
    }

    public Boolean getKiePluginPresent() {
        return kiePluginPresent;
    }

    public void setKiePluginPresent(Boolean kiePluginPresent) {
        this.kiePluginPresent = kiePluginPresent;
    }

    public Boolean getKieTakariPresent() {
        return kieTakariPresent;
    }

    public void setKieTakariPresent(Boolean kieTakariPresent) {
        this.kieTakariPresent = kieTakariPresent;
    }

    public Integer getAlternativeCompilerPosition() {
        return alternativeCompilerPosition;
    }

    public void setAlternativeCompilerPosition(Integer alternativeCompilerPosition) {
        this.alternativeCompilerPosition = alternativeCompilerPosition;
    }

    public Integer getDefaultMavenCompilerPosition() {
        return defaultMavenCompilerPosition;
    }

    public void setDefaultMavenCompilerPosition(Integer defaultMavenCompilerPosition) {
        this.defaultMavenCompilerPosition = defaultMavenCompilerPosition;
    }

    public Integer getKieMavenPluginPosition() {
        return kieMavenPluginPosition;
    }

    public void setKieMavenPluginPosition(Integer kieMavenPluginPosition) {
        this.kieMavenPluginPosition = kieMavenPluginPosition;
    }
}
