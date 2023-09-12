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

import org.jboss.errai.codegen.Context;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;

/**
 * An empty statement. Use {@link #INSTANCE} to obtain an instance of this type.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 */
public class EmptyStatement implements Statement {

  /**
   * Sharable empty statement instance.
   */
  public static final Statement INSTANCE = new EmptyStatement();

  /**
   * Private constructor to enforce singletonness of this class.
   */
  private EmptyStatement() {
  }

  /**
   * Returns the empty string.
   */
  @Override
  public String generate(Context context) {
    return "";
  }

  /**
   * Always returns the MetaClass for java.lang.Object.
   */
  @Override
  public MetaClass getType() {
    return MetaClassFactory.get(Object.class);
  }
}
