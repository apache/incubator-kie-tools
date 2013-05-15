/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.util;

import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;

import java.util.Comparator;

public class DataObjectComparator implements Comparator <DataObjectTO> {

    @Override
    public int compare(DataObjectTO o1, DataObjectTO o2) {

        if (o1 == null && o2 == null) return 0;
        if (o1 == null && o2 != null) return -1;
        if (o1 != null && o2 == null) return 1;

        String key1 = o1.getName();
        String key2 = o2.getName();

        if (key1 != null) return key1.compareTo(key2);
        if (key2 != null) return key2.compareTo(key1);
        return 0;
    }
}
