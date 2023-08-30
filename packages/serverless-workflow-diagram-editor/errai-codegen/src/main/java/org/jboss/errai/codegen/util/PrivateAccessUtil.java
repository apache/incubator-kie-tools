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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;

/**
 * Utility class with methods that generate code to access private, default
 * access ("package private"), and protected methods and fields in arbitrary
 * classes. Each generator allows the choice of generating Java Reflection code
 * (for use on the server side) or JSNI code (for use on the client side).
 *
 * @author Mike Brock
 * @author Jonathan Fuerth
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class PrivateAccessUtil {
  private static final Map<String, PrivateMemberAccessor> PRIVATE_MEMBER_ACCESSORS
      = new HashMap<String, PrivateMemberAccessor>();

  static {
    PRIVATE_MEMBER_ACCESSORS.put("reflection", new ReflectionPrivateMemberAccessor());
  }

  public static void registerPrivateMemberAccessor(final String type, final PrivateMemberAccessor accessor) {
    PRIVATE_MEMBER_ACCESSORS.put(type, accessor);
  }

  public static void addPrivateAccessStubs(final String type,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaField f) {
    addPrivateAccessStubs(PrivateAccessType.Both, type, classBuilder, f);
  }

  /**
   * Generates methods for accessing a private field using either JSNI or Java
   * Reflection. The generated methods will be private and static.
   *
   * @param accessType
   *     Whether to generate a read method, a write method, or both.
   * @param type
   *     The type of accessors to use (ie. "reflection" or "jsni").
   * @param classBuilder
   *     The class builder to add the generated methods to.
   * @param f
   *     The field the generated accessors read and write.
   */
  public static void addPrivateAccessStubs(final PrivateAccessType accessType,
                                           final String type,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaField f) {
    addPrivateAccessStubs(accessType, type, classBuilder, f, new Modifier[]{Modifier.Static});
  }

  /**
   * Generates methods for accessing a private field using either JSNI or Java
   * Reflection. The generated methods will be private.
   *
   * @param accessType
   *     Whether to generate a read method, a write method, or both.
   * @param accessorType
   *     The type of accessors to use (ie. "reflection" or "jsni").
   * @param classBuilder
   *     The class builder to add the generated methods to.
   * @param f
   *     The field the generated accessors read and write.
   * @param modifiers
   *     The modifiers on the generated method, for example
   *     {@link Modifier#Final} or {@link Modifier#Synchronized}. <i>Never
   *     specify {@code Modifier.JSNI}</i>; it is added automatically when
   *     needed.
   */
  public static void addPrivateAccessStubs(final PrivateAccessType accessType,
                                           final String accessorType,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaField f,
                                           Modifier[] modifiers) {
    final MetaClass type = f.getType();

    final boolean read = accessType == PrivateAccessType.Read || accessType == PrivateAccessType.Both;
    final boolean write = accessType == PrivateAccessType.Write || accessType == PrivateAccessType.Both;

    final PrivateMemberAccessor privateMemberAccessor
        = PRIVATE_MEMBER_ACCESSORS.get(accessorType);

    if (privateMemberAccessor == null) {
      throw new IllegalArgumentException("unknown accessor type: " + accessorType);
    }

    if (read) {
      privateMemberAccessor.createReadableField(type, classBuilder, f, modifiers);
    }

    if (write) {
      privateMemberAccessor.createWritableField(type, classBuilder, f, modifiers);
    }
  }

  /**
   * Generates methods for accessing a nonpublic constructor using either JSNI or
   * Java Reflection. The generated method will be private and static. The name
   * of the generated method can be discovered by calling
   * {@link #getPrivateMethodName(MetaMethod)}.
   *
   * @param accessorType
   *     The type of accessors to use (ie. "reflection" or "jsni").
   * @param classBuilder
   *     The class builder to add the generated method to.
   * @param m
   *     The constructor the generated method will invoke
   */
  public static void addPrivateAccessStubs(final String accessorType,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaConstructor m) {

    final PrivateMemberAccessor privateMemberAccessor
        = PRIVATE_MEMBER_ACCESSORS.get(accessorType);

    if (privateMemberAccessor == null) {
      throw new IllegalArgumentException("unknown accessor type: " + accessorType);
    }

    privateMemberAccessor.makeConstructorAccessible(classBuilder, m);
  }

  /**
   * Generates methods for accessing a nonpublic method using either JSNI or
   * Java Reflection. The generated method will be private and static. The name
   * of the generated method can be discovered by calling
   * {@link #getPrivateMethodName(MetaMethod)}.
   *
   * @param accessorType
   *     The type of accessors to use (ie. "reflection" or "jsni").
   * @param classBuilder
   *     The class builder to add the generated method to.
   * @param m
   *     The nonpublic method the generated method will invoke
   */
  public static void addPrivateAccessStubs(final String accessorType,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaMethod m) {
    addPrivateAccessStubs(accessorType, classBuilder, m, new Modifier[]{Modifier.Static});
  }

  /**
   * Generates methods for accessing a nonpublic method using either JSNI or Java
   * Reflection. The generated method will be private and static.
   *
   * @param accessorType
   *     The type of accessors to use (ie. "reflection" or "jsni").
   * @param classBuilder
   *     The class builder to add the generated method to.
   * @param m
   *     The method the generated accessors read and write.
   * @param modifiers
   *     The modifiers on the generated method, for example
   *     {@link Modifier#Final} or {@link Modifier#Synchronized}. <i>Never
   *     specify {@code Modifier.JSNI}</i>; it is added automatically when
   *     needed.
   */
  public static void addPrivateAccessStubs(final String accessorType,
                                           final ClassStructureBuilder<?> classBuilder,
                                           final MetaMethod m,
                                           final Modifier[] modifiers) {


    final PrivateMemberAccessor privateMemberAccessor
        = PRIVATE_MEMBER_ACCESSORS.get(accessorType);

    if (privateMemberAccessor == null) {
      throw new IllegalArgumentException("unknown accessor type: " + accessorType);
    }

    privateMemberAccessor.makeMethodAccessible(classBuilder, m, modifiers);
  }

  public static String getPrivateFieldAccessorName(final MetaField field) {
    return field.getDeclaringClass().getName()
        + "_" + getTypeName(field.getType())
        + "_" + field.getName();
  }

  public static String getPrivateMethodName(final MetaMethod method) {
    final MetaClass declaringClass = method.getDeclaringClass();
    String name =  declaringClass.getName() + "_" + method.getName();

    if (method.getParameters() != null) {
      for (final MetaParameter p : method.getParameters()) {
       name += "_" + getTypeName(p.getType());
      }
    }

    return name;
  }

  private static String getTypeName(final MetaClass type) {
    if (type.isArray()) {
      return type.getName().replace("[]", "_array");
    } else {
      return type.getName();
    }
  }

  public static String condensify(String name) {
    return name.replace('.', '_').replace('$', '_');
  }
}
