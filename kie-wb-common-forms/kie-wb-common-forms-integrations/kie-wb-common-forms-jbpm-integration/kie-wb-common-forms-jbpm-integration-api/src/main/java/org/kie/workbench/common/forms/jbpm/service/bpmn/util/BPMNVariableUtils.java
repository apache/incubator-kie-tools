/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.jbpm.service.bpmn.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldReadOnlyEntry;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.kie.workbench.common.forms.service.backend.util.ModelPropertiesGenerator;

public class BPMNVariableUtils {

    public static final String TASK_FORM_VARIABLE = "TaskName";
    public static final String TASK_FORM_SUFFIX = "-taskform";

    private static List<String> bannedTaskInputNames;

    static {
        String[] bannedNames = new String[]{TASK_FORM_VARIABLE, "GroupId", "Skippable", "Comment", "Description",
                "Content", "Priority", "Locale", "CreatedBy", "NotCompletedReassign", "NotStartedReassign",
                "NotCompletedNotify", "NotStartedNotify"};

        bannedTaskInputNames = Collections.unmodifiableList(Arrays.asList(bannedNames));
    }

    public static boolean isValidInputName(String inputName) {
        return !bannedTaskInputNames.contains(inputName);
    }

    public static String getRealTypeForInput(String inputType) {

        String type = StringUtils.defaultIfEmpty(inputType,
                                                 "java.lang.String");

        if (!type.contains(".")) {
            if ("Object".equals(type)) {
                return Object.class.getName();
            }
            if ("String".equals(type)) {
                return String.class.getName();
            }
            if ("Integer".equals(type)) {
                return Integer.class.getName();
            }
            if ("Short".equals(type)) {
                return Short.class.getName();
            }
            if ("Long".equals(type)) {
                return Long.class.getName();
            }
            if ("Float".equals(type)) {
                return Float.class.getName();
            }
            if ("Double".equals(type)) {
                return Double.class.getName();
            }
            if ("Boolean".equals(type)) {
                return Boolean.class.getName();
            }
            if ("Date".equals(type)) {
                return Date.class.getName();
            }
            if ("BigDecimal".equals(type)) {
                return BigDecimal.class.getName();
            }
            if ("BigInteger".equals(type)) {
                return BigInteger.class.getName();
            }
        }

        return type;
    }

    public static ModelProperty generateVariableProperty(String name, String type, ClassLoader classLoader) {
        return generateVariableProperty(name, type, false, classLoader);
    }

    public static ModelProperty generateVariableProperty(String name, String type, boolean readOnly, ClassLoader classLoader) {

        ModelProperty property = ModelPropertiesGenerator.createModelProperty(name,
                                                                              BPMNVariableUtils.getRealTypeForInput(type),
                                                                              classLoader);

        if(property != null) {
            if(readOnly) {
                property.getMetaData().addEntry(new FieldReadOnlyEntry(readOnly));
            }

            if (property.getTypeInfo().getClassName().equals(Object.class.getName())) {
                if (property.getTypeInfo().isMultiple()) {
                    property.getMetaData().addEntry(new FieldTypeEntry(MultipleInputFieldType.NAME));
                } else {
                    property.getMetaData().addEntry(new FieldTypeEntry(TextAreaFieldType.NAME));
                }
            }
        }

        return property;
    }
}
