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


package org.jboss.errai.codegen.meta.impl.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.meta.impl.AbstractMetaClass;
import org.jboss.errai.codegen.util.GenUtil;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;

public class JavaReflectionClass extends AbstractMetaClass<Class> {
  private Annotation[] _annotationsCache;

  protected JavaReflectionClass(final Class clazz, final boolean erased) {
    this(clazz, null, erased);
  }

  private JavaReflectionClass(final Class clazz, final Type type, final boolean erased) {
    super(clazz);
    _asClassCache = clazz;
    if (!erased) {
      if (type instanceof ParameterizedType) {
        super.parameterizedType = new JavaReflectionParameterizedType((ParameterizedType) type);
      }
      if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
        super.genericSuperClass = new JavaReflectionParameterizedType((ParameterizedType) clazz.getGenericSuperclass());
      }
    }
  }

  private JavaReflectionClass(final TypeLiteral typeLiteral) {
    super(typeLiteral.getRawType());
    if (typeLiteral.getType() instanceof ParameterizedType) {
      super.parameterizedType = new JavaReflectionParameterizedType((ParameterizedType) typeLiteral.getType());
    }
  }

  public static MetaClass newInstance(final Class type) {
    if (type == null) return null;

    if (!MetaClassFactory.isCached(type.getName())) {
      final MetaClass clazz = newUncachedInstance(type);
      MetaClassFactory.getMetaClassCache().pushCache(clazz);

      return clazz;
    }
    else {
      return MetaClassFactory.get(type);
    }
  }

  public static MetaClass newUncachedInstance(final Class type) {
    return new JavaReflectionClass(type, false);
  }

  public static MetaClass newUncachedInstance(final Class clazz, final boolean erased) {
    return new JavaReflectionClass(clazz, erased);
  }

  public static MetaClass newUncachedInstance(final Class clazz, final Type type) {
    return new JavaReflectionClass(clazz, type, false);
  }

  public static MetaClass newInstance(final TypeLiteral type) {
    return MetaClassFactory.get(type);
  }

  public static MetaClass newUncachedInstance(final TypeLiteral type) {
    return new JavaReflectionClass(type);
  }

  @Override
  public MetaClass getErased() {
    if (parameterizedType == null) {
      return this;
    }
    return new JavaReflectionClass(getEnclosedMetaObject(), true);
  }

  @Override
  public String getName() {
    return getEnclosedMetaObject().getSimpleName();
  }

  @Override
  public String getFullyQualifiedName() {
    return getEnclosedMetaObject().getName();
  }

  @Override
  public String getCanonicalName() {
    return getEnclosedMetaObject().getCanonicalName();
  }

  private String _packageName = null;
  @Override
  public String getPackageName() {
    if (_packageName != null) {
      return _packageName;
    }

    final Package pack = getEnclosedMetaObject().getPackage();
    if (pack != null) {
      _packageName = pack.getName();
    }
    return _packageName;
  }

  private MetaMethod[] fromMethodArray(final Method[] methods) {
    final List<MetaMethod> methodList = new ArrayList<>();

    for (final Method m : methods) {
      // hack to exclude jacoco instrumented methods.
      if (!m.isBridge() && !m.getName().startsWith("$jacoco")) {
        methodList.add(new JavaReflectionMethod(this, m));
      }
    }

    return methodList.toArray(new MetaMethod[methodList.size()]);
  }

  private MetaMethod[] _methodCache = null;

  @Override
  public MetaMethod[] getMethods() {
    if (_methodCache != null) {
      return _methodCache;
    }

    final Set<MetaMethod> meths = new LinkedHashSet<>();

    Class<?> type = getEnclosedMetaObject();
    if (type == null) {
      return null;
    }

    final Set<String> processedMethods = new HashSet<>();
    do {
      for (final Method method : type.getDeclaredMethods()) {
        final JavaReflectionMethod metaMethod = new JavaReflectionMethod(this, method);
        final String readableMethodDecl = GenUtil.getMethodString(metaMethod);
        if (!metaMethod.isPrivate() && !method.isBridge() && !processedMethods.contains(readableMethodDecl)) {
            meths.add(metaMethod);
            processedMethods.add(readableMethodDecl);
        }
      }

      // we don't need to recurse on interfaces in this case because we already get the list of
      // all inherited methods from Class.getMethods() -- interface methods are public
      for (final Class<?> interfaceType : type.getInterfaces()) {
        for (final MetaMethod ifaceMethod : Arrays.asList(JavaReflectionClass.newInstance(interfaceType).getMethods())) {
          final String readableMethodDecl = GenUtil.getMethodString(ifaceMethod);
          if (!processedMethods.contains(readableMethodDecl)) {
            meths.add(ifaceMethod);
            processedMethods.add(readableMethodDecl);
          }
        }
      }
    }
    while ((type = type.getSuperclass()) != null);

    _methodCache = meths.toArray(new MetaMethod[meths.size()]);
    return _methodCache;
  }

  private transient volatile MetaMethod[] _declaredMethodCache;

  @Override
  public MetaMethod[] getDeclaredMethods() {
    return _declaredMethodCache != null ? _declaredMethodCache
            : (_declaredMethodCache = fromMethodArray(getEnclosedMetaObject().getDeclaredMethods()));
  }


  private static MetaField[] fromFieldArray(final Field[] methods) {
    return Arrays.stream(methods).map(f -> new JavaReflectionField(f)).toArray(s -> new MetaField[s]);
  }

  private transient volatile MetaField[] _fieldCache;

  @Override
  public MetaField[] getFields() {
    return _fieldCache != null ? _fieldCache : (_fieldCache = fromFieldArray(getEnclosedMetaObject().getFields()));
  }

  private transient volatile MetaField[] _declaredFieldCache;

  @Override
  public MetaField[] getDeclaredFields() {
    return _declaredFieldCache != null ? _declaredFieldCache
            : (_declaredFieldCache = fromFieldArray(getEnclosedMetaObject().getDeclaredFields()));
  }

  @Override
  public MetaField getField(final String name) {
    try {
      final MetaField mFld;
      if ("length".equals(name) && getEnclosedMetaObject().isArray()) {
        mFld = new MetaField.ArrayLengthMetaField(this);
      }
      else {
        mFld = new JavaReflectionField(getEnclosedMetaObject().getField(name));
      }
      return mFld;
    }
    catch (final Exception e) {
      return null;
    }
  }

  @Override
  public MetaField getDeclaredField(final String name) {
    try {
      final MetaField mFld;

      if ("length".equals(name) && getEnclosedMetaObject().isArray()) {
        mFld = new MetaField.ArrayLengthMetaField(this);
      }
      else {
        mFld = new JavaReflectionField(getEnclosedMetaObject().getDeclaredField(name));
      }
      return mFld;
    }
    catch (final Exception e) {
      return null;
    }
  }


  private transient volatile MetaConstructor[] constructorCache;

  @Override
  public MetaConstructor[] getConstructors() {
    if (constructorCache != null) {
      return constructorCache;
    }

    return constructorCache = Arrays.stream(getEnclosedMetaObject().getConstructors())
            .map(c -> new JavaReflectionConstructor(c)).toArray(s -> new MetaConstructor[s]);
  }

  private transient volatile MetaConstructor[] declConstructorCache;

  @Override
  public MetaConstructor[] getDeclaredConstructors() {
    if (getEnclosedMetaObject().isEnum()) {
      // Enum constructors have strange metadata, so we avoid trouble by saying enums have no constructors
      // (getParameterTypes().length != getGenericParameterTypes().length)
      return new MetaConstructor[0];
    }

    if (declConstructorCache != null) {
      return declConstructorCache;
    }
    return declConstructorCache = Arrays.stream(getEnclosedMetaObject().getDeclaredConstructors())
            .map(c -> new JavaReflectionConstructor(c)).toArray(s -> new MetaConstructor[s]);
  }

  @Override
  public MetaConstructor getConstructor(final Class... parameters) {
    try {
      return new JavaReflectionConstructor(getEnclosedMetaObject().getConstructor(parameters));
    }
    catch (final Exception e) {
      return null;
    }
  }

  @Override
  public MetaConstructor getDeclaredConstructor(final Class... parameters) {
    try {
      return new JavaReflectionConstructor(getEnclosedMetaObject().getDeclaredConstructor(parameters));
    }
    catch (final Exception e) {
      return null;
    }
  }

  @Override
  public MetaClass[] getDeclaredClasses() {
    final Class[] declaredClasses = getEnclosedMetaObject().getDeclaredClasses();
    final MetaClass[] declaredClassesMC = new MetaClass[declaredClasses.length];
    int i = 0;
    for (final Class c : declaredClasses) {
      declaredClassesMC[i++] = MetaClassFactory.get(c);
    }
    return declaredClassesMC;
  }

  private MetaClass[] _interfacesCache;

  @Override
  public MetaClass[] getInterfaces() {
    if (_interfacesCache != null) {
      return _interfacesCache;
    }

    final List<MetaClass> metaClassList = new ArrayList<>();
    final Type[] genIface = getEnclosedMetaObject().getGenericInterfaces();

    int i = 0;
    for (final Class<?> type : getEnclosedMetaObject().getInterfaces()) {
      if (genIface != null) {
        metaClassList.add(new JavaReflectionClass(type, genIface[i], false));
      }
      else {
        metaClassList.add(new JavaReflectionClass(type, false));
      }
      i++;
    }

    return _interfacesCache = metaClassList.toArray(new MetaClass[metaClassList.size()]);
  }

  private MetaClass _superClass;

  @Override
  public MetaClass getSuperClass() {
    if (_superClass != null)
      return _superClass;

    if (getGenericSuperClass() != null) {
      _superClass = parameterizedAs(getEnclosedMetaObject().getSuperclass(), typeParametersOf(getGenericSuperClass()
              .getTypeParameters()));
    }
    else {
      _superClass = newInstance(getEnclosedMetaObject().getSuperclass());
    }
    return _superClass;
  }

  @Override
  public MetaClass getComponentType() {
    return MetaClassFactory.get(getEnclosedMetaObject().getComponentType());
  }

  @Override
  public synchronized Annotation[] getAnnotations() {
    if (_annotationsCache == null) {
      _annotationsCache = getEnclosedMetaObject().getAnnotations();
    }
    return _annotationsCache;
  }

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    return JavaReflectionUtil.fromTypeVariable(getEnclosedMetaObject().getTypeParameters());
  }


  @Override
  public boolean isPrimitive() {
    return getEnclosedMetaObject().isPrimitive();
  }

  @Override
  public boolean isVoid() {
    return getEnclosedMetaObject().equals(Void.TYPE);
  }

  @Override
  public boolean isInterface() {
    return getEnclosedMetaObject().isInterface();
  }

  @Override
  public boolean isAbstract() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.ABSTRACT) != 0;
  }

  @Override
  public boolean isArray() {
    return getEnclosedMetaObject().isArray();
  }

  @Override
  public boolean isEnum() {
    return getEnclosedMetaObject().isEnum();
  }

  @Override
  public boolean isAnnotation() {
    return getEnclosedMetaObject().isAnnotation();
  }

  @Override
  public boolean isPublic() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.PUBLIC) != 0;
  }

  @Override
  public boolean isPrivate() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.PRIVATE) != 0;
  }

  @Override
  public boolean isProtected() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.PROTECTED) != 0;
  }

  @Override
  public boolean isFinal() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.FINAL) != 0;
  }

  @Override
  public boolean isStatic() {
    return (getEnclosedMetaObject().getModifiers() & Modifier.STATIC) != 0;
  }

  @Override
  public boolean isSynthetic() {
    return getEnclosedMetaObject().isSynthetic();
  }

  @Override
  public boolean isAnonymousClass() {
    return getEnclosedMetaObject().isAnonymousClass();
  }

  private final Map<Integer, MetaClass> _arrayTypeCache = new HashMap<>();

  @Override
  public MetaClass asArrayOf(final int dimensions) {
    MetaClass arrayType = _arrayTypeCache.get(dimensions);
    if (arrayType == null) {
      _arrayTypeCache.put(dimensions, arrayType = MetaClassFactory.getArrayOf(getEnclosedMetaObject(), dimensions));
    }
    return arrayType;
  }
}
