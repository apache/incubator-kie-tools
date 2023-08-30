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


package org.jboss.errai.codegen.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.VariableReference;
import org.jboss.errai.codegen.builder.impl.Scope;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.exception.InvalidTypeException;
import org.jboss.errai.codegen.exception.OutOfScopeException;
import org.jboss.errai.codegen.exception.TypeNotIterableException;
import org.jboss.errai.codegen.exception.UndefinedMethodException;
import org.jboss.errai.codegen.literal.LiteralFactory;
import org.jboss.errai.codegen.literal.LiteralValue;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.mvel2.DataConversion;
import org.mvel2.util.NullType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class GenUtil {
  private static final String PERMISSIVE_PROPERTY = "errai.codegen.permissive";
  private static boolean PERMISSIVE_MODE;

  static {
    PERMISSIVE_MODE = System.getProperty(PERMISSIVE_PROPERTY) != null
        && Boolean.getBoolean(PERMISSIVE_PROPERTY);
  }

  public static boolean isPermissiveMode() {
    return PERMISSIVE_MODE;
  }

  public static void setPermissiveMode(final boolean permissiveMode) {
    PERMISSIVE_MODE = permissiveMode;
  }

  public static Statement[] generateCallParameters(final Context context, final Object... parameters) {
    return Arrays.stream(parameters).map(p -> generate(context, p)).toArray(s -> new Statement[s]);
  }

  public static Statement[] generateCallParameters(final MetaMethod method,
                                                   final Context context,
                                                   final Object... parameters) {
    if (parameters.length != method.getParameters().length && !method.isVarArgs()) {
      throw new UndefinedMethodException("Wrong number of parameters");
    }

    final MetaParameter[] methParms = method.getParameters();

    final Statement[] statements = new Statement[parameters.length];
    int i = 0;
    for (Object parameter : parameters) {
      if (parameter instanceof Statement) {
        if (((Statement) parameter).getType() == null) {
          parameter = generate(context, parameter);
        }
      }
      try {
        statements[i] = convert(context, parameter, methParms[i++].getType());
      }
      catch (final GenerationException t) {
        t.appendFailureInfo("in method call: "
            + method.getDeclaringClass().getFullyQualifiedName()
            + "." + method.getName() + "(" + Arrays.toString(methParms) + ")");
        throw t;
      }
    }
    return statements;
  }

  public static Statement generate(final Context context, final Object o) {
    if (o instanceof VariableReference) {
      return context.getVariable(((VariableReference) o).getName());
    }
    else if (o instanceof Variable) {
      final Variable v = (Variable) o;
      if (context.isScoped(v)) {
        return v.getReference();
      }
      else {
        if (context.isPermissiveMode()) {
          return v.getReference();
        }
        else {
          throw new OutOfScopeException("variable cannot be referenced from this scope: " + v.getName());
        }
      }
    }
    else if (o instanceof Statement) {
      ((Statement) o).generate(context);
      return (Statement) o;
    }
    else {
      return LiteralFactory.getLiteral(context, o);
    }
  }

  public static void assertIsIterable(final Statement statement) {
    final Class<?> cls = statement.getType().asClass();

    if (!cls.isArray() && !Iterable.class.isAssignableFrom(cls))
      throw new TypeNotIterableException(statement.generate(Context.create()));
  }

  private static final Set<String> classAliases = new HashSet<String>() {
    {
      add(JavaReflectionClass.class.getName());
      add(Class.class.getName());
    }
  };

  public static void addClassAlias(final Class cls) {
    classAliases.add(cls.getName());
  }

  public static void assertAssignableTypes(final Context context, final MetaClass from, final MetaClass to) {
    if (!to.asBoxed().isAssignableFrom(from.asBoxed())) {
      if (to.isArray() && from.isArray()
          && GenUtil.getArrayDimensions(to) == GenUtil.getArrayDimensions(from)
          && to.getOuterComponentType().isAssignableFrom(from.getOuterComponentType())) {
        return;
      }

      if (!context.isPermissiveMode()) {
        if (classAliases.contains(from.getFullyQualifiedName()) && classAliases.contains(to.getFullyQualifiedName())) {
          // handle convertibility between MetaClass API and java Class reference.
          return;
        }

        throw new InvalidTypeException(to.getFullyQualifiedName() + " is not assignable from "
            + from.getFullyQualifiedName());
      }
    }
  }

  public static Statement convert(final Context context, Object input, final MetaClass targetType) {
    try {
      if (input instanceof Statement) {
        if (input instanceof LiteralValue<?>) {
          input = ((LiteralValue<?>) input).getValue();
        }
        else {
          if ("null".equals(((Statement) input).generate(context))) {
            return (Statement) input;
          }

          assertAssignableTypes(context, ((Statement) input).getType(), targetType);
          return (Statement) input;
        }
      }

      if (input != null && MetaClassFactory.get(input.getClass())
          .getOuterComponentType().getFullyQualifiedName().equals(MetaClass.class.getName())) {
        return generate(context, input);
      }

      if (Object.class.getName().equals(targetType.getFullyQualifiedName())) {
        return generate(context, input);
      }

      Class<?> inputClass = input == null ? Object.class : input.getClass();

      if (MetaClass.class.isAssignableFrom(inputClass)) {
        inputClass = Class.class;
      }

      final Class<?> targetClass = targetType.asBoxed().asClass();
      if (NullType.class.getName().equals(targetClass.getName())) {
        return generate(context, input);
      }

      if (!targetClass.isAssignableFrom(inputClass) && DataConversion.canConvert(targetClass, inputClass)) {
        return generate(context, DataConversion.convert(input, targetClass));
      }
      else {
        return generate(context, input);
      }
    }
    catch (final NumberFormatException nfe) {
      throw new InvalidTypeException(nfe);
    }
  }

  public static String classesAsStrings(final MetaClass... stmt) {
    final StringBuilder buf = new StringBuilder(128);
    for (int i = 0; i < stmt.length; i++) {
      buf.append(stmt[i].getFullyQualifiedName());
      if (i + 1 < stmt.length) {
        buf.append(", ");
      }
    }
    return buf.toString();
  }

  public static MetaClass[] fromParameters(final MetaParameter... parms) {
    return Arrays.stream(parms).map(p -> p.getType()).toArray(s -> new MetaClass[s]);
  }

  public static MetaClass[] classToMeta(final Class<?>[] types) {
    return Arrays.stream(types).map(t -> MetaClassFactory.get(t)).toArray(s -> new MetaClass[s]);
  }

  public static Scope scopeOf(final MetaClass clazz) {
    if (clazz.isPublic()) {
      return Scope.Public;
    }
    else if (clazz.isPrivate()) {
      return Scope.Private;
    }
    else if (clazz.isProtected()) {
      return Scope.Protected;
    }
    else {
      return Scope.Package;
    }
  }

  public static Scope scopeOf(final MetaClassMember member) {
    if (member.isPublic()) {
      return Scope.Public;
    }
    else if (member.isPrivate()) {
      return Scope.Private;
    }
    else if (member.isProtected()) {
      return Scope.Protected;
    }
    else {
      return Scope.Package;
    }
  }

  public static DefModifiers modifiersOf(final MetaClassMember member) {
    final DefModifiers defModifiers = new DefModifiers();
    if (member.isAbstract()) {
      defModifiers.addModifiers(Modifier.Abstract);
    }
    else if (member.isFinal()) {
      defModifiers.addModifiers(Modifier.Final);
    }
    else if (member.isStatic()) {
      defModifiers.addModifiers(Modifier.Static);
    }
    else if (member.isSynchronized()) {
      defModifiers.addModifiers(Modifier.Synchronized);
    }
    else if (member.isVolatile()) {
      defModifiers.addModifiers(Modifier.Volatile);
    }
    else if (member.isTransient()) {
      defModifiers.addModifiers(Modifier.Transient);
    }
    return defModifiers;
  }

  public static boolean equals(final MetaField a, final MetaField b) {
    return a.getName().equals(b.getName()) && !a.getType().equals(b.getType())
        && !a.getDeclaringClass().equals(b.getDeclaringClass());
  }

  public static boolean equals(final MetaConstructor a, final MetaConstructor b) {
    if (a.getParameters().length != b.getParameters().length) {
      return false;
    }

    for (int i = 0; i < a.getParameters().length; i++) {
      if (!equals(a.getParameters()[i], b.getParameters()[i])) {
        return false;
      }
    }

    return a.getDeclaringClass().equals(b.getDeclaringClass());
  }

  public static boolean equals(final MetaMethod a, final MetaMethod b) {
    if (!a.getName().equals(b.getName())) return false;
    if (a.getParameters().length != b.getParameters().length) return false;
    if (!a.getDeclaringClass().equals(b.getDeclaringClass())) return false;

    for (int i = 0; i < a.getParameters().length; i++) {
      if (!equals(a.getParameters()[i], b.getParameters()[i])) return false;
    }
    return true;
  }

  public static boolean equals(final MetaParameter a, final MetaParameter b) {
    return a.getType().isAssignableFrom(b.getType()) || b.getType().isAssignableFrom(a.getType());
  }

  public static String getMethodString(final MetaMethod method) {
    return method.getName() + "(" + Arrays.toString(method.getParameters()) + ")";
  }

  public static MetaClass getPrimitiveWrapper(final MetaClass clazz) {
    if (clazz.isPrimitive()) {
      if ("int".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Integer.class);
      }
      else if ("boolean".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Boolean.class);
      }
      else if ("long".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Long.class);
      }
      else if ("double".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Double.class);
      }
      else if ("float".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Float.class);
      }
      else if ("short".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Short.class);
      }
      else if ("char".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Character.class);
      }
      else if ("byte".equals(clazz.getCanonicalName())) {
        return MetaClassFactory.get(Byte.class);
      }
    }
    return clazz;
  }

  public static boolean isPrimitiveWrapper(final MetaClass clazz) {
    return Integer.class.getName().equals(clazz.getFullyQualifiedName())
        || Boolean.class.getName().equals(clazz.getFullyQualifiedName())
        || Long.class.getName().equals(clazz.getFullyQualifiedName())
        || Double.class.getName().equals(clazz.getFullyQualifiedName())
        || Float.class.getName().equals(clazz.getFullyQualifiedName())
        || Short.class.getName().equals(clazz.getFullyQualifiedName())
        || Character.class.getName().equals(clazz.getFullyQualifiedName())
        || Byte.class.getName().equals(clazz.getFullyQualifiedName());
  }

  public static MetaClass getUnboxedFromWrapper(final MetaClass clazz) {
    if (Integer.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(int.class);
    }
    else if (Boolean.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(boolean.class);
    }
    else if (Long.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(long.class);
    }
    else if (Double.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(double.class);
    }
    else if (Float.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(float.class);
    }
    else if (Short.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(short.class);
    }
    else if (Character.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(char.class);
    }
    else if (Byte.class.getName().equals(clazz.getFullyQualifiedName())) {
      return MetaClassFactory.get(byte.class);
    }
    return clazz;
  }

  public static int getArrayDimensions(final MetaClass type) {
    if (!type.isArray()) return 0;

    final String internalName = type.getInternalName();
    for (int i = 0; i < internalName.length(); i++) {
      if (internalName.charAt(i) != '[') return i;
    }
    return 0;
  }

  public static MetaMethod findCaseInsensitiveMatch(final MetaClass retType,
                                                    final MetaClass clazz,
                                                    final String name,
                                                    final MetaClass... parms) {
    MetaClass c = clazz;

    do {
      Outer:
      for (final MetaMethod method : c.getDeclaredMethods()) {
        if (name.equalsIgnoreCase(method.getName())) {
          if (parms.length != method.getParameters().length) continue;

          final MetaParameter[] mps = method.getParameters();
          for (int i = 0; i < parms.length; i++) {
            if (!parms[i].getFullyQualifiedName().equals(mps[i].getType().getFullyQualifiedName())) {
              continue Outer;
            }
          }

          if (retType != null
              && !retType.getFullyQualifiedName().equals(method.getReturnType().getFullyQualifiedName())) {
            continue;
          }

          return method;
        }
      }
    }
    while ((c = c.getSuperClass()) != null);

    return null;
  }

  public static void throwIfUnhandled(final String error, final Throwable t) {
    try {
      throw t;
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Throwable e) {
      throw new RuntimeException("generation failure at: " + error, e);
    }
  }

  public static MetaMethod getBestCandidate(final MetaClass[] arguments,
                                            final String method,
                                            final MetaClass decl,
                                            MetaMethod[] methods,
                                            final boolean classTarget) {
    if (methods == null || methods.length == 0) {
      return null;
    }

    MetaParameter[] parmTypes;
    MetaMethod bestCandidate = null;
    int bestScore = 0;
    int score;
    boolean retry = false;

    do {
      for (final MetaMethod meth : methods) {
        if (classTarget && (meth.isStatic())) continue;

        if (method.equals(meth.getName())) {
          final boolean isVarArgs = meth.isVarArgs();
          if ((parmTypes = meth.getParameters()).length != arguments.length && !isVarArgs) {
            continue;
          }
          else if (arguments.length == 0 && parmTypes.length == 0) {
            bestCandidate = meth;
            break;
          }

          score = scoreMethods(arguments, parmTypes, isVarArgs);

          if (score != 0 && score > bestScore) {
            bestCandidate = meth;
            bestScore = score;
          }
        }
      }

      if (!retry && bestCandidate == null && decl.isInterface()) {
        final MetaMethod[] objMethods = Object_MetaClass.getMethods();
        final MetaMethod[] nMethods = new MetaMethod[methods.length + objMethods.length];
        System.arraycopy(methods, 0, nMethods, 0, methods.length);
        System.arraycopy(objMethods, 0, nMethods, methods.length, objMethods.length);
        methods = nMethods;

        retry = true;
      }
      else {
        break;
      }
    }
    while (true);

    return bestCandidate;
  }

  public static int scoreMethods(final MetaClass[] arguments, final MetaParameter[] parmTypes, final boolean isVarArgs) {
    int score = 0;
    for (int i = 0; i != arguments.length; i++) {
      final MetaClass actualParamType;
      if (isVarArgs && !arguments[arguments.length - 1].isArray() && i >= parmTypes.length - 1)
        actualParamType = parmTypes[parmTypes.length - 1].getType().getComponentType();
      else
        actualParamType = parmTypes[i].getType();

      if (arguments[i] == null) {
        if (!actualParamType.isPrimitive()) {
          score += 6;
        }
        else {
          score = 0;
          break;
        }
      }
      else if (actualParamType.equals(arguments[i])) {
        score += 7;
      }
      else if (actualParamType.isPrimitive() && actualParamType.asBoxed().equals(arguments[i])) {
        score += 6;
      }
      else if (arguments[i].isPrimitive() && arguments[i].asUnboxed().equals(actualParamType)) {
        score += 6;
      }
      else if (actualParamType.isAssignableFrom(arguments[i])) {
        score += 5;
      }
      else if (isNumericallyCoercible(arguments[i], actualParamType)) {
        score += 4;
      }
      else if (actualParamType.asBoxed().isAssignableFrom(arguments[i].asBoxed())
          && !Object_MetaClass.equals(arguments[i])) {
        score += 3 + scoreInterface(actualParamType, arguments[i]);
      }
      else if (canConvert(actualParamType, arguments[i])) {
        if (actualParamType.isArray() && arguments[i].isArray()) {
          final MetaClass outerComponentTypeActual = actualParamType.getOuterComponentType();
          final MetaClass outerComponentTypeArg = arguments[i].getOuterComponentType();

          if (canConvert(outerComponentTypeActual, outerComponentTypeArg)) {
            score += 1;
          }
          else {
            continue;
          }
        }
        else if (actualParamType.equals(char_MetaClass) && arguments[i].equals(String_MetaClass)) {
          score += 1;
        }

        score += 1;
      }
      else if (actualParamType.equals(Object_MetaClass) || arguments[i].equals(NullType_MetaClass)) {
        score += 1;
      }
      else {
        score = 0;
        break;
      }
    }
    return score;
  }

  public static MetaConstructor getBestConstructorCandidate(final MetaClass[] arguments,
                                                            final MetaClass decl,
                                                            MetaConstructor[] constructors,
                                                            final boolean classTarget) {
    if (constructors == null || constructors.length == 0) {
      return null;
    }

    MetaParameter[] parmTypes;
    MetaConstructor bestCandidate = null;
    int bestScore = 0;
    int score;
    boolean retry = false;

    do {
      for (final MetaConstructor meth : constructors) {
        if (classTarget && (meth.isStatic())) continue;

        final boolean isVarArgs = meth.isVarArgs();
        if ((parmTypes = meth.getParameters()).length != arguments.length && !isVarArgs) {
          continue;
        }
        else if (arguments.length == 0 && parmTypes.length == 0) {
          bestCandidate = meth;
          break;
        }

        score = scoreMethods(arguments, parmTypes, isVarArgs);

        if (score != 0 && score > bestScore) {
          bestCandidate = meth;
          bestScore = score;
        }
      }

      if (!retry && bestCandidate == null && decl.isInterface()) {
        final MetaConstructor[] objMethods = Object_MetaClass.getConstructors();
        final MetaConstructor[] nMethods = new MetaConstructor[constructors.length + objMethods.length];
        System.arraycopy(constructors, 0, nMethods, 0, constructors.length);
        System.arraycopy(objMethods, 0, nMethods, constructors.length, objMethods.length);
        constructors = nMethods;

        retry = true;
      }
      else {
        break;
      }
    }
    while (true);

    return bestCandidate;
  }

  private static final MetaClass Number_MetaClass = MetaClassFactory.get(Number.class);
  private static final MetaClass Object_MetaClass = MetaClassFactory.get(Object.class);
  private static final MetaClass NullType_MetaClass = MetaClassFactory.get(NullType.class);
  private static final MetaClass char_MetaClass = MetaClassFactory.get(char.class);
  private static final MetaClass String_MetaClass = MetaClassFactory.get(String.class);

  public static boolean canConvert(final MetaClass to, final MetaClass from) {
    try {
      final Class<?> fromClazz = from.asClass();
      final Class<?> toClass = to.asClass();

      return DataConversion.canConvert(toClass, fromClazz);
    }
    catch (final Throwable t) {
      return false;
    }
  }

  public static boolean isNumericallyCoercible(final MetaClass target, final MetaClass parm) {
    MetaClass boxedTarget = target.isPrimitive() ? target.asBoxed() : target;

    if (boxedTarget != null && Number_MetaClass.isAssignableFrom(target)) {
      if ((boxedTarget = parm.isPrimitive() ? parm.asBoxed() : parm) != null) {
        return Number_MetaClass.isAssignableFrom(boxedTarget);
      }
    }
    return false;
  }

  public static int scoreInterface(final MetaClass parm, final MetaClass arg) {
    if (parm.isInterface()) {
      final MetaClass[] iface = arg.getInterfaces();
      if (iface != null) {
        for (final MetaClass c : iface) {
          if (c == parm) return 1;
          else if (parm.isAssignableFrom(c)) return scoreInterface(parm, arg.getSuperClass());
        }
      }
    }
    return 0;
  }

  public static void rewriteBlameStackTrace(final Throwable innerBlame) {
    final StackTraceElement[] stackTrace = innerBlame.getStackTrace();

    final List<StackTraceElement> innerStackTrace = new ArrayList<StackTraceElement>(10);
    final List<StackTraceElement> outerStackTrace = new ArrayList<StackTraceElement>(10);
    for (final StackTraceElement el : stackTrace) {
      if (el.getClassName().startsWith("org.jboss.errai.codegen.")) {
        innerStackTrace.add(el);
      }
      else {
        outerStackTrace.add(el);
      }
    }

    innerBlame.setStackTrace(innerStackTrace.toArray(new StackTraceElement[innerStackTrace.size()]));

    final RuntimeException outerBlame = new RuntimeException("External call to API");
    outerBlame.setStackTrace(outerStackTrace.toArray(new StackTraceElement[outerStackTrace.size()]));
    innerBlame.initCause(outerBlame);
  }
}
