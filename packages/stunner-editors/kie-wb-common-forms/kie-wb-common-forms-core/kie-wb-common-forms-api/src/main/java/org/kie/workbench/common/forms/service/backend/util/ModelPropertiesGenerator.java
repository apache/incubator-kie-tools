/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.forms.service.backend.util;

import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelPropertiesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ModelPropertiesGenerator.class);

    public static ModelProperty createModelProperty(String name,
                                                    String type,
                                                    boolean multiple) {
        return createModelProperty(name,
                                   type,
                                   multiple,
                                   ClassLoader.getSystemClassLoader());
    }

    public static ModelProperty createModelProperty(String name,
                                                    String className,
                                                    ClassLoader classLoader) {

        if (FormModelPropertiesUtil.isListType(className)) {
            return createModelProperty(name,
                                       Object.class.getName(),
                                       true,
                                       classLoader);
        }
        return createModelProperty(name,
                                   className,
                                   false,
                                   classLoader);
    }

    public static ModelProperty createModelProperty(String name,
                                                    String className,
                                                    boolean isMultiple,
                                                    ClassLoader classLoader) {
        if (FormModelPropertiesUtil.isBaseType(className)) {
            // Dealing with basic type properties (String, Integer...)
            return new ModelPropertyImpl(name,
                                         new TypeInfoImpl(className,
                                                          isMultiple));
        } else {
            // Dealing with complex types.
            if (FormModelPropertiesUtil.isListType(className)) {
                // If className is a List let's create a model for Object...
                return createModelProperty(name,
                                           Object.class.getName(),
                                           true);
            }
            try {
                Class clazz = classLoader.loadClass(className);

                TypeKind typeKind = clazz.isEnum() ? TypeKind.ENUM : TypeKind.OBJECT;

                return new ModelPropertyImpl(name,
                                             new TypeInfoImpl(typeKind,
                                                              className,
                                                              isMultiple));
            } catch (ClassNotFoundException e) {
                logger.warn("Unable to create property '" + name + "' for class '" + className + "':",
                            e);
            }
        }
        return null;
    }
}
