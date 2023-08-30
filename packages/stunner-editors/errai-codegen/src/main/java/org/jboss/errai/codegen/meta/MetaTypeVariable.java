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


package org.jboss.errai.codegen.meta;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public interface MetaTypeVariable extends MetaType {

  /**
   * Returns an array of the upper bounds on this type variable. For each entry
   * in the array, if the bound can be resolved to a type, it will be resolved
   * (the array entry will be a {@link MetaClass} or a
   * {@link MetaParameterizedType}); otherwise (the type variable is
   * unresolvable) it will be a type variable or a parameterized type with an
   * unresolved type variable itself.
   */
  public MetaType[] getBounds();

  // note: Java's TypeVariable interface has a getGenericDeclaration() method that returns the
  // class, method, or constructor where the type variable was declared. Unfortunately, this
  // information is not available from GWT's type system.

}
