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


package org.jboss.errai.codegen.meta.impl.gwt;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.meta.impl.AbstractMetaClass;
import org.jboss.errai.codegen.util.GWTPrivateMemberAccessor;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.codegen.util.PrivateAccessUtil;
import org.jboss.errai.common.rebind.CacheStore;
import org.jboss.errai.common.rebind.CacheUtil;
import org.jboss.errai.reflections.scanners.AbstractScanner;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class GWTClass extends AbstractMetaClass<JType> {
  protected final Annotation[] annotations;
  protected TypeOracle oracle;
  private String fqcn;

  static {
    GenUtil.addClassAlias(GWTClass.class);
    PrivateAccessUtil.registerPrivateMemberAccessor("jsni", new GWTPrivateMemberAccessor());
  }

  protected GWTClass(final TypeOracle oracle, final JType classType, final boolean erased) {
    super(classType);
    this.oracle = oracle;

    final JClassType classOrInterface = classType.isClassOrInterface();
    if (classOrInterface != null) {
      annotations = classOrInterface.getAnnotations();
    }
    else {
      annotations = new Annotation[0];
    }

    if (classType.isTypeParameter() != null || classType.isWildcard() != null) {
      throw new IllegalArgumentException("Cannot represent \"" + classType
              + "\" as a class. Try a different meta type such as GWTWildcardType or GWTTypeVariable.");
    }

    final JParameterizedType parameterizedType = classType.isParameterized();
    if (!erased) {
      if (parameterizedType != null) {
        super.parameterizedType = new GWTParameterizedType(oracle, parameterizedType);
      }
    }
  }


  public static class GWTClassCache implements CacheStore {
    private final Map<String, MetaClass> reloadableClasses = new ConcurrentHashMap<>();

    // Classes in .jar files can't change between refreshes so we can hold on to them
    private final Map<String, MetaClass> classesInJar = new ConcurrentHashMap<>();

    @Override
    public void clear() {
      reloadableClasses.clear();
    }

    public void put(final String name, final MetaClass clazz) {
      if (AbstractScanner.isInJar(name) && !name.contains("<")) {
        classesInJar.put(name, clazz);
      }
      else {
        reloadableClasses.put(name, clazz);
      }
    }

    public MetaClass get(final String name) {
      final MetaClass clazz = classesInJar.get(name);
      if (clazz != null) {
        if (AbstractScanner.isInJar(name)) {
          return clazz;
        }
        else {
          classesInJar.remove(name);
          return null;
        }
      }
      else {
        return reloadableClasses.get(name);
      }
    }
  }

  final static GWTClassCache cache = CacheUtil.getCache(GWTClassCache.class);

  public static MetaClass newInstance(final TypeOracle oracle, final JType type) {
    MetaClass clazz = cache.get(type.getParameterizedQualifiedSourceName());
    if (clazz == null) {
      clazz = newUncachedInstance(oracle, type);
      cache.put(type.getParameterizedQualifiedSourceName(), clazz);
    }

    return clazz;
  }

  public static MetaClass newInstance(final TypeOracle oracle, final String type) {
    try {
      return newUncachedInstance(oracle, oracle.getType(type));
    }
    catch (final NotFoundException e) {
      return null;
    }
  }

  public static MetaClass newUncachedInstance(final TypeOracle oracle, final JType type) {
    return new GWTClass(oracle, type, false);
  }

  public static MetaClass newUncachedInstance(final TypeOracle oracle, final JType type, final boolean erased) {
    return new GWTClass(oracle, type, erased);
  }

  public static MetaClass[] fromClassArray(final TypeOracle oracle, final JClassType[] classes) {
    return Arrays.stream(classes).map(c -> newInstance(oracle, c)).toArray(s -> new MetaClass[s]);
  }

  public static Class<?>[] jParmToClass(final JParameter[] parms) throws ClassNotFoundException {
    return Arrays.stream(parms).map(p -> getPrimitiveOrClass(p)).toArray(s -> new Class<?>[s]);
  }

  public static Class<?> getPrimitiveOrClass(final JParameter parm) {
    final JType type = parm.getType();
    final String name =
        type.isArray() != null ? type.getJNISignature().replace("/", ".") : type.getQualifiedSourceName();

    if (parm.getType().isPrimitive() != null) {
      final char sig = parm.getType().isPrimitive().getJNISignature().charAt(0);

      switch (sig) {
      case 'Z':
        return boolean.class;
      case 'B':
        return byte.class;
      case 'C':
        return char.class;
      case 'D':
        return double.class;
      case 'F':
        return float.class;
      case 'I':
        return int.class;
      case 'J':
        return long.class;
      case 'S':
        return short.class;
      case 'V':
        return void.class;
      default:
        return null;
      }
    }
    else {
      try {
        return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
      }
      catch (final ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public String getName() {
    return getEnclosedMetaObject().getSimpleSourceName();
  }

  @Override
  public String getFullyQualifiedName() {
    if (fqcn != null)  {
      return fqcn;
    }

    if (isArray()) {
      if (getOuterComponentType().isPrimitive()) {
        fqcn = getInternalName();
      }
      else {
        fqcn = getInternalName().replace('/', '.');
      }
    }
    else {
      fqcn = getEnclosedMetaObject().getQualifiedBinaryName();
    }
    return fqcn;
  }

  @Override
  public String getCanonicalName() {
    return getEnclosedMetaObject().getQualifiedSourceName();
  }

  @Override
  public String getInternalName() {
    return getEnclosedMetaObject().getJNISignature();
  }

  private String _packageName = null;
  @Override
  public String getPackageName() {
    if (_packageName != null) {
      return _packageName;
    }

    _packageName = getEnclosedMetaObject().isClassOrInterface().getPackage().getName();
    return _packageName;
  }

  private static MetaMethod[] fromMethodArray(final TypeOracle oracle, final JMethod[] methods) {
    return Arrays.stream(methods).map(m -> new GWTMethod(oracle, m)).toArray(s -> new MetaMethod[s]);
  }

  private List<MetaMethod> getSpecialTypeMethods() {
    final List<MetaMethod> meths = new ArrayList<>();
    final JEnumType type = getEnclosedMetaObject().isEnum();

    if (type != null) {
      meths.add(new GWTSpecialMethod(this, DefModifiers.none(), Scope.Public, String.class, "name"));
      meths.add(new GWTSpecialMethod(this, DefModifiers.none(), Scope.Public, Enum.class, "valueOf", Parameter.of(
          String.class, "p").getMetaParameter()));
      meths.add(new GWTSpecialMethod(this, DefModifiers.none(), Scope.Public, Enum[].class, "values"));
    }

    return meths;
  }

  // TODO report this to be fixed in GWT: getClass() in java.lang.Object is reported as non-final method.
  private static final List<MetaMethod> overrideMethods =
      Arrays.asList(MetaClassFactory.get(Object.class).getMethods());

  private MetaMethod[] _methodsCache = null;

  @Override
  public MetaMethod[] getMethods() {
    if (_methodsCache != null) {
      return _methodsCache;
    }

    final Set<MetaMethod> meths = new LinkedHashSet<>();
    meths.addAll(getSpecialTypeMethods());

    JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      return null;
    }

    final Set<String> processedMethods = new HashSet<>();
    do {
      for (final JMethod jMethod : type.getMethods()) {
        final GWTMethod gwtMethod = new GWTMethod(oracle, jMethod);
        final String readableMethodDecl = GenUtil.getMethodString(gwtMethod);
        if (!jMethod.isPrivate() && !processedMethods.contains(readableMethodDecl)) {
            meths.add(gwtMethod);
            processedMethods.add(readableMethodDecl);
        }
      }

      for (final JClassType interfaceType : type.getImplementedInterfaces()) {
        for (final MetaMethod ifaceMethod : Arrays.asList(GWTClass.newInstance(oracle, interfaceType).getMethods())) {
          final String readableMethodDecl = GenUtil.getMethodString(ifaceMethod);
          if (!processedMethods.contains(readableMethodDecl)) {
            meths.add(ifaceMethod);
            processedMethods.add(readableMethodDecl);
          }
        }
      }
    }
    while ((type = type.getSuperclass()) != null && !type.getQualifiedSourceName().equals("java.lang.Object"));
    meths.addAll(overrideMethods);
    _methodsCache = meths.toArray(new MetaMethod[meths.size()]);

    return _methodsCache;
  }

  @Override
  public MetaMethod[] getDeclaredMethods() {
    final JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      return null;
    }

    return fromMethodArray(oracle, type.getMethods());
  }

  @Override
  public MetaClass getErased() {
    if (getParameterizedType() == null) {
      return this;
    }
    else {
      return new GWTClass(oracle, getEnclosedMetaObject().getErasedType(), true);
    }
  }

  @Override
  public MetaField[] getFields() {
    final List<MetaField> fields = Lists.newArrayList();

    JClassType type = getEnclosedMetaObject().isClass();
    while (type != null) {
      for (final JField field : type.getFields()) {
        // In GWT 2.7 java.lang.Object contains two public fields castableTypeMap and typeMarker that we don't want.
        if (field.isPublic() && !field.getEnclosingType().getQualifiedSourceName().equals("java.lang.Object")) {
          fields.add(new GWTField(oracle, field));
        }
      }
      type = type.getSuperclass();
    }

    return fields.toArray(new MetaField[fields.size()]);
  }

  @Override
  public MetaField[] getDeclaredFields() {
    final JClassType type = getEnclosedMetaObject().isClass();
    if (type != null) {
      return Arrays.stream(type.getFields()).map(f -> new GWTField(oracle, f)).toArray(s -> new MetaField[s]);
    }

    return new MetaField[0];
  }

  @Override
  public MetaField getField(final String name) {
    JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      if ("length".equals(name) && getEnclosedMetaObject().isArray() != null) {
        return new MetaField.ArrayLengthMetaField(this);
      }
      return null;
    }

    JField field = type.findField(name);
    while ((field == null || (field != null && !field.isPublic())) &&
        (type = type.getSuperclass()) != null && !type.getQualifiedSourceName().equals("java.lang.Object")) {
      field = type.findField(name);

      for (final JClassType interfaceType : type.getImplementedInterfaces()) {
        field = interfaceType.findField(name);
      }
    }

    if (field == null) {
      throw new RuntimeException("no such field: " + name + " in class: " + this);
    }

    return new GWTField(oracle, field);
  }

  @Override
  public MetaField getDeclaredField(final String name) {
    final JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      if ("length".equals(name) && getEnclosedMetaObject().isArray() != null) {
        return new MetaField.ArrayLengthMetaField(this);
      }
      return null;
    }

    final JField field = type.findField(name);

    if (field == null) {
      return null;
    }

    return new GWTField(oracle, field);
  }

  private static MetaConstructor[] fromMethodArray(final TypeOracle oracle, final Stream<JConstructor> constructors) {
    return constructors.map(c -> new GWTConstructor(oracle, c)).toArray(s -> new MetaConstructor[s]);
  }

  @Override
  public MetaConstructor[] getConstructors() {
    final JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      return null;
    }

    return fromMethodArray(oracle,
                           Arrays
                             .stream(type.getConstructors())
                             .filter(ctor -> ctor.isPublic()));
  }

  @Override
  public MetaConstructor[] getDeclaredConstructors() {
    final JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      return null;
    }

    return fromMethodArray(oracle, Arrays.stream(type.getConstructors()));
  }

  @Override
  public MetaClass[] getDeclaredClasses() {
    final JClassType[] nestedTypes = getEnclosedMetaObject().isClassOrInterface().getNestedTypes();
    final MetaClass[] declaredClasses = new MetaClass[nestedTypes.length];
    int i = 0;
    for (final JClassType type : nestedTypes) {
      declaredClasses[i++] = GWTClass.newInstance(oracle, type);
    }
    return declaredClasses;
  }

  private MetaClass[] _intefacesCache = null;

  @Override
  public MetaClass[] getInterfaces() {
    if (_intefacesCache != null) {
      return _intefacesCache;
    }

    final JClassType jClassType = getEnclosedMetaObject().isClassOrInterface();
    if (jClassType == null)
      return new MetaClass[0];

    return _intefacesCache = Arrays.stream(jClassType.getImplementedInterfaces())
            .map(i -> new GWTClass(oracle, i, false)).toArray(s -> new MetaClass[s]);
  }

  @Override
  public boolean isArray() {
    return getEnclosedMetaObject().isArray() != null;
  }

  @Override
  public MetaClass getSuperClass() {
    JClassType type = getEnclosedMetaObject().isClassOrInterface();
    if (type == null) {
      return null;
    }

    type = type.getSuperclass();

    if (type == null) {
      return null;
    }

    return newInstance(oracle, type);
  }

  @Override
  public MetaClass getComponentType() {
    final JArrayType type = getEnclosedMetaObject().isArray();
    if (type == null) {
      return null;
    }
    return newUncachedInstance(oracle, type.getComponentType());
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations;
  }

  @Override
  public MetaTypeVariable[] getTypeParameters() {
    final JGenericType genericType;

    if (getEnclosedMetaObject().isGenericType() != null) {
      genericType = getEnclosedMetaObject().isGenericType();
    }
    else if (getEnclosedMetaObject().isParameterized() != null) {
      genericType = getEnclosedMetaObject().isParameterized().getBaseType();
    }
    else if (getEnclosedMetaObject().isRawType() != null) {
      genericType = getEnclosedMetaObject().isRawType().getGenericType();
    }
    else {
      return new MetaTypeVariable[0];
    }

    return Arrays.stream(genericType.getTypeParameters())
            .map(p -> new GWTTypeVariable(oracle, p)).toArray(s -> new MetaTypeVariable[s]);
  }

  @Override
  public boolean isVoid() {
    return getEnclosedMetaObject().getSimpleSourceName().equals("void");
  }

  @Override
  public boolean isPrimitive() {
    return getEnclosedMetaObject().isPrimitive() != null;
  }

  @Override
  public boolean isInterface() {
    return getEnclosedMetaObject().isInterface() != null;
  }

  @Override
  public boolean isAbstract() {
    return getEnclosedMetaObject().isClass() != null && getEnclosedMetaObject().isClass().isAbstract();
  }

  @Override
  public boolean isEnum() {
    return getEnclosedMetaObject().isEnum() != null;
  }

  @Override
  public boolean isAnnotation() {
    return getEnclosedMetaObject().isAnnotation() != null;
  }

  @Override
  public boolean isPublic() {
    return getEnclosedMetaObject().isClassOrInterface() != null &&
            getEnclosedMetaObject().isClassOrInterface().isPublic();
  }

  @Override
  public boolean isPrivate() {
    return getEnclosedMetaObject().isClassOrInterface() != null &&
            getEnclosedMetaObject().isClassOrInterface().isPrivate();
  }

  @Override
  public boolean isProtected() {
    return getEnclosedMetaObject().isClassOrInterface() != null &&
            getEnclosedMetaObject().isClassOrInterface().isProtected();
  }

  @Override
  public boolean isFinal() {
    return getEnclosedMetaObject().isClassOrInterface() != null &&
            getEnclosedMetaObject().isClassOrInterface().isFinal();
  }

  @Override
  public boolean isStatic() {
    return getEnclosedMetaObject().isClassOrInterface() != null &&
            getEnclosedMetaObject().isClassOrInterface().isStatic();
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public boolean isAnonymousClass() {
    return false;
  }

  @Override
  public MetaClass asArrayOf(final int dimensions) {
    JType type = getEnclosedMetaObject();
    for (int i = 0; i < dimensions; i++) {
      type = oracle.getArrayType(type);
    }

    return new GWTClass(oracle, type, false);
  }
}
