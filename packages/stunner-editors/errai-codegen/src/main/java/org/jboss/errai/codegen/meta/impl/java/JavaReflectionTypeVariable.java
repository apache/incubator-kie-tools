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


package org.jboss.errai.codegen.meta.impl.java;

import java.lang.reflect.TypeVariable;

import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;

/**
 * Java Reflection based implementation of {@link MetaTypeVariable}.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class JavaReflectionTypeVariable implements MetaTypeVariable {
  private final TypeVariable<?> variable;

  public JavaReflectionTypeVariable(final TypeVariable<?> variable) {
    this.variable = variable;
  }

  @Override
  public MetaType[] getBounds() {
    return JavaReflectionUtil.fromTypeArray(variable.getBounds());
  }

  @Override
  public String getName() {
    return variable.getName();
  }
}
