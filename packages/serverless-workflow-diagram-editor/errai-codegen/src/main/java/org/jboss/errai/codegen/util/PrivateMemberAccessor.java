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

import org.jboss.errai.codegen.Modifier;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;

/**
 * @author Mike Brock
 */
public interface PrivateMemberAccessor {
  public void createWritableField(MetaClass type,
                                  ClassStructureBuilder<?> classBuilder,
                                  MetaField field,
                                  Modifier[] modifiers);

  public void createReadableField(MetaClass type,
                                  ClassStructureBuilder<?> classBuilder,
                                  MetaField field,
                                  Modifier[] modifiers);

  public void makeMethodAccessible(final ClassStructureBuilder<?> classBuilder,
                                   final MetaMethod field,
                                   Modifier[] modifiers);

  public void makeConstructorAccessible(final ClassStructureBuilder<?> classBuilder,
                                   final MetaConstructor field);
}
