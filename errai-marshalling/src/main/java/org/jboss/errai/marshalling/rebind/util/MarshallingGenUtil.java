/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.marshalling.rebind.util;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.config.rebind.EnvUtil;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.marshalling.client.api.Marshaller;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class MarshallingGenUtil {
  private static final String USE_STATIC_MARSHALLERS = "errai.marshalling.use_static_marshallers";
  private static final String FORCE_STATIC_MARSHALLERS = "errai.marshalling.force_static_marshallers";
  public static final String USE_SHORT_IMPL_NAMES = "errai.marshalling.short_names";
  public static final String USE_VERY_SHORT_IMPL_NAMES = "errai.marshalling.very_short_names";

  public static final String ARRAY_VAR_PREFIX = "arrayOf_";
  public static final String ERRAI_DOLLARSIGN_REPLACEMENT = ".erraiD.";
  public static final String ERRAI_UNDERSCORE_REPLACEMENT = ".erraiU.";

  public static String getVarName(final MetaClass clazz) {
    return clazz.isArray()
            ? getArrayVarName(clazz.getOuterComponentType().getFullyQualifiedName())
                + "_D" + GenUtil.getArrayDimensions(clazz)
            : getVarName(clazz.asBoxed().getFullyQualifiedName());
  }

  public static String getVarName(final Class<?> clazz) {
    return getVarName(MetaClassFactory.get(clazz));
  }

  public static String getArrayVarName(final String clazz) {
    return ARRAY_VAR_PREFIX + normalizeName(clazz);
  }

  public static String getVarName(final String clazz) {
    return normalizeName(clazz);
  }

  private static String normalizeName(final String sourceString) {
    String result = sourceString;
    result = StringUtils.replace(result, "_", ERRAI_UNDERSCORE_REPLACEMENT);
    result = StringUtils.replace(result, "$", ERRAI_DOLLARSIGN_REPLACEMENT);
    result = StringUtils.replace(result, ".", "_");
    return result;
  }

  public static MetaMethod findGetterMethod(final MetaClass cls, final String key) {
    MetaMethod metaMethod = _findGetterMethod("get", cls, key);
    if (metaMethod != null)
      return metaMethod;
    metaMethod = _findGetterMethod("is", cls, key);
    return metaMethod;
  }

  private static MetaMethod _findGetterMethod(final String prefix, final MetaClass cls, String key) {
    key = (prefix + key).toUpperCase();
    for (final MetaMethod m : cls.getDeclaredMethods()) {
      if (m.getName().toUpperCase().equals(key) && m.getParameters().length == 0) {
        return m;
      }
    }
    return null;
  }

  /**
   * Returns the element type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType is a collection type
   * <li>toType has a single type parameter
   * <li>toType's type parameter is not a wildcard
   * <li>toType's type parameter is a non-abstract (concrete) type
   * <li>toType's type parameter is not java.lang.Object
   * </ul>
   *
   * @param toType
   *          The type to check for a known concrete collection element type.
   * @return The concrete element type meeting all above-mentioned criteria, or null if one or more
   *         of the criteria fails.
   */
  public static MetaClass getConcreteCollectionElementType(final MetaClass toType) {
    if (toType.isAssignableTo(Collection.class)) {
      return getConcreteElementType(toType);
    }
    return null;
  }

  /**
   * Returns the element type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType has a single type parameter
   * <li>toType's type parameter is not a wildcard
   * <li>toType's type parameter is a non-abstract (concrete) type
   * <li>toType's type parameter is not java.lang.Object
   * </ul>
   *
   * @param toType
   *          The type to check for a known concrete collection element type.
   * @return The concrete element type meeting all above-mentioned criteria, or null if one or more
   *         of the criteria fails.
   */
  public static MetaClass getConcreteElementType(final MetaClass toType) {
    return getConcreteTypeParameter(toType, 0, 1);
  }

  /**
   * Returns the map key type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType is a {@link Map}
   * <li>toType's key type is not a wildcard
   * <li>toType's key type is a non-abstract (concrete) type
   * </ul>
   *
   * @param toType
   *          The type to check for a known concrete map key type.
   * @return The concrete map key type meeting all above-mentioned criteria, or null if one or more
   *         of the criteria fails.
   */
  public static MetaClass getConcreteMapKeyType(final MetaClass toType) {
    if (toType.isAssignableTo(Map.class)) {
      return getConcreteTypeParameter(toType, 0, 2);
    }
    return null;
  }

  /**
   * Returns the map value type of the given metaclass under the following conditions:
   * <ul>
   * <li>toType is a {@link Map}
   * <li>toType's value type is not a wildcard
   * <li>toType's value type is a non-abstract (concrete) type
   * </ul>
   *
   * @param toType
   *          The type to check for a known concrete map key type.
   * @return The concrete map value type meeting all above-mentioned criteria, or null if one or
   *         more of the criteria fails.
   */
  public static MetaClass getConcreteMapValueType(final MetaClass toType) {
    if (toType.isAssignableTo(Map.class)) {
      return getConcreteTypeParameter(toType, 1, 2);
    }
    return null;
  }

  private static MetaClass getConcreteTypeParameter(final MetaClass toType, final int typeParamIndex, final int typeParamsSize) {
    if (toType.getParameterizedType() != null) {
      final MetaType[] typeParms = toType.getParameterizedType().getTypeParameters();
      if (typeParms != null && typeParms.length == typeParamsSize) {

        MetaClass typeParameter = null;
        if (typeParms[typeParamIndex] instanceof MetaParameterizedType) {
          final MetaParameterizedType parameterizedTypeParemeter = (MetaParameterizedType) typeParms[typeParamIndex];
          typeParameter = (MetaClass) parameterizedTypeParemeter.getRawType();
        }
        else if (typeParms[typeParamIndex] instanceof MetaClass) {
          typeParameter = (MetaClass) typeParms[typeParamIndex];
        }

        return typeParameter;
      }
    }
    return null;
  }

  public static Collection<MetaClass> getDefaultArrayMarshallers() {
    final List<MetaClass> l = new ArrayList<>();

    l.add(MetaClassFactory.get(Object[].class));
    l.add(MetaClassFactory.get(String[].class));
    l.add(MetaClassFactory.get(int[].class));
    l.add(MetaClassFactory.get(long[].class));
    l.add(MetaClassFactory.get(double[].class));
    l.add(MetaClassFactory.get(float[].class));
    l.add(MetaClassFactory.get(short[].class));
    l.add(MetaClassFactory.get(boolean[].class));
    l.add(MetaClassFactory.get(byte[].class));
    l.add(MetaClassFactory.get(char[].class));

    l.add(MetaClassFactory.get(Integer[].class));
    l.add(MetaClassFactory.get(Long[].class));
    l.add(MetaClassFactory.get(Double[].class));
    l.add(MetaClassFactory.get(Float[].class));
    l.add(MetaClassFactory.get(Short[].class));
    l.add(MetaClassFactory.get(Boolean[].class));
    l.add(MetaClassFactory.get(Byte[].class));
    l.add(MetaClassFactory.get(Character[].class));

    return Collections.unmodifiableCollection(l);
  }

  public static boolean isUseStaticMarshallers() {
    if (isForceStaticMarshallers())
      return true;

    if (EnvUtil.isDevMode() && !EnvUtil.isJUnitTest())
      return false;

    if (System.getProperty(USE_STATIC_MARSHALLERS) != null) {
      return Boolean.getBoolean(USE_STATIC_MARSHALLERS);
    }

    final Map<String, String> frameworkProperties = EnvUtil.getEnvironmentConfig().getFrameworkProperties();
    if (frameworkProperties.containsKey(USE_STATIC_MARSHALLERS)) {
      return "true".equals(frameworkProperties.get(USE_STATIC_MARSHALLERS));
    }
    else {
      return !EnvUtil.isDevMode();
    }
  }

  public static boolean isForceStaticMarshallers() {
    if (System.getProperty(FORCE_STATIC_MARSHALLERS) != null) {
      return Boolean.getBoolean(FORCE_STATIC_MARSHALLERS);
    }

    final Map<String, String> frameworkProperties = EnvUtil.getEnvironmentConfig().getFrameworkProperties();
    if (frameworkProperties.containsKey(FORCE_STATIC_MARSHALLERS)) {
      return "true".equals(frameworkProperties.get(FORCE_STATIC_MARSHALLERS));
    }
    else {
      return false;
    }
  }

  public static void ensureMarshallerFieldCreated(final ClassStructureBuilder<?> classStructureBuilder,
      final MetaClass marshallerForType, final MetaClass type, final BlockBuilder<?> initMethod) {
    ensureMarshallerFieldCreated(classStructureBuilder, marshallerForType, type, initMethod, null);
  }

  public static void ensureMarshallerFieldCreated(final ClassStructureBuilder<?> classStructureBuilder,
      final MetaClass marshallerForType, final MetaClass type, final BlockBuilder<?> initMethod,
      final Statement marshallerCreationCallback) {
    final String fieldName = MarshallingGenUtil.getVarName(type);

    if (classStructureBuilder.getClassDefinition().getField(fieldName) == null) {
      final Statement marshallerLookup = createMarshallerLookup(marshallerForType, type, marshallerCreationCallback);
      if (initMethod == null) {
        classStructureBuilder.privateField(fieldName,
            parameterizedAs(Marshaller.class, typeParametersOf(type.getErased().asBoxed())))
            .initializesWith(marshallerLookup).finish();
      }
      else {
        classStructureBuilder.privateField(fieldName,
            parameterizedAs(Marshaller.class, typeParametersOf(type.getErased().asBoxed())))
            .initializesWith(Stmt.load(null)).finish();

        initMethod.append(
            If.isNull(Stmt.loadVariable(fieldName)).append(
                Stmt.loadVariable(fieldName).assignValue(marshallerLookup)).finish());
      }
    }
  }

  private static Statement createMarshallerLookup(final MetaClass marshallerForType, final MetaClass type,
      final Statement marshallerCreationCallback) {
    Statement marshallerLookup = null;

    if (type.equals(marshallerForType)) {
      marshallerLookup = Stmt.loadVariable("this");
    }
    else if (marshallerCreationCallback == null) {
      marshallerLookup =
          Stmt.invokeStatic(Marshalling.class, "getMarshaller", Stmt.loadLiteral(type.asBoxed()));
    }
    else {
      marshallerLookup =
          Stmt.invokeStatic(Marshalling.class, "getMarshaller",
              Stmt.loadLiteral(type.asBoxed().asClass()), marshallerCreationCallback);
    }

    return marshallerLookup;
  }
}
