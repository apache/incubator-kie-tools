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
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.ThrowsDeclaration;
import org.jboss.errai.codegen.builder.ClassStructureBuilderAbstractMethodOption;
import org.jboss.errai.codegen.builder.MethodBuildCallback;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaMethod;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ClassBuilderAbstractMethodOption extends ClassBuilder<ClassStructureBuilderAbstractMethodOption> 
    implements ClassStructureBuilderAbstractMethodOption {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  ClassBuilderAbstractMethodOption(ClassBuilder that, Context context) {
    super(that, context);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name) {
    return genMethod(Scope.Public, returnType, name, DefParameters.none());
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name) {
    return publicAbstractMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms) {
    return publicAbstractMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Public, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name, Parameter... parms) {
    return publicAbstractMethod(MetaClassFactory.get(returnType), name, parms);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.none());
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name) {
    return protectedAbstractMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms) {
    return protectedAbstractMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Protected, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name, Parameter... parms) {
    return protectedAbstractMethod(MetaClassFactory.get(returnType), name, parms);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name) {
    return genMethod(Scope.Package, returnType, name, DefParameters.none());
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name) {
    return packageAbstractMethod(MetaClassFactory.get(returnType), name);
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromTypeArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms) {
    return packageAbstractMethod(MetaClassFactory.get(returnType), name, MetaClassFactory.fromClassArray(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name, Parameter... parms) {
    return genMethod(Scope.Package, returnType, name, DefParameters.fromParameters(parms));
  }

  @Override
  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name, Parameter... parms) {
    return packageAbstractMethod(MetaClassFactory.get(returnType), name, parms);
  }

  private MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> genMethod(final Scope scope,
      final MetaClass returnType,
      final String name,
      final DefParameters defParameters) {

    return new MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption>(
            new MethodBuildCallback<ClassStructureBuilderAbstractMethodOption>() {
      @Override
      public ClassStructureBuilderAbstractMethodOption callback(final BlockStatement statement,
                                                                final DefParameters parameters,
                                                                final DefModifiers modifiers,
                                                                final ThrowsDeclaration throwsDeclaration,
                                                                final List<Annotation> annotations,
                                                                final String comment) {

        final BuildMetaMethod buildMetaMethod = new BuildMetaMethod(classDefinition, statement, scope, modifiers, name,
            returnType, defParameters, throwsDeclaration);

        if (annotations != null) {
          buildMetaMethod.addAnnotations(annotations);
        }
        buildMetaMethod.setMethodComment(comment);

        classDefinition.addMethod(buildMetaMethod);
        return ClassBuilderAbstractMethodOption.this;
      }
    });
  }
}
