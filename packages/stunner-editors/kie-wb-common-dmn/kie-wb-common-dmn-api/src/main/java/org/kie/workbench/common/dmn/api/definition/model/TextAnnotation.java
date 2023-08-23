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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition()
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.model.TextAnnotation"),
        startElement = "id")
public class TextAnnotation extends Artifact implements DMNViewDefinition<GeneralRectangleDimensionsSet>,
                                                        HasText,
                                                        HasContentDefinitionId,
                                                        DynamicReadOnly {

    private static final String[] READONLY_FIELDS = {"Description", "Text", "TextFormat"};

    protected boolean allowOnlyVisualChange;

    /**
     * Hold the {@link DMNDiagramElement} id for the {@link TextAnnotation} instance.
     */
    private String dmnDiagramId;

    @Category
    private static final String stunnerCategory = Categories.NODES;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("text-annotation").collect(Collectors.toSet());

    @Property(meta = PropertyMetaTypes.NAME)
    @FormField(afterElement = "description")
    protected Text text;

    @Property
    @FormField(afterElement = "text", labelKey = "text")
    protected TextFormat textFormat;

    @Property
    @FormField(afterElement = "variable")
    @Valid
    protected StylingSet stylingSet;

    @Property
    protected GeneralRectangleDimensionsSet dimensionsSet;

    public TextAnnotation() {
        this(new Id(),
             new org.kie.workbench.common.dmn.api.property.dmn.Description(),
             new Text(),
             new TextFormat(),
             new StylingSet(),
             new GeneralRectangleDimensionsSet());
    }

    public TextAnnotation(final Id id,
                          final org.kie.workbench.common.dmn.api.property.dmn.Description description,
                          final Text text,
                          final TextFormat textFormat,
                          final StylingSet stylingSet,
                          final GeneralRectangleDimensionsSet dimensionsSet) {
        super(id,
              description);
        this.text = text;
        this.textFormat = textFormat;
        this.stylingSet = stylingSet;
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // Stunner core properties
    // -----------------------

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    @Override
    public StylingSet getStylingSet() {
        return stylingSet;
    }

    public void setStylingSet(final StylingSet stylingSet) {
        this.stylingSet = stylingSet;
    }

    @Override
    public GeneralRectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final GeneralRectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public void setText(final Text text) {
        this.text = text;
    }

    public TextFormat getTextFormat() {
        return textFormat;
    }

    public void setTextFormat(final TextFormat textFormat) {
        this.textFormat = textFormat;
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
    public void setContentDefinitionId(final String contentDefinitionId) {
        setId(new Id(contentDefinitionId));
    }

    @Override
    public void setDiagramId(final String dmnDiagramId) {
        this.dmnDiagramId = dmnDiagramId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextAnnotation)) {
            return false;
        }

        final TextAnnotation that = (TextAnnotation) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }
        if (textFormat != null ? !textFormat.equals(that.textFormat) : that.textFormat != null) {
            return false;
        }
        if (stylingSet != null ? !stylingSet.equals(that.stylingSet) : that.stylingSet != null) {
            return false;
        }
        return dimensionsSet != null ? dimensionsSet.equals(that.dimensionsSet) : that.dimensionsSet == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         text != null ? text.hashCode() : 0,
                                         textFormat != null ? textFormat.hashCode() : 0,
                                         stylingSet != null ? stylingSet.hashCode() : 0,
                                         dimensionsSet != null ? dimensionsSet.hashCode() : 0);
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

    protected boolean isReadonlyField(final String fieldName) {
        return Arrays.stream(READONLY_FIELDS).anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }
}
