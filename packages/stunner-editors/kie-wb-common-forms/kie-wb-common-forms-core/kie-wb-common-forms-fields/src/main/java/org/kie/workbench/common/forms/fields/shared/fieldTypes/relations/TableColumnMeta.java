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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Column;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Layout;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

@Bindable
@Portable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties.multipleSubForm.columns"),
        layout = @Layout({@Column, @Column}),
        startElement = "label"
)
public class TableColumnMeta {

    @FormField(
            labelKey = "label",
            required = true
    )
    @NotEmpty
    @NotNull
    private String label;

    @FormField(
            type = ListBoxFieldType.class,
            labelKey = "property",
            afterElement = "label",
            required = true
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.forms.data.modeller.service.dataProvider.BeanPropertiesProvider")
    @NotNull
    @NotEmpty
    private String property;

    public TableColumnMeta() {
    }

    public TableColumnMeta(@MapsTo("label") String label,
                           @MapsTo("property") String property) {
        this.label = label;
        this.property = property;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableColumnMeta that = (TableColumnMeta) o;

        if (label != null ? !label.equals(that.label) : that.label != null) {
            return false;
        }
        return property != null ? property.equals(that.property) : that.property == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
