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

import org.jboss.errai.codegen.meta.MetaClass;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface ClassFieldBuilder<T extends ClassStructureBuilder<T>> extends Builder {
  public FieldBuildStart<T> publicField(String name, MetaClass type);
  public FieldBuildStart<T> publicField(String name, Class<?> type);

  public FieldBuildStart<T> privateField(String name, MetaClass type);
  public FieldBuildStart<T> privateField(String name, Class<?> type);

  public FieldBuildStart<T> protectedField(String name, MetaClass type);
  public FieldBuildStart<T> protectedField(String name, Class<?> type);

  public FieldBuildStart<T> packageField(String name, MetaClass type);
  public FieldBuildStart<T> packageField(String name, Class<?> type);
}
