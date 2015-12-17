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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Filtered (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
 */
public class FilteredEnumLists
        extends HashMap<String, String[]> {

    // This is used to calculate what fields an enum list may depend on.
    private transient Map<String, Object> enumLookupFields;

    /**
     * For simple cases - where a list of values are known based on a field.
     */
    public String[] getEnumValues(final String factType,
                                  final String field) {
        return this.get(factType + "#" + field);
    }

    Object getTypeFields(String type, String field) {
        return loadDataEnumLookupFields().get(type + "#" + field);
    }

    boolean isDependentEnum(final String factType,
                            final String parentField,
                            final String childField) {
        final Map<String, Object> enums = loadDataEnumLookupFields();
        if (enums.isEmpty()) {
            return false;
        }
        //Check if the childField is a direct descendant of the parentField
        final String key = factType + "#" + childField;
        if (!enums.containsKey(key)) {
            return false;
        }

        //Otherwise follow the dependency chain...
        final Object _parent = enums.get(key);
        if (_parent instanceof String) {
            final String _parentField = (String) _parent;
            if (_parentField.equals(parentField)) {
                return true;
            } else {
                return isDependentEnum(factType,
                                       parentField,
                                       _parentField);
            }
        }
        return false;
    }

    /**
     * This is only used by enums that are like Fact.field[something=X] and so on.
     */
    Map<String, Object> loadDataEnumLookupFields() {
        if (enumLookupFields == null) {
            enumLookupFields = new HashMap<String, Object>();
            final Set<String> keys = keySet();
            for (String key : keys) {
                if (key.indexOf('[') != -1) {
                    int ix = key.indexOf('[');
                    final String factField = key.substring(0,
                                                           ix);
                    final String predicate = key.substring(ix + 1,
                                                           key.indexOf(']'));
                    if (predicate.indexOf('=') > -1) {

                        final String[] bits = predicate.split(",");
                        final StringBuilder typeFieldBuilder = new StringBuilder();

                        for (int i = 0; i < bits.length; i++) {
                            typeFieldBuilder.append(bits[i].substring(0,
                                                                      bits[i].indexOf('=')));
                            if (i != (bits.length - 1)) {
                                typeFieldBuilder.append(",");
                            }
                        }

                        enumLookupFields.put(factField,
                                             typeFieldBuilder.toString());
                    } else {
                        final String[] fields = predicate.split(",");
                        for (int i = 0; i < fields.length; i++) {
                            fields[i] = fields[i].trim();
                        }
                        enumLookupFields.put(factField,
                                             fields);
                    }
                }
            }
        }

        return enumLookupFields;
    }

    boolean hasEnums(final String qualifiedFactField) {
        boolean hasEnums = false;
        final String key = qualifiedFactField.replace(".",
                                                      "#");
        final String dependentType = key + "[";
        for (String e : keySet()) {
            //e.g. Fact.field1
            if (e.equals(key)) {
                return true;
            }
            //e.g. Fact.field2[field1=val2]
            if (e.startsWith(dependentType)) {
                return true;
            }
        }
        return hasEnums;
    }
}
