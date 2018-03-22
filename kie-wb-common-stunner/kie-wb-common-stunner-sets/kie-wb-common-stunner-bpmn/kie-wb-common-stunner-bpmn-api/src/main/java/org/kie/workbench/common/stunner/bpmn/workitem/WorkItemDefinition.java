/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.workitem;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class WorkItemDefinition {

    private String name;
    private String description;
    private String category;
    private String displayName;
    private String documentation;
    private String iconData;
    private String defaultHandler;
    private String parameters;
    private String results;

    public WorkItemDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public WorkItemDefinition setDescription(String description) {
        this.description = description;
        return this;
    }

    public WorkItemDefinition setCategory(String category) {
        this.category = category;
        return this;
    }

    public WorkItemDefinition setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public WorkItemDefinition setDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public WorkItemDefinition setIconData(String iconData) {
        this.iconData = iconData;
        return this;
    }

    public WorkItemDefinition setDefaultHandler(String defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    public WorkItemDefinition setParameters(String parameters) {
        this.parameters = parameters;
        return this;
    }

    public WorkItemDefinition setResults(String results) {
        this.results = results;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getIconData() {
        return iconData;
    }

    public String getDefaultHandler() {
        return defaultHandler;
    }

    public String getParameters() {
        return parameters;
    }

    public String getResults() {
        return results;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         name.hashCode(),
                                         (null != category) ? category.hashCode() : 0,
                                         (null != displayName) ? displayName.hashCode() : 0,
                                         (null != description) ? description.hashCode() : 0,
                                         (null != documentation) ? documentation.hashCode() : 0,
                                         (null != iconData) ? iconData.hashCode() : 0,
                                         (null != defaultHandler) ? defaultHandler.hashCode() : 0,
                                         (null != parameters) ? parameters.hashCode() : 0,
                                         (null != results) ? results.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WorkItemDefinition) {
            WorkItemDefinition other = (WorkItemDefinition) o;
            return super.equals(other) &&
                    name.equals(other.name) &&
                    ((null != category) ? category.equals(other.category) : null == other.category) &&
                    ((null != description) ? description.equals(other.description) : null == other.description) &&
                    ((null != displayName) ? displayName.equals(other.displayName) : null == other.displayName) &&
                    ((null != documentation) ? documentation.equals(other.documentation) : null == other.documentation) &&
                    ((null != iconData) ? iconData.equals(other.iconData) : null == other.iconData) &&
                    ((null != defaultHandler) ? defaultHandler.equals(other.defaultHandler) : null == other.defaultHandler) &&
                    ((null != parameters) ? parameters.equals(other.parameters) : null == other.parameters) &&
                    ((null != results) ? results.equals(other.results) : null == other.results);
        }
        return false;
    }
}