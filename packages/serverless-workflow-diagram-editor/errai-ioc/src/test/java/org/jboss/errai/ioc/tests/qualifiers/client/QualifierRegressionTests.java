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


package org.jboss.errai.ioc.tests.qualifiers.client;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.tests.qualifiers.client.res.AImpl;
import org.jboss.errai.ioc.tests.qualifiers.client.res.ClassWIthArrayValuedQual;
import org.jboss.errai.ioc.tests.qualifiers.client.res.InjectionPoint;
import org.jboss.errai.ioc.tests.qualifiers.client.res.QualWithArrayValue;

public class QualifierRegressionTests extends IOCClientTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.qualifiers.QualifierRegressionTests";
  }

  /**
   * Regression test for ERRAI-340
   */
  public void testInjectWithUnqualifiedImpl() throws Exception {
    InjectionPoint injectionPoint = IOC.getBeanManager().lookupBean(InjectionPoint.class).getInstance();

    assertTrue("Injected value should be instance of AImpl", injectionPoint.getInjected() instanceof AImpl);
  }

  public void testQualifierEqualityWithArrayValue() throws Exception {
    // Test passes as long as this does not cause an error
    try {
      IOC.getBeanManager().lookupBean(ClassWIthArrayValuedQual.class, new QualWithArrayValue() {

        @Override
        public Class<? extends Annotation> annotationType() {
          return QualWithArrayValue.class;
        }

        @Override
        public String[] value() {
          return new String[] { "test" };
        }
      }).getInstance();
    } catch (Throwable t) {
      throw new AssertionError("An error occured while looking up a bean with an array-valued qualifier.", t);
    }
  }

}
