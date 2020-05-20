/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.List;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class DMNUtils {

    private DMNUtils() {
    }

    /**
     * This method return first buildInType found or top level Type of the given DMNType
     * @param dmnType
     * @return
     */
    public static Type getRootType(BaseDMNTypeImpl dmnType) {
        if (dmnType.getFeelType() instanceof BuiltInType) {
            return dmnType.getFeelType();
        } else if (dmnType.getBaseType() != null) {
            return getRootType((BaseDMNTypeImpl) dmnType.getBaseType());
        }
        return dmnType.getFeelType();
    }

    /**
     * This method returns the correct <b>type</b> name of a given <code>DMNType</code>. Basically, if a DMNType
     * contains a baseType, it takes the type name from its baseType. This is to manage DMN Simple Types and Anonymous
     * inner types
     * @param dmnType
     * @return
     */
    public static String getDMNTypeName(DMNType dmnType) {
        if (dmnType.getBaseType() != null) {
            return dmnType.getBaseType().getName();
        }
        return dmnType.getName();
    }

    public static DMNType navigateDMNType(DMNType rootType, List<String> steps) {
        DMNType toReturn = rootType;
        for (String step : steps) {
            if (!toReturn.getFields().containsKey(step)) {
                throw new IllegalStateException("Impossible to find field '" + step + "' in type '" + toReturn.getName() + "'");
            }
            toReturn = toReturn.getFields().get(step);
        }
        return toReturn;
    }
}
