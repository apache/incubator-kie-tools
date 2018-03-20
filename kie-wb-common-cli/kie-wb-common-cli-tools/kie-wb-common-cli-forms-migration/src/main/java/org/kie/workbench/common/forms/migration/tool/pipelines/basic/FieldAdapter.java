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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic;

import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public interface FieldAdapter {

    String[] getLegacyFieldTypeCodes();

    void parseField(Field originalField, FormMigrationSummary formSummary, FormDefinition fieldDefinitionConsumer, Consumer<LayoutComponent> layoutElementConsumer);

    default String lookupI18nValue(Map<String, String> i1i8nMap) {
        if(i1i8nMap == null) {
            return "";
        }
        String value = i1i8nMap.get(FormsMigrationConstants.DEFAULT_LANG);

        if (StringUtils.isNotEmpty(value)) {
            return value;
        }

        return i1i8nMap.values()
                .stream()
                .filter(StringUtils::isNotEmpty)
                .findAny()
                .orElse("");
    }
}
