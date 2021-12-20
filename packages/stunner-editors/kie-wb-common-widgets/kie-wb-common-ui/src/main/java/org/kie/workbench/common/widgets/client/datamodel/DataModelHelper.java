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

import java.util.Iterator;
import java.util.Map;

class DataModelHelper {

    /**
     * Get the query string for a fact.field It will ignore any specified field,
     * and just look for the string - as there should only be one Fact.field of
     * this type (it is all determined server side).
     * @param fieldsNeeded
     */
    static String getQueryString(final String factType,
                                 final String field,
                                 final String[] fieldsNeeded,
                                 final Map<String, String[]> dataEnumLists) {
        for (Iterator<String> iterator = dataEnumLists.keySet().iterator(); iterator.hasNext(); ) {
            final String key = iterator.next();
            if (key.startsWith(factType + "#" + field) && fieldsNeeded != null && key.contains("[")) {

                final String[] values = key.substring(key.indexOf('[') + 1,
                                                      key.lastIndexOf(']')).split(",");

                if (values.length != fieldsNeeded.length) {
                    continue;
                }

                boolean fail = false;
                for (int i = 0; i < values.length; i++) {
                    final String a = values[i].trim();
                    final String b = fieldsNeeded[i].trim();
                    if (!a.equals(b)) {
                        fail = true;
                        break;
                    }
                }
                if (fail) {
                    continue;
                }

                final String[] qry = dataEnumLists.get(key);
                return qry[0];
            } else if (key.startsWith(factType + "#" + field) && (fieldsNeeded == null || fieldsNeeded.length == 0)) {
                final String[] qry = dataEnumLists.get(key);
                return qry[0];
            }
        }
        throw new IllegalStateException();
    }
}
