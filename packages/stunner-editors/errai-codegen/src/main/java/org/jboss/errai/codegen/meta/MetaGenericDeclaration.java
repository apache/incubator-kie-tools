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
 * Umbrella interface for {@link MetaClass}, {@link MetaMethod}, and
 * {@link MetaConstructor}: the three places where a generic type variable can
 * be declared.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public interface MetaGenericDeclaration {

  /**
   * Returns the array of type variables declared on this generic declaration
   * site, in the order they were declared in the source code.
   *
   * @return the type variables declared at this site. The returned array is
   *         never null, but it will be empty if no type variable are declared
   *         at this site.
   */
  public MetaTypeVariable[] getTypeParameters();
}
