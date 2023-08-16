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
public interface ClassConstructorBuilder<T extends ClassStructureBuilder> extends Builder {
  public ConstructorBlockBuilder<T> publicConstructor();
  public ConstructorBlockBuilder<T> publicConstructor(MetaClass... parms);
  public ConstructorBlockBuilder<T> publicConstructor(Class<?>... parms);
  public ConstructorBlockBuilder<T> publicConstructor(Parameter... parms);

  public ConstructorBlockBuilder<T> privateConstructor();
  public ConstructorBlockBuilder<T> privateConstructor(MetaClass... parms);
  public ConstructorBlockBuilder<T> privateConstructor(Class<?>... parms);
  public ConstructorBlockBuilder<T> privateConstructor(Parameter... parms);

  public ConstructorBlockBuilder<T> protectedConstructor();
  public ConstructorBlockBuilder<T> protectedConstructor(MetaClass... parms);
  public ConstructorBlockBuilder<T> protectedConstructor(Class<?>... parms);
  public ConstructorBlockBuilder<T> protectedConstructor(Parameter... parms);

  public ConstructorBlockBuilder<T> packageConstructor();
  public ConstructorBlockBuilder<T> packageConstructor(MetaClass... parms);
  public ConstructorBlockBuilder<T> packageConstructor(Class<?>... parms);
  public ConstructorBlockBuilder<T> packageConstructor(Parameter... parms);
}
