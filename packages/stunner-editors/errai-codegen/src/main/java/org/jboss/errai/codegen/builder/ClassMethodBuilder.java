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


package org.jboss.errai.codegen.builder;

import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface ClassMethodBuilder<T extends ClassStructureBuilder<T>> extends ClassConstructorBuilder<T>,
                                                                                ClassFieldBuilder<T>, Builder {

  public MethodCommentBuilder<T> publicMethod(MetaClass returnType, String name);

  public MethodCommentBuilder<T> publicMethod(Class<?> returnType, String name);

  public MethodCommentBuilder<T> publicMethod(MetaClass returnType, String name, MetaClass... parms);

  public MethodCommentBuilder<T> publicMethod(Class<?> returnType, String name, Class<?>... parms);

  public MethodCommentBuilder<T> publicMethod(MetaClass returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> publicMethod(Class<?> returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> privateMethod(MetaClass returnType, String name);

  public MethodCommentBuilder<T> privateMethod(Class<?> returnType, String name);

  public MethodCommentBuilder<T> privateMethod(MetaClass returnType, String name, MetaClass... parms);

  public MethodCommentBuilder<T> privateMethod(Class<?> returnType, String name, Class<?>... parms);

  public MethodCommentBuilder<T> privateMethod(MetaClass returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> privateMethod(Class<?> returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> protectedMethod(MetaClass returnType, String name);

  public MethodCommentBuilder<T> protectedMethod(Class<?> returnType, String name);

  public MethodCommentBuilder<T> protectedMethod(MetaClass returnType, String name,
                                               MetaClass... parms);

  public MethodCommentBuilder<T> protectedMethod(Class<?> returnType, String name, Class<?>... parms);

  public MethodCommentBuilder<T> protectedMethod(MetaClass returnType, String name,
                                               Parameter... parms);

  public MethodCommentBuilder<T> protectedMethod(Class<?> returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> packageMethod(MetaClass returnType, String name);

  public MethodCommentBuilder<T> packageMethod(Class<?> returnType, String name);

  public MethodCommentBuilder<T> packageMethod(MetaClass returnType, String name, MetaClass... parms);

  public MethodCommentBuilder<T> packageMethod(Class<?> returnType, String name, Class<?>... parms);

  public MethodCommentBuilder<T> packageMethod(MetaClass returnType, String name, Parameter... parms);

  public MethodCommentBuilder<T> packageMethod(Class<?> returnType, String name, Parameter... parms);
}
