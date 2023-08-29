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


package org.jboss.errai.codegen.test.meta.java;

import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.codegen.test.meta.AbstractMetaClassTest;
import org.jboss.errai.codegen.test.model.PortableIntegerParameterDefinition;
import org.junit.Test;

import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;
import static org.junit.Assert.assertEquals;

public class JavaReflectionMetaClassTest extends AbstractMetaClassTest {

  @Override
  protected MetaClass getMetaClassImpl(Class<?> javaClass) {
    return JavaReflectionClass.newInstance(javaClass);
  }

  @Override
  protected Class<? extends MetaClass> getTypeOfMetaClassBeingTested() {
    return JavaReflectionClass.class;
  }

  /**
   * This reproduces a bug found in the JavaReflectionClass.
   * PortableIntegerParameterDefinition overrides a method with a generic return
   * type (getValue). Class.getDeclaredMethods returns a bridge method with a
   * return type of the upper type bound (in this case, Serializable). This
   * resulted in JavaReflectionClass.getMethods() returning a MetaMethod for
   * bridge method with the wrong return type, leading to seemingly mysterious
   * codegen errors.
   */
  @Test
  public void testMethodReturnTypeIsSpecializedTypeAndNotFromBridgeMethod() throws Exception {
    final MetaClass mc = getMetaClassImpl(PortableIntegerParameterDefinition.class);
    final MetaMethod method = mc.getBestMatchingMethod("getValue", new MetaClass[0]);
    final ContextualStatementBuilder invokeStmt = nestedCall(newObject(mc)).invoke("getValue");
    // Force statement to load return type
    invokeStmt.toJavaString();
    final MetaClass stmtReturnType = invokeStmt.getType();

    final MetaClass expected = getMetaClassImpl(java.lang.Integer.class);
    assertEquals(expected, method.getReturnType());
    assertEquals(expected, stmtReturnType);
  }

}
