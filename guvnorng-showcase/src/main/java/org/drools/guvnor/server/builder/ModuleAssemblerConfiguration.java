/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

public class ModuleAssemblerConfiguration {

    private String buildMode;
    private String statusOperator;
    private String statusDescriptionValue;
    private boolean enableStatusSelector = false;
    private String categoryOperator;
    private String categoryValue;
    private boolean enableCategorySelector = false;
    private String customSelectorConfigName;

    public String getBuildMode() {
        return buildMode;
    }

    public void setBuildMode(String buildMode) {
        this.buildMode = buildMode;
    }

    public String getStatusOperator() {
        return statusOperator;
    }

    public void setStatusOperator(String statusOperator) {
        this.statusOperator = statusOperator;
    }

    public String getStatusDescriptionValue() {
        return statusDescriptionValue;
    }

    public void setStatusDescriptionValue(String statusDescriptionValue) {
        this.statusDescriptionValue = statusDescriptionValue;
    }

    public boolean isEnableStatusSelector() {
        return enableStatusSelector;
    }

    public void setEnableStatusSelector(boolean enableStatusSelector) {
        this.enableStatusSelector = enableStatusSelector;
    }

    public String getCategoryOperator() {
        return categoryOperator;
    }

    public void setCategoryOperator(String categoryOperator) {
        this.categoryOperator = categoryOperator;
    }

    public String getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public boolean isEnableCategorySelector() {
        return enableCategorySelector;
    }

    public void setEnableCategorySelector(boolean enableCategorySelector) {
        this.enableCategorySelector = enableCategorySelector;
    }

    public String getCustomSelectorConfigName() {
        return customSelectorConfigName;
    }

    public void setCustomSelectorConfigName(String customSelectorConfigName) {
        this.customSelectorConfigName = customSelectorConfigName;
    }
}
