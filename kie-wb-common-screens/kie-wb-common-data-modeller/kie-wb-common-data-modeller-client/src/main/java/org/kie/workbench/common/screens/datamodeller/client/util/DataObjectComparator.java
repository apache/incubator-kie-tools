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

package org.kie.workbench.common.screens.datamodeller.client.util;

import java.util.Comparator;

import org.kie.workbench.common.services.datamodeller.core.DataObject;

public class DataObjectComparator implements Comparator <DataObject> {

    @Override
    public int compare(DataObject o1, DataObject o2) {

        if (o1 == null && o2 == null) return 0;
        if (o1 == null && o2 != null) return -1;
        if (o1 != null && o2 == null) return 1;

        String key1 = DataModelerUtils.getDataObjectUILabel(o1);
        String key2 = DataModelerUtils.getDataObjectUILabel(o2);

        if (key1 != null) return key1.compareTo(key2);
        if (key2 != null) return key2.compareTo(key1);
        return 0;
    }
}
