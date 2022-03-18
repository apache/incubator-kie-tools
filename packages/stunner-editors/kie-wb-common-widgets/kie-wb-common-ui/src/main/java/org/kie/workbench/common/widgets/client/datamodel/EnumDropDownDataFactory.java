/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.Map;

import org.kie.soup.project.datamodel.oracle.DropDownData;

import static org.kie.workbench.common.widgets.client.datamodel.DataModelHelper.*;

class EnumDropDownDataFactory {

    private FilteredEnumLists filteredEnumLists;
    private CurrentValues currentValues;

    EnumDropDownDataFactory(FilteredEnumLists filteredEnumLists,
                            Map<String, String> currentValueMap) {
        this.filteredEnumLists = filteredEnumLists;
        this.currentValues = new CurrentValues(currentValueMap);
    }

    public DropDownData getEnums(final String type,
                                 final String field) {

        // Check for data dependent enums
        if (!currentValues.isEmpty()) {
            DropDownData dropDownData = getDependentEnumDropDown(type, field);
            if (dropDownData != null) {
                return dropDownData;
            }
        }

        return DropDownData.create(filteredEnumLists.getEnumValues(type,
                                                                   field));
    }

    private DropDownData getDependentEnumDropDown(final String type,
                                                  final String field) {
        final Object _typeFields = filteredEnumLists.getTypeFields(type, field);

        if (_typeFields instanceof String) {
            final DropDownData data = DropDownData.create(filteredEnumLists.get(buildKey(type, field, (String) _typeFields)));
            if (data != null) {
                return data;
            }
        } else if (_typeFields != null) {
            // these enums are calculated on demand, server side...
            final String[] fieldsNeeded = (String[]) _typeFields;

            // collect all the values of the fields needed, then return it as a string...
            final String[] valuePairs = collectValuePairs(fieldsNeeded);

            if (isNotEmpty(valuePairs)) {
                return DropDownData.create(getQueryString(type,
                                                          field,
                                                          fieldsNeeded,
                                                          filteredEnumLists),
                                           valuePairs);
            }
        }

        return null;
    }

    private boolean isNotEmpty(String[] valuePairs) {
        return valuePairs.length > 0 && valuePairs[0] != null;
    }

    private String[] collectValuePairs(String[] fieldsNeeded) {
        final String[] valuePairs = new String[fieldsNeeded.length];
        for (int i = 0; i < fieldsNeeded.length; i++) {
            for (CurrentValueEntry currentValueEntry : currentValues) {
                if (currentValueEntry.getFieldName().equals(fieldsNeeded[i])) {
                    valuePairs[i] = fieldsNeeded[i] + "=" + currentValueEntry.getFieldValue();
                }
            }
        }
        return valuePairs;
    }

    private String buildKey(String type, String field, String _typeFields) {
        final String typeFields = _typeFields;
        final StringBuilder dataEnumListsKeyBuilder = new StringBuilder(type).append("#").append(field);

        boolean addOpeninColumn = true;
        final String[] splitTypeFields = typeFields.split(",");
        for (int j = 0; j < splitTypeFields.length; j++) {
            final String typeField = splitTypeFields[j];

            for (CurrentValueEntry currentValueEntry : currentValues) {
                if (currentValueEntry.getFieldName().trim().equals(typeField.trim())) {
                    if (addOpeninColumn) {
                        dataEnumListsKeyBuilder.append("[");
                        addOpeninColumn = false;
                    }
                    dataEnumListsKeyBuilder.append(typeField).append("=").append(currentValueEntry.getFieldValue());

                    if (j != (splitTypeFields.length - 1)) {
                        dataEnumListsKeyBuilder.append(",");
                    }
                }
            }
        }

        if (!addOpeninColumn) {
            dataEnumListsKeyBuilder.append("]");
        }
        return dataEnumListsKeyBuilder.toString();
    }

    private class CurrentValues
            extends ArrayList<CurrentValueEntry> {

        private CurrentValues() {
        }

        public CurrentValues(Map<String, String> currentValueMap) {
            for (Map.Entry<String, String> entry : currentValueMap.entrySet()) {
                add(new CurrentValueEntry(entry.getKey(), entry.getValue()));
            }
        }
    }

    private class CurrentValueEntry {

        private final String fieldName;
        private final String fieldValue;

        public CurrentValueEntry(String fieldName, String fieldValue) {

            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldValue() {
            return fieldValue;
        }
    }
}
