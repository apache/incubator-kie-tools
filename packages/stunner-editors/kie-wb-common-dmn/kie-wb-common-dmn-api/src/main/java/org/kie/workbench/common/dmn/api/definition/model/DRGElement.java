/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.Arrays;

import javax.validation.Valid;

import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.rules.NoInputNodesInImportedDecisionRule;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.HasStringName;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;

@RuleExtension(handler = NoInputNodesInImportedDecisionRule.class)
public abstract class DRGElement extends NamedElement implements DynamicReadOnly,
                                                                 HasContentDefinitionId,
                                                                 HasStringName {

    private static final String[] READONLY_FIELDS = {
            "NameHolder",
            "AllowedAnswers",
            "Description",
            "Question",
            "DataType",
            "SourceType",
            "LocationURI"};

    protected boolean allowOnlyVisualChange;

    /**
     * Hold the {@link DMNDiagramElement} id for the {@link DRGElement} instance.
     */
    private String dmnDiagramId;

    @Property
    @FormField(afterElement = "description", type = DocumentationLinksFieldType.class)
    @Valid
    protected DocumentationLinksHolder linksHolder;

    public DRGElement() {
        this.linksHolder = new DocumentationLinksHolder();
    }

    public DRGElement(final Id id,
                      final Description description,
                      final Name name) {
        super(id,
              description,
              name);
        this.linksHolder = new DocumentationLinksHolder();
    }

    @Override
    public void setAllowOnlyVisualChange(final boolean allowOnlyVisualChange) {
        this.allowOnlyVisualChange = allowOnlyVisualChange;
    }

    @Override
    public boolean isAllowOnlyVisualChange() {
        return allowOnlyVisualChange;
    }

    @Override
    public ReadOnly getReadOnly(final String fieldName) {
        if (!isAllowOnlyVisualChange()) {
            return ReadOnly.NOT_SET;
        }

        if (isReadonlyField(fieldName)) {
            return ReadOnly.TRUE;
        }

        return ReadOnly.FALSE;
    }

    @Override
    public String getContentDefinitionId() {
        return getId().getValue();
    }

    @Override
    public String getDiagramId() {
        return dmnDiagramId;
    }

    @Override
    public void setDiagramId(final String dmnDiagramId) {
        this.dmnDiagramId = dmnDiagramId;
    }

    @Override
    public void setContentDefinitionId(final String contentDefinitionId) {
        setId(new Id(contentDefinitionId));
    }

    protected boolean isReadonlyField(final String fieldName) {
        return Arrays.stream(READONLY_FIELDS).anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }

    public DocumentationLinksHolder getLinksHolder() {
        return linksHolder;
    }

    public void setLinksHolder(final DocumentationLinksHolder linksHolder) {
        this.linksHolder = linksHolder;
    }

    @Override
    public String getStringName() {
        return getName().getValue();
    }
}
