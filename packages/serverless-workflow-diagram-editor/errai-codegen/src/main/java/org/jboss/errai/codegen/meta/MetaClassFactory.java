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


package org.jboss.errai.codegen.meta;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.builder.callstack.LoadClassReference;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaConstructor;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaField;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaMethod;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaParameterizedType;
import org.jboss.errai.codegen.meta.impl.build.ShadowBuildMetaField;
import org.jboss.errai.codegen.meta.impl.build.ShadowBuildMetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.codegen.util.ClassChangeUtil;
import org.jboss.errai.codegen.util.EmptyStatement;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.common.rebind.CacheUtil;
import org.mvel2.ConversionHandler;
import org.mvel2.DataConversion;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public final class MetaClassFactory {
  static {
    DataConversion.addConversionHandler(Class.class, new ConversionHandler() {
      @Override
      public Object convertFrom(final Object in) {
        if (MetaClass.class.isAssignableFrom(in.getClass())) {
          return ((MetaClass) in).asClass();
        }
        else {
          throw new RuntimeException("cannot convert from " + in.getClass() + "; to " + Class.class.getName());
        }
      }

      @Override
      public boolean canConvertFrom(final Class cls) {
        return MetaType.class.isAssignableFrom(cls);
      }
    });

    DataConversion.addConversionHandler(Class[].class, new ConversionHandler() {
      @Override
      public Object convertFrom(final Object in) {
        final int length = Array.getLength(in);
        final Class[] cls = new Class[length];

        for (int i = 0; i < length; i++) {
          final Object el = Array.get(in, i);

          if (el instanceof MetaClass) {
            cls[i] = ((MetaClass) el).asClass();
          }
          else if (el != null && el.getClass().isArray()) {
            cls[i] = (Class) convertFrom(el);
          }
          else {
            cls[i] = null;
          }
        }
        return cls;
      }

      @Override
      public boolean canConvertFrom(Class cls) {
        while (cls.isArray()) {
          cls = cls.getComponentType();
        }
        return MetaClass.class.isAssignableFrom(cls) || MetaType.class.isAssignableFrom(cls);
      }
    });
  }

  private static MetaClassCache cache;

  public static MetaClassCache getMetaClassCache() {
    if (cache == null) {
      cache = CacheUtil.getCache(MetaClassCache.class);
    }
    return cache;
  }

  public static MetaClass get(final String fullyQualifiedClassName, final boolean erased) {
    return createOrGet(fullyQualifiedClassName, erased);
  }

  public static MetaClass get(final String fullyQualifiedClassName) {
    return createOrGet(fullyQualifiedClassName);
  }

  public static MetaClass get(final Class<?> clazz) {
    if (clazz == null)
      return null;
    return createOrGet(clazz, null);
  }

  public static MetaClass getArrayOf(final Class<?> clazz, final int dims) {
    final int[] da = new int[dims];
    for (int i = 0; i < da.length; i++) {
      da[i] = 0;
    }

    return getArrayOf(clazz, da);
  }

  public static MetaClass getArrayOf(final Class<?> clazz, int... dims) {
    if (dims.length == 0) {
      dims = new int[1];
    }
    return JavaReflectionClass.newInstance(Array.newInstance(clazz, dims).getClass());
  }

  public static MetaClass get(final Class<?> clazz, final Type type) {
    return createOrGet(clazz, type);
  }

  public static MetaClass get(final TypeLiteral<?> literal) {
    return createOrGet(literal);
  }

  public static MetaMethod get(final Method method) {
    return get(method.getDeclaringClass()).getDeclaredMethod(method.getName(), method.getParameterTypes());
  }

  public static MetaField get(final Field field) {
    return get(field.getDeclaringClass()).getDeclaredField(field.getName());
  }

  public static Statement getAsStatement(final Class<?> clazz) {
    return getAsStatement(createOrGet(clazz.getName(), false));
  }

  public static Statement getAsStatement(final MetaClass metaClass) {
    return new Statement() {
      @Override
      public String generate(final Context context) {
        return LoadClassReference.getClassReference(metaClass, context);
      }

      @Override
      public MetaClass getType() {
        return MetaClassFactory.get(Class.class);
      }
    };
  }

  public static boolean isCached(final String name) {
    return getMetaClassCache().isKnownErasedType(name);
  }

  private static MetaClass createOrGet(final String fullyQualifiedClassName) {
    if (!getMetaClassCache().isKnownErasedType(fullyQualifiedClassName)) {
      return createOrGet(fullyQualifiedClassName, false);
    }

    return getMetaClassCache().getErased(fullyQualifiedClassName);
  }

  private static MetaClass createOrGet(final TypeLiteral type) {
    if (type == null)
      return null;

    if (!getMetaClassCache().isKnownErasedType(type.toString())) {
      final MetaClass gwtClass = JavaReflectionClass.newUncachedInstance(type);

      addLookups(type, gwtClass);
      return gwtClass;
    }

    return getMetaClassCache().getErased(type.toString());
  }

  private static MetaClass createOrGet(final String clsName, final boolean erased) {
    if (clsName == null)
      return null;

    MetaClass mCls;
    if (erased) {
      mCls = getMetaClassCache().getErased(clsName);
      if (mCls == null) {
        getMetaClassCache().pushErasedCache(clsName, mCls = JavaReflectionClass.newUncachedInstance(loadClass(clsName), erased));
      }
    }
    else {
      mCls = getMetaClassCache().get(clsName);
      if (mCls == null) {
        getMetaClassCache().pushCache(clsName, mCls = JavaReflectionClass.newUncachedInstance(loadClass(clsName), erased));
      }
    }
    return mCls;
  }

  private static MetaClass createOrGet(final Class cls, final Type type) {
    if (cls == null)
      return null;

    if (type != null) {
      if (cls.getTypeParameters() != null) {
        return JavaReflectionClass.newUncachedInstance(cls, type);
      }

      if (!getMetaClassCache().isKnownErasedType(cls.getName())) {
        final MetaClass javaReflectionClass = JavaReflectionClass.newUncachedInstance(cls, type);
        addLookups(cls, javaReflectionClass);
        return javaReflectionClass;
      }

      return getMetaClassCache().getErased(cls.getName());
    }
    else {
      MetaClass mCls;
      mCls = getMetaClassCache().get(cls.getName());
      if (mCls == null) {
        getMetaClassCache().pushCache(cls.getName(), mCls = JavaReflectionClass.newUncachedInstance(cls));
      }
      return mCls;
    }
  }

  public static MetaClass parameterizedAs(final Class clazz, final MetaParameterizedType parameterizedType) {
    return parameterizedAs(MetaClassFactory.get(clazz), parameterizedType);
  }

  public static MetaClass parameterizedAs(final MetaClass clazz, final MetaParameterizedType parameterizedType) {
    return parameterizedAs(clazz, parameterizedType, true);
  }

  public static MetaClass parameterizedAs(final MetaClass clazz,
                                          final MetaParameterizedType parameterizedType,
                                          final boolean reifyRecursively) {
    return cloneToBuildMetaClass(clazz, parameterizedType, reifyRecursively);
  }

  private static BuildMetaClass cloneToBuildMetaClass(final MetaClass clazz) {
    return cloneToBuildMetaClass(clazz, null, false);
  }

  private static BuildMetaClass cloneToBuildMetaClass(final MetaClass clazz,
                                                      final MetaParameterizedType parameterizedType,
                                                      final boolean reifyRecursively) {
    final BuildMetaClass buildMetaClass = new BuildMetaClass(null, clazz.getFullyQualifiedName());

    buildMetaClass.setReifiedFormOf(clazz);
    buildMetaClass.setAbstract(clazz.isAbstract());
    buildMetaClass.setFinal(clazz.isFinal());
    buildMetaClass.setStatic(clazz.isStatic());
    buildMetaClass.setInterface(clazz.isInterface());
    addInterfaces(clazz, buildMetaClass, parameterizedType);
    buildMetaClass.setScope(GenUtil.scopeOf(clazz));
    buildMetaClass.setSuperClass(clazz.getSuperClass());

    for (final MetaTypeVariable typeVariable : clazz.getTypeParameters()) {
      buildMetaClass.addTypeVariable(typeVariable);
    }

    if (parameterizedType != null) {
      buildMetaClass.setParameterizedType(parameterizedType);
    }
    else {
      buildMetaClass.setParameterizedType(clazz.getParameterizedType());
    }

    for (final MetaField field : clazz.getDeclaredFields()) {
      final BuildMetaField bmf = new ShadowBuildMetaField(buildMetaClass, EmptyStatement.INSTANCE,
          GenUtil.scopeOf(field), field.getType(), field.getName(), field);

      bmf.setFinal(field.isFinal());
      bmf.setStatic(field.isStatic());
      bmf.setVolatile(field.isVolatile());
      bmf.setTransient(field.isTransient());

      buildMetaClass.addField(bmf);
    }

    for (final MetaConstructor c : clazz.getDeclaredConstructors()) {
      final BuildMetaConstructor newConstructor = new BuildMetaConstructor(buildMetaClass, EmptyStatement.INSTANCE,
          GenUtil.scopeOf(c),
          DefParameters.from(c));
      newConstructor.setReifiedFormOf(c);

      buildMetaClass.addConstructor(newConstructor);
    }

    for (final MetaMethod method : clazz.getDeclaredMethods()) {

      MetaClass returnType = method.getReturnType();
      if (method.getGenericReturnType() instanceof MetaTypeVariable) {
        final MetaTypeVariable typeVariable = (MetaTypeVariable) method.getGenericReturnType();
        final MetaClass tVarVal = getTypeVariableValue(typeVariable, buildMetaClass);
        if (tVarVal != null) {
          returnType = tVarVal;
        }
      }
      else if (method.getGenericReturnType() instanceof MetaParameterizedType) {
        final MetaParameterizedType metaParameterizedType
            = (MetaParameterizedType) method.getGenericReturnType();

        final List<MetaType> typeVarValues = new ArrayList<MetaType>();
        boolean defaultOnly = true;
        for (final MetaType metaType : metaParameterizedType.getTypeParameters()) {
          if (metaType instanceof MetaTypeVariable) {
            final MetaTypeVariable typeVariable = (MetaTypeVariable) metaType;
            final MetaClass tVarVar = getTypeVariableValue(typeVariable, buildMetaClass);
            if (tVarVar != null) {
              defaultOnly = false;
              typeVarValues.add(tVarVar);
            }
            else {
              typeVarValues.add(MetaClassFactory.get(Object.class));
            }
          }
          else {
            typeVarValues.add(metaType);
          }
        }

        if (reifyRecursively && !defaultOnly) {
          returnType = parameterizedAs(returnType, typeParametersOf(typeVarValues.toArray(new MetaType[typeVarValues.size()])), false);
        }
      }

      final List<Parameter> parameters = new ArrayList<Parameter>();
      int i = 0;
      for (final MetaParameter parm : method.getParameters()) {
        MetaClass parmType = null;
        if (method.getGenericParameterTypes() != null) {
          if (method.getGenericParameterTypes()[i] instanceof MetaTypeVariable) {
            final MetaTypeVariable typeVariable = (MetaTypeVariable) method.getGenericParameterTypes()[i];

            final MetaClass tVarVal = getTypeVariableValue(typeVariable, buildMetaClass);
            if (tVarVal != null) {
              parmType = tVarVal;
            }
          }
        }

        if (parmType == null) {
          parmType = parm.getType();
        }

        parameters.add(Parameter.of(parmType, parm.getName()));
        i++;
      }

      final BuildMetaMethod newMethod = new ShadowBuildMetaMethod(buildMetaClass, BlockStatement.EMPTY_BLOCK,
          GenUtil.scopeOf(method), GenUtil.modifiersOf(method), method.getName(), returnType,
          DefParameters.fromParameters(parameters), ThrowsDeclaration.of(method.getCheckedExceptions()), method);

      newMethod.setReifiedFormOf(method);

      buildMetaClass.addMethod(newMethod);
    }

    return buildMetaClass;
  }

  private static void addInterfaces(final MetaClass clazz, final BuildMetaClass buildMetaClass, final MetaParameterizedType parameterizedType) {
    final MetaType[] typeParams = (clazz.getParameterizedType() != null ? clazz.getParameterizedType().getTypeParameters() : clazz.getTypeParameters());
    final MetaType[] typeArgs = parameterizedType.getTypeParameters();
    validateTypeArgumentsAgainstParameters(clazz, typeParams, typeArgs);

    final Map<String, MetaType> typeArgsByTypeParam = mapTypeArgsByTypeParamName(typeParams, typeArgs);
    final List<MetaClass> ifaces = Arrays
            .stream(clazz.getInterfaces())
            .map(iface -> getParameterizedInterface(typeArgsByTypeParam, iface))
            .collect(Collectors.toList());
    buildMetaClass.setInterfaces(ifaces);
  }

  private static BuildMetaClass getParameterizedInterface(final Map<String, MetaType> typeArgsByTypeParam, final MetaClass iface) {
    final MetaType[] ifaceTypeArgs = getTypeArgumentsForInterface(typeArgsByTypeParam, iface);
    return cloneToBuildMetaClass(iface, typeParametersOf(ifaceTypeArgs), true);
  }

  private static MetaType[] getTypeArgumentsForInterface(final Map<String, MetaType> typeArgsByTypeParam, final MetaClass iface) {
    return Arrays
      .stream(Optional.ofNullable(iface.getParameterizedType())
                      .map(i -> i.getTypeParameters())
                      .orElseGet(() -> iface.getTypeParameters()))
      .map(mt -> {
        if (typeArgsByTypeParam.containsKey(mt.getName())) {
          return typeArgsByTypeParam.get(mt.getName());
        }
        else {
          return mt;
        }
    }).toArray(size -> new MetaType[size]);
  }

  private static Map<String, MetaType> mapTypeArgsByTypeParamName(final MetaType[] typeParams, final MetaType[] typeArgs) {
    final Map<String, MetaType> typeArgsByTypeParam = new HashMap<>();
    for (int i = 0; i < typeParams.length; i++) {
      typeArgsByTypeParam.put(typeParams[i].getName(), typeArgs[i]);
    }
    return typeArgsByTypeParam;
  }

  private static void validateTypeArgumentsAgainstParameters(final MetaClass clazz, final MetaType[] typeParams, final MetaType[] assignedTypes) {
    if (typeParams.length != assignedTypes.length) {
      final String message = "Number of provided types does not match the number of type parameters for " + clazz.getFullyQualifiedName()
      + ".\nType parameters: " + Arrays.toString(typeParams)
      + "\nProvided types: " + Arrays.toString(assignedTypes);
      throw new IllegalArgumentException(message);
    }
  }

  private static MetaClass getTypeVariableValue(final MetaTypeVariable typeVariable, final MetaClass clazz) {
    int idx = -1;
    final MetaTypeVariable[] typeVariables = clazz.getTypeParameters();
    for (int i = 0; i < typeVariables.length; i++) {
      if (typeVariables[i].getName().equals(typeVariable.getName())) {
        idx = i;
        break;
      }
    }

    if (idx != -1) {
      final MetaType type = clazz.getParameterizedType().getTypeParameters()[idx];
      if (type instanceof MetaClass) {
        return (MetaClass) type;
      }
    }
    return null;
  }

  public static MetaParameterizedType typeParametersOf(final Object... classes) {
    final MetaType[] types = new MetaType[classes.length];
    int i = 0;
    for (final Object o : classes) {
      if (o instanceof Class) {
        types[i++] = MetaClassFactory.get((Class) o);
      }
      else if (o instanceof TypeLiteral) {
        types[i++] = MetaClassFactory.get((TypeLiteral) o);
      }
      else if (o instanceof MetaType) {
        types[i++] = (MetaType) o;
      }
      else {
        throw new RuntimeException("not a recognized type reference: " + o.getClass().getName());
      }
    }

    return typeParametersOf(types);
  }

  public static MetaParameterizedType typeParametersOf(final Class<?>... classes) {
    return typeParametersOf(fromClassArray(classes));
  }

  public static MetaParameterizedType typeParametersOf(final MetaType... classes) {
    return new BuildMetaParameterizedType(classes, null, null);
  }

  private static void addLookups(final TypeLiteral literal, final MetaClass metaClass) {
    getMetaClassCache().pushErasedCache(literal.toString(), metaClass);
  }

  private static void addLookups(final Class cls, final MetaClass metaClass) {
    getMetaClassCache().pushErasedCache(cls.getName(), metaClass);
  }

  public static final Map<String, Class<?>> PRIMITIVE_LOOKUP = Collections.unmodifiableMap(new HashMap<String, Class<?>>() {
    {
      put("void", void.class);
      put("boolean", boolean.class);
      put("int", int.class);
      put("long", long.class);
      put("float", float.class);
      put("double", double.class);
      put("short", short.class);
      put("byte", byte.class);
      put("char", char.class);
    }
  });

  public static Class<?> loadClass(final String fullyQualifiedName) {
    try {
      Class<?> cls = PRIMITIVE_LOOKUP.get(fullyQualifiedName);
      if (cls == null) {
        cls = Class.forName(fullyQualifiedName, false, Thread.currentThread().getContextClassLoader());
      }
      return cls;
    }
    catch (final ClassNotFoundException e) {
      final URL url = MetaClassFactory.class.getClassLoader()
          .getResource(fullyQualifiedName.replace('.', '/') + ".java");

      if (url != null) {
        final File sourceFile = new File(url.getFile()).getAbsoluteFile();
        if (sourceFile.exists()) {

          final File directory = sourceFile.getParentFile();
          final String packageName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.'));
          final String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1);

          final String location = ClassChangeUtil.compileClass(directory.getAbsolutePath(),
              packageName,
              className,
              RebindUtils.getTempDirectory() + "/hotload/");

          try {
            return ClassChangeUtil.loadClassDefinition(location,
                packageName, className);
          }
          catch (final Exception e2) {
            throw new RuntimeException("Could not load class: " + fullyQualifiedName, e2);
          }
        }
      }

      throw new RuntimeException("Could not load class: " + fullyQualifiedName);
    }
  }

  public static boolean canLoadClass(final String fullyQualifiedName) {
    try {
      final Class cls = loadClass(fullyQualifiedName);
      return cls != null;
    }
    catch (final Throwable t) {
      return false;
    }
  }

  public static MetaClass[] fromClassArray(final Class<?>[] classes) {
    final MetaClass[] newClasses = new MetaClass[classes.length];
    for (int i = 0; i < classes.length; i++) {
      newClasses[i] = createOrGet(classes[i].getName(), false);
    }
    return newClasses;
  }

  public static MetaClass[] asMetaClassArray(final MetaParameter[] parms) {
    final MetaClass[] type = new MetaClass[parms.length];
    for (int i = 0; i < parms.length; i++) {
      type[i] = parms[i].getType();
    }
    return type;
  }

  public static Class<?>[] asClassArray(final MetaParameter[] parms) {
    final MetaType[] type = new MetaType[parms.length];
    for (int i = 0; i < parms.length; i++) {
      type[i] = parms[i].getType();
    }
    return asClassArray(type);
  }

  public static Class<?>[] asClassArray(final MetaType[] cls) {
    final Class<?>[] newClasses = new Class<?>[cls.length];
    for (int i = 0; i < cls.length; i++) {
      if (cls[i] instanceof MetaParameterizedType) {
        newClasses[i] = ((MetaClass) ((MetaParameterizedType) cls[i]).getRawType()).asClass();
      }
      else {
        newClasses[i] = ((MetaClass) cls[i]).asClass();
      }
    }
    return newClasses;
  }

  public static Collection<MetaClass> getAllNewOrUpdatedClasses() {
    return getMetaClassCache().getAllNewOrUpdated();
  }

  public static Collection<MetaClass> getNewClasses() {
    return getMetaClassCache().getAllNewClasses();
  }

  public static boolean isChangedOrDeleted(final String fqcn) {
    return getMetaClassCache().getAllDeletedClasses().contains(fqcn) || getMetaClassCache().isNewOrUpdated(fqcn);
  }

  public static Set<String> getAllDeletedClasses() {
    return getMetaClassCache().getAllDeletedClasses();
  }

  public static Collection<MetaClass> getAllCachedClasses() {
    return getMetaClassCache().getAllCached();
  }

  public static boolean isKnownType(final String fqcn) {
    return getMetaClassCache().isKnownType(fqcn);
  }

  public static boolean hasAnyChanges() {
    return !getAllNewOrUpdatedClasses().isEmpty() || !getAllDeletedClasses().isEmpty();
  }
}
