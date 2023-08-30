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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.BindableListWrapper;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.DataBindingModuleBootstrapper;
import org.jboss.errai.databinding.client.MapPropertyType;
import org.jboss.errai.databinding.client.NonExistingPropertyException;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;

import static org.jboss.errai.databinding.client.components.ListComponent.forIsWidgetComponent;

/**
 * Tests binding to lists with the {@link DataBinder}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class MapBindingIntegrationTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.databinding.DataBindingTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    new DataBindingModuleBootstrapper().run();
  }

  public void testMapWithNonBindableProperties() throws Exception {
    final Map<String, PropertyType> propertyTypes = new HashMap<>();
    propertyTypes.put("str", new PropertyType(String.class));
    propertyTypes.put("num", new PropertyType(Integer.class));

    final DataBinder<Map<String, Object>> binder = DataBinder.forMap(propertyTypes);
    final TextBox strTextBox = new TextBox();
    final IntegerBox numIntBox = new IntegerBox();

    binder
      .bind(strTextBox, "str")
      .bind(numIntBox, "num");

    final Map<String, Object> map = binder.getModel();
    assertNotNull(map);

    map.put("str", "foo");
    assertEquals("foo", strTextBox.getValue());

    map.put("num", 42);
    assertEquals(Integer.valueOf(42), numIntBox.getValue());
  }

  public void testMapWithBindableProperties() throws Exception {
    final Map<String, PropertyType> propertyTypes = new HashMap<>();
    propertyTypes.put("list", new PropertyType(List.class, false, true));
    propertyTypes.put("bindable", new PropertyType(TestModel.class, true, false));

    final DataBinder<Map<String, Object>> binder = DataBinder.forMap(propertyTypes);
    final IntegerBox ageIntBox = new IntegerBox();
    final ListComponent<String, TextBox> component = forIsWidgetComponent(TextBox::new, c -> {}).inDiv();

    binder
      .bind(component, "list", Convert.identityConverter(List.class))
      .bind(ageIntBox, "bindable.age");

    final Map<String, Object> map = binder.getModel();
    assertNotNull(map);

    map.put("bindable", new TestModel());
    final TestModel model = (TestModel) map.get("bindable");
    model.setAge(99);
    assertTrue("TestModel is not proxied.", model instanceof BindableProxy);
    assertEquals(Integer.valueOf(99), ageIntBox.getValue());

    map.put("list", new ArrayList<>());
    final List<String> list = (List<String>) map.get("list");
    assertTrue("List value was not proxied.", list instanceof BindableListWrapper);
    list.add("moogah");
    try {
      assertEquals("moogah", component.getComponent(0).getValue());
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError("Component not added for list entry.", t);
    }
  }

  public void testMapWithBindableMap() throws Exception {
    final Map<String, PropertyType> innerMapPropertyTypes = new HashMap<>();
    innerMapPropertyTypes.put("str", new PropertyType(String.class));
    innerMapPropertyTypes.put("num", new PropertyType(Integer.class));

    final Map<String, PropertyType> outerMapPropertyTypes = new HashMap<>();
    outerMapPropertyTypes.put("map", new MapPropertyType(innerMapPropertyTypes));

    final DataBinder<Map<String, Object>> binder = DataBinder.forMap(outerMapPropertyTypes);
    final TextBox strTextBox = new TextBox();
    final IntegerBox numIntBox = new IntegerBox();

    binder
      .bind(strTextBox, "map.str")
      .bind(numIntBox, "map.num");

    final Map<String, Object> outerMap = binder.getModel();
    assertNotNull(outerMap);

    outerMap.put("map", new HashMap<>());
    final Map<String, Object> innerMap = (Map<String, Object>) outerMap.get("map");

    innerMap.put("str", "foo");
    assertEquals("foo", strTextBox.getValue());

    innerMap.put("num", 42);
    assertEquals(Integer.valueOf(42), numIntBox.getValue());
  }

  public void testPutAllUpdatesWidgets() throws Exception {
    final Map<String, PropertyType> propertyTypes = new HashMap<>();
    propertyTypes.put("str", new PropertyType(String.class));
    propertyTypes.put("num", new PropertyType(Integer.class));

    final DataBinder<Map<String, Object>> binder = DataBinder.forMap(propertyTypes);
    final TextBox strTextBox = new TextBox();
    final IntegerBox numIntBox = new IntegerBox();

    binder
      .bind(strTextBox, "str")
      .bind(numIntBox, "num");

    final Map<String, Object> map = binder.getModel();
    assertNotNull(map);

    final Map<String, Object> otherMap = new HashMap<>();
    otherMap.put("str", "foo");
    otherMap.put("num", 42);
    map.putAll(otherMap);

    assertEquals("foo", strTextBox.getValue());
    assertEquals(Integer.valueOf(42), numIntBox.getValue());
  }

  public void testPutAllThrowsErrorForUnrecognizedProperties() throws Exception {
    final Map<String, PropertyType> propertyTypes = new HashMap<>();
    propertyTypes.put("str", new PropertyType(String.class));
    propertyTypes.put("num", new PropertyType(Integer.class));

    final DataBinder<Map<String, Object>> binder = DataBinder.forMap(propertyTypes);
    final TextBox strTextBox = new TextBox();
    final IntegerBox numIntBox = new IntegerBox();

    binder
      .bind(strTextBox, "str")
      .bind(numIntBox, "num");

    final Map<String, Object> map = binder.getModel();
    assertNotNull(map);

    final Map<String, Object> otherMap = new HashMap<>();
    otherMap.put("nonexistent", "foible");

    try {
      map.putAll(otherMap);
      fail("Putting non-existent property into map did not cause an exception.");
    } catch (final NonExistingPropertyException ex) {
      // success
    } catch (final AssertionError ae) {
      throw ae;
    } catch (final Throwable t) {
      throw new AssertionError("Wrong exception thrown: " + t.getMessage(), t);
    }
  }
}
