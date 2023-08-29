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


package org.jboss.errai.codegen.gwt.test;

import java.io.File;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.gwt.GWTParameterizedType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GWTParameterizedTypeTest {

  private static final TypeOracle mockacle;
  static {
    MockacleFactory f = new MockacleFactory(new File(
            "../errai-codegen/src/test/java"));
    f.addTestClass("org.jboss.errai.codegen.test.model.GenericArrayCollectionTestModel");

    mockacle = f.generateMockacle();
  }

  @Test
  public void getTypeParameters_GenericArray() {
    JClassType type = mockacle
            .findType("org.jboss.errai.codegen.test.model.GenericArrayCollectionTestModel");
    JType returnType = type.getMethods()[0].getReturnType();
    GWTParameterizedType parameterizedType = new GWTParameterizedType(mockacle,
            returnType.isParameterized());
    MetaType[] typeParameters = parameterizedType.getTypeParameters();
    assertEquals(typeParameters.length, 1);
    assertEquals(typeParameters[0].getName(), "Object[]");
  }

}
