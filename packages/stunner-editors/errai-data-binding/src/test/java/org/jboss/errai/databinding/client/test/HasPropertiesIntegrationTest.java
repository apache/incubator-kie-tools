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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.NonExistingPropertyException;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.client.TestModelBindableProxyMethods;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.junit.Test;

/**
 * Tests functionality provided by the {@link HasProperties} API.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class HasPropertiesIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.databinding.DataBindingTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Convert.deregisterDefaultConverters();
  }

  @Test
  public void testPropertyMapIsReadOny() {
    final HasProperties model = (HasProperties) DataBinder.forType(TestModel.class).getModel();
    final Map<String, PropertyType> properties = model.getBeanProperties();
    try {
      properties.put("prop1", new PropertyType(String.class));
      fail("Expected UnsupportedOperationException");
    }
    catch(UnsupportedOperationException uoe) {
      // expected
    }
  }

  @Test
  public void testGetProperties() {
    final HasProperties model = (HasProperties) DataBinder.forType(TestModel.class).getModel();
    final Map<String, PropertyType> properties = model.getBeanProperties();
    final Set<String> actualProperties = properties.keySet();
    final Set<String> expectedProperties = new HashSet<String>(Arrays.asList("agent", "amountDec", "id", "amountInt",
            "lastChanged", "value", "name", "age", "active", "child", "oldValue"));

    assertEquals(expectedProperties, actualProperties);
  }

  @Test
  public void testChangePropertyValue() {
    final TestModel model = DataBinder.forType(TestModel.class).getModel();
    final HasProperties properties = (HasProperties) model;

    assertNull(properties.get("value"));
    assertNull(model.getValue());

    properties.set("value", "value");
    assertEquals("value", model.getValue());
    assertSame(model.getValue(), properties.get("value"));
  }

  @Test
  public void testGetThrowsNonExistingPropertyException() {
    final HasProperties model = (HasProperties) DataBinder.forType(TestModel.class).getModel();
    try {
      model.get("prop1");
      fail("Expected NonExistingPropertyException");
    }
    catch(final NonExistingPropertyException ex) {
      assertTrue("Property name was not mentioned in error message: " + ex.getMessage(), ex.getMessage().contains("prop1"));
      assertTrue("Entity type was not mentioned in error message: " + ex.getMessage(), ex.getMessage().contains("TestModel"));
    }
  }

  @Test
  public void testSetThrowsNonExistingPropertyException() {
    final HasProperties model = (HasProperties) DataBinder.forType(TestModel.class).getModel();
    try {
      model.set("prop1", "value");
      fail("Expected NonExistingPropertyException");
    }
    catch(final NonExistingPropertyException ex) {
      assertTrue("Property name was not mentioned in error message: " + ex.getMessage(), ex.getMessage().contains("prop1"));
      assertTrue("Entity type was not mentioned in error message: " + ex.getMessage(), ex.getMessage().contains("TestModel"));
    }
  }
  
  @Test
  public void testSkippingBindableProxyMethods() {
    final TestModelBindableProxyMethods model = DataBinder.forType(TestModelBindableProxyMethods.class).getModel();
    
    model.set("prop", "someValue");
    
    assertEquals("someValue", model.get("prop"));
  }
}
