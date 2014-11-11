/**
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.util;

import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.KeyAnnotationDefinition;

public class DataModelUtils {

    /**
     * @return true if a given property is assignable and thus can be used in setter/getter methods and constructors.
     */
    public static boolean isAssignable( ObjectProperty property ) {
        return property != null && !property.isFinal() && !property.isStatic();
    }

    /**
     * @return true if a given property is marked as a key field property.
     */
    public static boolean isKeyField(ObjectProperty property) {
        return property != null && property.getAnnotation(KeyAnnotationDefinition.getInstance().getClassName()) != null;
    }

    public static int keyFieldsCount( DataObject dataObject ) {
        int result = 0;
        for (ObjectProperty property : dataObject.getProperties().values()) {
            if ( isKeyField( property ) ) {
                result++;
            }
        }
        return result;
    }

    public static int assignableFieldsCount( DataObject dataObject ) {
        int result = 0;
        for (ObjectProperty property : dataObject.getProperties().values()) {
            if ( isAssignable( property ) ) {
                result++;
            }
        }
        return result;
    }

}
