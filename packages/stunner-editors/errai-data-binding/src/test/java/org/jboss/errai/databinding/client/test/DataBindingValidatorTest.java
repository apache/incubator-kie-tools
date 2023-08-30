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


package org.jboss.errai.databinding.client.test;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.rebind.DataBindingValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link DataBindingValidator} rebind utility.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class DataBindingValidatorTest {
  
  MetaClass testClass = MetaClassFactory.get(TestModel.class);

  @Test
  public void testLeadingDotFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(testClass, ".name"));
  }
  
  @Test
  public void testTrailingDotFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(testClass, "name."));
  }
  
  @Test
  public void testNonBindableTypeFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(MetaClassFactory.get(String.class), "length"));
  }
  
  @Test
  public void testSinglePropertyPasses() {
    assertTrue(DataBindingValidator.isValidPropertyChain(testClass, "value"));
  }
  
  @Test
  public void testPropertyChainPasses() {
    assertTrue(DataBindingValidator.isValidPropertyChain(testClass, "child.child.value"));
  }
  
  @Test
  public void testPropertyChainFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(testClass, "child.value.value"));
  }
  
  @Test
  public void testDoubleDotInChainFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(testClass, "child.child..value"));
  }
  
  @Test
  public void testLeadingAndTrailingDotInChainFails() {
    assertFalse(DataBindingValidator.isValidPropertyChain(testClass, ".child.child.value."));
  }
  
  @Test(expected=NullPointerException.class)
  public void testExceptionOnNullType() {
    DataBindingValidator.isValidPropertyChain(null, "child");
  }

  @Test(expected=NullPointerException.class)
  public void testExceptionOnNullPropertyChain() {
    DataBindingValidator.isValidPropertyChain(testClass, null);
  }

}
