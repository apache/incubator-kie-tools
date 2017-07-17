package org.kie.workbench.common.forms.service.backend.util;

import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.model.util.ModelPropertiesUtil;
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

        if (ModelPropertiesUtil.isListType(className)) {
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
        if (ModelPropertiesUtil.isBaseType(className)) {
            // Dealing with basic type properties (String, Integer...)
            return new ModelPropertyImpl(name,
                                         new TypeInfoImpl(className,
                                                          isMultiple));
        } else {
            // Dealing with complex types.
            if (ModelPropertiesUtil.isListType(className)) {
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
