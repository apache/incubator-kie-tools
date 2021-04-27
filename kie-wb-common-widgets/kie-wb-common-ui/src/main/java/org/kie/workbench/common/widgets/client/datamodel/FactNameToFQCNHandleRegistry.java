/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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

public class FactNameToFQCNHandleRegistry {

    Map<String, String> map = new HashMap<String, String>();

    public void add(String mfClassName_typeName, String mfClassName_qualifiedType) {
        map.put(mfClassName_typeName, mfClassName_qualifiedType);
    }

    public String get(String factName) {
        return map.get(factName);
    }

    public boolean contains(String mfTypeName) {
        return map.keySet().contains(mfTypeName);
    }
}
