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


package org.jboss.errai.codegen.builder.impl;

import java.lang.annotation.Annotation;
import java.util.List;

import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.DefModifiers;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.builder.BuildCallback;
import org.jboss.errai.codegen.builder.ClassDefinitionBuilderAbstractOption;
import org.jboss.errai.codegen.builder.ClassDefinitionBuilderCommentOption;
import org.jboss.errai.codegen.builder.ClassDefinitionBuilderInterfaces;
import org.jboss.errai.codegen.builder.ClassDefinitionBuilderScope;
import org.jboss.errai.codegen.builder.ClassDefinitionStaticOption;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilderAbstractMethodOption;
import org.jboss.errai.codegen.builder.ConstructorBlockBuilder;
import org.jboss.errai.codegen.builder.DefaultClassStructureBuilder;
import org.jboss.errai.codegen.builder.FieldBuildStart;
import org.jboss.errai.codegen.builder.MethodBuildCallback;
import org.jboss.errai.codegen.builder.MethodCommentBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaConstructor;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaField;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaMethod;
import org.jboss.errai.codegen.util.GenUtil;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ClassBuilder<T extends ClassStructureBuilder<T>> implements
        ClassDefinitionBuilderCommentOption<T>,
        ClassDefinitionBuilderScope<T>,
        ClassDefinitionStaticOption<T>,
        ClassStructureBuilder<T> {

  protected final BuildMetaClass classDefinition;

  ClassBuilder(final String className, final MetaClass parent, final Context context) {
    this.classDefinition = new BuildMetaClass(context, className);
    this.classDefinition.setSuperClass(parent);
    context.attachClass(classDefinition);
  }

  ClassBuilder(final ClassBuilder<T> that, final Context context) {
    this.classDefinition = that.classDefinition;
    this.classDefinition.setContext(context);
    context.attachClass(classDefinition);
  }

  public static ClassDefinitionBuilderCommentOption<?> define(final String fullyQualifiedName) {
    return new ClassBuilder<DefaultClassStructureBuilder>(fullyQualifiedName, null, Context.create().autoImport());
  }

  public static ClassDefinitionBuilderCommentOption<?> define(final String fullQualifiedName, final MetaClass parent) {
    return new ClassBuilder<DefaultClassStructureBuilder>(fullQualifiedName, parent, Context.create().autoImport());
  }

  public static ClassDefinitionBuilderCommentOption<?> define(final String fullQualifiedName, final Class<?> parent) {
    return define(fullQualifiedName, MetaClassFactory.get(parent));
  }

  public static ClassStructureBuilder<?> implement(final MetaClass cls) {
    return new ClassBuilder<DefaultClassStructureBuilder>(cls.getFullyQualifiedName() + "Impl", null, Context.create()
            .autoImport())
            .publicScope()
            .implementsInterface(cls).body();
  }

  public static ClassStructureBuilder<?> implement(final Class<?> cls) {
    return implement(MetaClassFactory.get(cls));
  }

  @Override
  public ClassBuilderAbstractMethodOption abstractClass() {
    classDefinition.setAbstract(true);
    return new ClassBuilderAbstractMethodOption(this, classDefinition.getContext());
  }

  @Override
  public ClassDefinitionBuilderInterfaces<ClassStructureBuilderAbstractMethodOption> interfaceDefinition() {
    classDefinition.setInterface(true);
    return new ClassBuilderAbstractMethodOption(this, classDefinition.getContext());

  }

  public ClassBuilder<T> importsClass(final Class<?> clazz) {
    return importsClass(MetaClassFactory.get(clazz));
  }

  public ClassBuilder<T> importsClass(final MetaClass clazz) {
    classDefinition.getContext().addImport(clazz);
    return this;
  }

  @Override
  public ClassDefinitionBuilderInterfaces<T> implementsInterface(final Class<?> clazz) {
    return implementsInterface(MetaClassFactory.get(clazz));
  }

  @Override
  public ClassDefinitionBuilderInterfaces<T> implementsInterface(final MetaClass clazz) {
    if (!clazz.isInterface()) {
      throw new RuntimeException("not an interface: " + clazz.getFullyQualifiedName());
    }

    classDefinition.addInterface(clazz);
    return this;
  }

  @Override
  public ClassStructureBuilder<T> body() {
    return this;
  }

  @Override
  public ClassDefinitionBuilderScope<T> classComment(String comment) {
    classDefinition.setClassComment(comment);
    return this;
  }

  @Override
  public ClassDefinitionStaticOption<T> publicScope() {
    classDefinition.setScope(Scope.Public);
    return this;
  }

  @Override
  public ClassDefinitionStaticOption<T> privateScope() {
    classDefinition.setScope(Scope.Private);
    return this;
  }

  @Override
  public ClassDefinitionStaticOption<T> protectedScope() {
    classDefinition.setScope(Scope.Protected);
    return this;
  }

  @Override
  public ClassDefinitionStaticOption<T> packageScope() {
    classDefinition.setScope(Scope.Package);
    return this;
  }

  @Override
  public ClassDefinitionBuilderAbstractOption<T> staticClass() {
    classDefinition.setStatic(true);
    return this;
  }

  @Override
  public ConstructorBlockBuilder<T> publicConstructor() {
    return genConstructor(Scope.Public, DefParameters.none());
  }

  @Override
  public ConstructorBlockBuilder<T> publicConstructor(final MetaClass... parms) {
    return genConstructor(Scope.Public, DefParameters.fromTypeArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> publicConstructor(final Class<?>... parms) {
    return publicConstructor(MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> publicConstructor(final Parameter... parms) {
    return genConstructor(Scope.Public, DefParameters.fromParameters(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> privateConstructor() {
    return genConstructor(Scope.Private, DefParameters.none());
  }

  @Override
  public ConstructorBlockBuilder<T> privateConstructor(final MetaClass... parms) {
    return genConstructor(Scope.Private, DefParameters.fromTypeArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> privateConstructor(final Class<?>... parms) {
    return privateConstructor(MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> privateConstructor(final Parameter... parms) {
    return genConstructor(Scope.Private, DefParameters.fromParameters(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> protectedConstructor() {
    return genConstructor(Scope.Protected, DefParameters.none());
  }

  @Override
  public ConstructorBlockBuilder<T> protectedConstructor(final MetaClass... parms) {
    return genConstructor(Scope.Protected, DefParameters.fromTypeArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> protectedConstructor(final Class<?>... parms) {
    return protectedConstructor(MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> protectedConstructor(final Parameter... parms) {
    return genConstructor(Scope.Protected, DefParameters.fromParameters(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> packageConstructor() {
    return genConstructor(Scope.Package, DefParameters.none());
  }

  @Override
  public ConstructorBlockBuilder<T> packageConstructor(final MetaClass... parms) {
    return genConstructor(Scope.Package, DefParameters.fromTypeArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> packageConstructor(final Class<?>... parms) {
    return packageConstructor(MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public ConstructorBlockBuilder<T> packageConstructor(final Parameter... parms) {
    return genConstructor(Scope.Package, DefParameters.fromParameters(parms));
  }

  private ConstructorBlockBuilder<T> genConstructor(final Scope scope, final DefParameters
          defParameters) {
    return new ConstructorBlockBuilderImpl<T>(new BuildCallback<T>() {

      @Override
      public T callback(final Statement statement) {
        final BuildMetaConstructor buildMetaConstructor =
                new BuildMetaConstructor(classDefinition, statement, defParameters);
        buildMetaConstructor.setScope(scope);

        classDefinition.addConstructor(buildMetaConstructor);
        return (T) ClassBuilder.this;
      }

      @Override
      public Context getParentContext() {
        return classDefinition.getContext();
      }
    });
  }

  // public method //
  @Override
  public MethodCommentBuilder<T> publicMethod(final MetaClass returnType, final String name) {
    return genMethod(Scope.Public, returnType, name, DefParameters.none());
  }

  @Override
  public MethodCommentBuilder<T> publicMethod(final Class<?> returnType, final String name) {
    return publicMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodCommentBuilder<T> publicMethod(final MetaClass returnType, final String name, final MetaClass... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> publicMethod(final Class<?> returnType, final String name, final Class<?>... parms) {
    return publicMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> publicMethod(final MetaClass returnType, final String name, final Parameter... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodCommentBuilder<T> publicMethod(final Class<?> returnType, final String name, final Parameter... parms) {
    return publicMethod(MetaClassFactory.get(returnType), name, parms);
  }

  // private method //
  @Override
  public MethodCommentBuilder<T> privateMethod(final MetaClass returnType, final String name) {
    return genMethod(Scope.Private, returnType, name, DefParameters.none());
  }

  @Override
  public MethodCommentBuilder<T> privateMethod(final Class<?> returnType, final String name) {
    return privateMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodCommentBuilder<T> privateMethod(final MetaClass returnType, final String name, final MetaClass... parms) {
    return genMethod(Scope.Private, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> privateMethod(final Class<?> returnType, final String name, final Class<?>... parms) {
    return privateMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> privateMethod(final MetaClass returnType, final String name, final Parameter... parms) {
    return genMethod(Scope.Private, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodCommentBuilder<T> privateMethod(final Class<?> returnType, final String name, final Parameter... parms) {
    return privateMethod(MetaClassFactory.get(returnType), name, parms);
  }

  // protected method //
  @Override
  public MethodCommentBuilder<T> protectedMethod(final MetaClass returnType, final String name) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.none());
  }

  @Override
  public MethodCommentBuilder<T> protectedMethod(final Class<?> returnType, final String name) {
    return protectedMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodCommentBuilder<T> protectedMethod(final MetaClass returnType, final String name, final MetaClass... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> protectedMethod(final Class<?> returnType, final String name, final Class<?>... parms) {
    return protectedMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> protectedMethod(final MetaClass returnType, final String name, final Parameter... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodCommentBuilder<T> protectedMethod(final Class<?> returnType, final String name, final Parameter... parms) {
    return protectedMethod(MetaClassFactory.get(returnType), name, parms);
  }

  // package-private method //
  @Override
  public MethodCommentBuilder<T> packageMethod(final MetaClass returnType, final String name) {
    return genMethod(Scope.Package, returnType, name, DefParameters.none());
  }

  @Override
  public MethodCommentBuilder<T> packageMethod(final Class<?> returnType, final String name) {
    return packageMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodCommentBuilder<T> packageMethod(final MetaClass returnType, final String name, final MetaClass... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> packageMethod(final Class<?> returnType, final String name, final Class<?>... parms) {
    return packageMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodCommentBuilder<T> packageMethod(final MetaClass returnType, final String name, final Parameter... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodCommentBuilder<T> packageMethod(final Class<?> returnType, final String name, final Parameter... parms) {
    return packageMethod(MetaClassFactory.get(returnType), name, parms);
  }

  private MethodCommentBuilder<T> genMethod(final Scope scope,
                                            final MetaClass returnType,
                                            final String name,
                                            final DefParameters defParameters) {

    return new MethodBlockBuilderImpl<T>(new MethodBuildCallback<T>() {
      @Override
      public T callback(final BlockStatement statement,
                        final DefParameters parameters,
                        final DefModifiers modifiers,
                        final ThrowsDeclaration throwsDeclaration,
                        final List<Annotation> annotations,
                        final String comment) {

        final DefParameters dParameters;

        if (parameters != null) {
          dParameters = parameters;
        }
        else {
          dParameters = defParameters;
        }

        final BuildMetaMethod buildMetaMethod = new BuildMetaMethod(classDefinition, statement, scope,
                modifiers, name, returnType, dParameters, throwsDeclaration);

        if (annotations != null) {
          buildMetaMethod.addAnnotations(annotations);
        }

        buildMetaMethod.setMethodComment(comment);
        classDefinition.addMethod(buildMetaMethod);
        return (T) ClassBuilder.this;
      }
    });
  }

  @Override
  public FieldBuildStart<T> publicField(final String name, final MetaClass type) {
    return genField(Scope.Public, name, type);
  }

  @Override
  public FieldBuildStart<T> publicField(final String name, final Class<?> type) {
    return publicField(name, MetaClassFactory.get(type));
  }

  @Override
  public FieldBuildStart<T> privateField(final String name, final MetaClass type) {
    return genField(Scope.Private, name, type);
  }

  @Override
  public FieldBuildStart<T> privateField(final String name, final Class<?> type) {
    return privateField(name, MetaClassFactory.get(type));
  }

  @Override
  public FieldBuildStart<T> protectedField(final String name, final MetaClass type) {
    return genField(Scope.Protected, name, type);
  }

  @Override
  public FieldBuildStart<T> protectedField(final String name, final Class<?> type) {
    return protectedField(name, MetaClassFactory.get(type));
  }

  @Override
  public FieldBuildStart<T> packageField(final String name, final MetaClass type) {
    return genField(Scope.Package, name, type);
  }

  @Override
  public FieldBuildStart<T> packageField(final String name, final Class<?> type) {
    return packageField(name, MetaClassFactory.get(type));
  }

  private FieldBuildStart<T> genField(final Scope scope, final String name,
                                      final MetaClass type) {
    return new FieldBuilder<T>(new BuildCallback<T>() {
      @Override
      public T callback(final Statement statement) {
        final BuildMetaField buildMetaField
                = new BuildMetaField(classDefinition, statement, scope, type, name);

        classDefinition.addField(buildMetaField);
        return (T) ClassBuilder.this;
      }

      @Override
      public Context getParentContext() {
        return classDefinition.getContext();
      }
    }, scope, type, name);
  }

  @Override
  public BuildMetaClass getClassDefinition() {
    return classDefinition;
  }

  @Override
  public String toJavaString() {
    try {
      return classDefinition.toJavaString();
    }
    catch (Throwable t) {
      GenUtil.throwIfUnhandled("error generating class: " + classDefinition.getFullyQualifiedName(), t);
      return null;
    }
  }

  @Override
  public ClassStructureBuilder<T> declaresInnerClass(InnerClass ic) {
    classDefinition.addInnerClass(ic);
    return this;
  }
}
