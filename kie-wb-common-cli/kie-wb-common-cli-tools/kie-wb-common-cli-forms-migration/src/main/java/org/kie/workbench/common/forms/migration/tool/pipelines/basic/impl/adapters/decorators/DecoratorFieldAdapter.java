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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.decorators;

import java.util.function.Consumer;

import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.FieldAdapter;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class DecoratorFieldAdapter implements FieldAdapter {

    @Override
    public String[] getLegacyFieldTypeCodes() {
        return new String[]{FieldTypeBuilder.HTML_LABEL, FieldTypeBuilder.SEPARATOR};
    }

    @Override
    public void parseField(Field originalField, FormMigrationSummary formSummary, FormDefinition fieldDefinitionConsumer, Consumer<LayoutComponent> layoutElementConsumer) {

        String htmlContent;

        switch (originalField.getFieldType().getCode()) {
            case FieldTypeBuilder.HTML_LABEL:
                htmlContent = lookupI18nValue(originalField.getHtmlContent());
                break;
            default:
                htmlContent = "<HR/>";
                break;
        }

        LayoutComponent component = new LayoutComponent(FormsMigrationConstants.HTML_COMPONENT);
        component.addProperty(FormsMigrationConstants.HTML_CODE_PARAMETER, htmlContent);

        layoutElementConsumer.accept(component);
    }
}
