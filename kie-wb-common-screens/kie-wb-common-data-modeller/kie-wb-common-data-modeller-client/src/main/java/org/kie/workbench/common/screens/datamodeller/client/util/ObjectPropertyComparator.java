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

import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
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

        Comparable key1 = null;
        Comparable key2 = null;

        if ("className".equals(field)) {
            key1 = o1.getClassName();
            key2 = o2.getClassName();
        } else if ("name".equals(field)){
            // By default compare by name
            key1 = o1.getName();
            key2 = o2.getName();
        } else if ("label".equals(field)) {
            key1 = AnnotationValueHandler.getInstance().getStringValue(o1, AnnotationDefinitionTO.LABEL_ANNOTATION, AnnotationDefinitionTO.VALUE_PARAM);
            key2 = AnnotationValueHandler.getInstance().getStringValue(o2, AnnotationDefinitionTO.LABEL_ANNOTATION, AnnotationDefinitionTO.VALUE_PARAM);
        } else if ("position".equals(field)) {
            key1 = AnnotationValueHandler.getInstance().getStringValue(o1, AnnotationDefinitionTO.POSITION_ANNOTATON, AnnotationDefinitionTO.VALUE_PARAM);
            key2 = AnnotationValueHandler.getInstance().getStringValue(o2, AnnotationDefinitionTO.POSITION_ANNOTATON, AnnotationDefinitionTO.VALUE_PARAM);
            if (key1 != null) {
                try {
                    key1 = new Integer(key1.toString());
                } catch (NumberFormatException e) {
                    key1 = null;
                }
            }
            if (key2 != null) {
                try {
                    key2 = new Integer(key2.toString());
                } catch (NumberFormatException e) {
                    key2 = null;
                }
            }
        }

        if (key1 == null && key2 == null) return 0;
        if (key1 != null && key2 != null) return key1.compareTo(key2);

        if (key1 == null && key2 != null) return -1;

        //if (key1 != null && key2 == null) return 1;
        return 1;

    }
}