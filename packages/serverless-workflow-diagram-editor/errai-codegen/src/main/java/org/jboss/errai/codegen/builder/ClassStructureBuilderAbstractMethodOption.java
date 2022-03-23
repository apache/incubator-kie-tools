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

package org.jboss.errai.codegen.builder;

import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.builder.impl.MethodBuilderAbstractOption;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface ClassStructureBuilderAbstractMethodOption extends
    ClassStructureBuilder<ClassStructureBuilderAbstractMethodOption>,
    ClassConstructorBuilder<ClassStructureBuilderAbstractMethodOption>,
    ClassFieldBuilder<ClassStructureBuilderAbstractMethodOption>,
    Builder {

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      MetaClass returnType, String name, Parameter... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> publicAbstractMethod(
      Class<?> returnType, String name, Parameter... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      MetaClass returnType, String name, Parameter... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> protectedAbstractMethod(
      Class<?> returnType, String name, Parameter... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name, MetaClass... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name, Class<?>... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      MetaClass returnType, String name, Parameter... parms);

  public MethodBuilderAbstractOption<ClassStructureBuilderAbstractMethodOption> packageAbstractMethod(
      Class<?> returnType, String name, Parameter... parms);
}
