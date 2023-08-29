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

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.StringStatement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.StatementEnd;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class Implementations {
  public static ClassStructureBuilder<?> implement(final Class<?> clazz) {
    return ClassBuilder.define(clazz.getPackage().getName() + "." + clazz.getSimpleName() + "Impl")
            .publicScope()
            .implementsInterface(clazz)
            .body();
  }

  public static ClassStructureBuilder<?> implement(final Class<?> clazz,
                                                   final String implClassName) {
    return ClassBuilder.define(clazz.getPackage().getName() + "." + implClassName)
            .publicScope()
            .implementsInterface(clazz)
            .body();
  }

  public static ClassStructureBuilder<?>  implement(final Class<?> clazz,
                                                   final String implPackageName,
                                                   final String implClassName) {
    return ClassBuilder.define(implPackageName + "." + implClassName)
            .publicScope()
            .implementsInterface(clazz)
            .body();
  }

  /**
   * Creates a builder for a new class that extends the given superclass. The
   * new class being built will be in the same package as the given superclass.
   *
   * @param superclass
   *         The class that the class being built extends.
   * @param implClassName
   *         The simple name (no package prefix) of the new class to be built.
   *
   * @return an instance of the {@link ClassStructureBuilder} for building the
   *         extended class
   */
  public static ClassStructureBuilder<?> extend(final Class<?> superclass, final String implClassName) {
    return ClassBuilder.define(superclass.getPackage().getName() + "." + implClassName, superclass)
            .publicScope()
            .body();
  }

  public static void autoInitializedField(final ClassStructureBuilder<?> builder,
                                          final MetaClass type,
                                          final String name,
                                          final Class<?> implementation) {

    autoInitializedField(builder, type, name, MetaClassFactory.get(implementation));
  }

  public static void autoInitializedField(final ClassStructureBuilder<?> builder,
                                          final MetaClass type,
                                          final String name,
                                          MetaClass implementation) {

    implementation = MetaClassFactory.parameterizedAs(implementation, type.getParameterizedType());

    builder.privateField(name, type)
            .initializesWith(Stmt.newObject(implementation)).finish();
  }

  public static StringBuilderBuilder newStringBuilder() {
    return newStringBuilder(64);
  }

  public static StringBuilderBuilder newStringBuilder(final int length) {
    final ContextualStatementBuilder statementBuilder
            = Stmt.nestedCall(Stmt.newObject(StringBuilder.class).withParameters(length));

    return new StringBuilderBuilder() {

      @Override
      public StringBuilderBuilder append(final Object statement) {
        statementBuilder.invoke("append", statement);
        return this;
      }

      String generatedCache;

      @Override
      public String generate(final Context context) {
        if (generatedCache != null) return generatedCache;
        return generatedCache = statementBuilder.generate(context);
      }

      @Override
      public MetaClass getType() {
        return statementBuilder.getType();
      }
    };
  }



  public static interface StringBuilderBuilder extends Statement {
    public StringBuilderBuilder append(Object statement);
  }

  public static BlockBuilder<StatementEnd> autoForLoop(final String varName, final Statement value) {
    return Stmt.for_(Stmt.declareVariable(int.class).named("i").initializeWith(0),
            Bool.lessThan(Variable.get("i"), value),
            new StringStatement(varName + "++", null));
  }

}
