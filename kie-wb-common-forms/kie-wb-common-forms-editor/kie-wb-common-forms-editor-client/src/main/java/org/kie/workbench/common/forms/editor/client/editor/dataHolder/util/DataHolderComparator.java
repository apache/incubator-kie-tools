/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.dataHolder.util;

import java.util.Comparator;

import org.kie.workbench.common.forms.model.DataHolder;

public class DataHolderComparator implements Comparator<DataHolder> {

    String field;

    public DataHolderComparator(String field) {
        this.field = field;
    }


    @Override
    public int compare(DataHolder o1, DataHolder o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null && o2 != null) return -1;
        if (o1 != null && o2 == null) return 1;

        Comparable key1 = null;
        Comparable key2 = null;

        if ("name".equals(field)){
            // By default compare by name
            key1 = o1.getName();
            key2 = o2.getName();
        } else if ("type".equals(field)) {
            key1 = o1.getType();
            key2 = o2.getType();
        }

        if (key1 == null && key2 == null) return 0;
        if (key1 != null && key2 != null) return key1.compareTo(key2);

        if (key1 == null && key2 != null) return -1;

        //if (key1 != null && key2 == null) return 1;
        return 1;

    }
}
