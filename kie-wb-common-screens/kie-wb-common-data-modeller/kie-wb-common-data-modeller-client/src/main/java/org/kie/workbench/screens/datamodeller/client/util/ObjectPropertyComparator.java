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

package org.kie.workbench.screens.datamodeller.client.util;

import org.kie.workbench.screens.datamodeller.model.ObjectPropertyTO;
import java.util.Comparator;

public class ObjectPropertyComparator implements Comparator<ObjectPropertyTO> {
    
    String field;

    public ObjectPropertyComparator(String field) {
        this.field = field;
    }


    @Override
    public int compare(ObjectPropertyTO o1, ObjectPropertyTO o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null && o2 != null) return -1;
        if (o1 != null && o2 == null) return 1;

        String key1 = null;
        String key2 = null;

        if ("className".equals(field)) {
            key1 = o1.getClassName();
            key2 = o2.getClassName();
        } else {
            // By default compare by name
            key1 = o1.getName();
            key2 = o2.getName();
        }

        if (key1 != null) return key1.compareTo(key2);
        if (key2 != null) return key2.compareTo(key1);

        return 0;
    }
}