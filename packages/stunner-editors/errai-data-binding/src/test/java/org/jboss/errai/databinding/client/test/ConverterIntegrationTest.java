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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.IdentityConverter;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.junit.Test;

/**
 * Tests functionality related to the use of {@link Converter}s for data binding.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ConverterIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.databinding.DataBindingTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    Convert.deregisterDefaultConverters();
    super.gwtSetUp();
  }

  @Test
  public void testBindingSpecificConverter() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1701;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "testCustomConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age", converter).getModel();

    textBox.setValue("321", true);
    assertEquals("Model not properly updated using custom converter", Integer.valueOf(1701), model.getAge());

    model.setAge(123);
    assertEquals("Widget not properly updated using custom converter", "testCustomConverter", textBox.getText());
  }

  @Test
  public void testBindingSpecificConverterAndNullValues() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return ("".equals(widgetValue)) ? -1 : 0;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return (modelValue == null) ? "null-widget" : modelValue.toString();
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age", converter).getModel();

    // Set initial non-empty value so that value change handlers are called on the next call.
    textBox.setValue("1337", false);

    textBox.setValue(null, true);
    assertEquals("Model not properly updated using custom converter", Integer.valueOf(-1), model.getAge());

    model.setAge(null);
    assertEquals("Widget not properly updated using custom converter", "null-widget", textBox.getText());
  }

  @Test
  public void testBinderRetainsConverterAfterModelInstanceChange() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1701;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "testCustomConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final TextBox textBox = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox, "age", converter);

    final TestModel oldModel = binder.getModel();

    binder.setModel(new TestModel());
    textBox.setValue("321", true);
    assertEquals("Model not properly updated using custom converter", Integer.valueOf(1701), binder.getModel().getAge());

    binder.getModel().setAge(123);
    assertEquals("Widget not properly updated using custom converter", "testCustomConverter", textBox.getText());

    assertEquals("Original model should not have been updated", null, oldModel.getAge());
  }

  @Test
  public void testBindingSpecificConverterForReadOnlyField() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        throw new UnsupportedOperationException("Should never be called!");
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "test";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final Label label = new Label();
    final TestModel model = DataBinder.forModel(new TestModel())
      .bind(label, "age", converter).getModel();

    model.setAge(123);
    assertEquals("Widget not properly updated using custom converter", "test", label.getText());
  }

  @Test
  public void testBindingWithGlobalDefaultConverter() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1701;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "testGlobalDefaultConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };
    Convert.registerDefaultConverter(Integer.class, String.class, converter);

    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age").getModel();

    textBox.setValue("321", true);
    assertEquals("Model not properly updated using global default converter", Integer.valueOf(1701), model.getAge());

    model.setAge(123);
    assertEquals("Widget not properly updated using global default converter",
        "testGlobalDefaultConverter", textBox.getText());
  }

  @Test
  public void testBindingWithGlobalDefaultConverterAndNullValues() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return ("".equals(widgetValue)) ? -1 : 0;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return (modelValue == null) ? "null-widget" : modelValue.toString();
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    Convert.registerDefaultConverter(Integer.class, String.class, converter);

    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age").getModel();

    // Set initial non-empty value so that value change handlers are called on the next call.
    textBox.setValue("1337", false);

    textBox.setValue(null, true);
    assertEquals("Model not properly updated using global default converter", Integer.valueOf(-1), model.getAge());

    model.setAge(null);
    assertEquals("Widget not properly updated using global default converter", "null-widget", textBox.getText());
  }

  @Test
  public void testBindingWithAutoRegisteredDefaultConverter() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "active").getModel();

    textBox.setValue("123", true);
    assertEquals("Model not properly updated using global default converter", true, model.isActive());

    model.setActive(false);
    assertEquals("Widget not properly updated using global default converter",
        "AutoRegisteredDefaultConverter", textBox.getText());
  }

  @Test
  public void testOverrideGlobalDefaultConverter() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1701;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "globalDefaultConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };
    Convert.registerDefaultConverter(Integer.class, String.class, converter);

    final Converter<Integer, String> bindingConverter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "bindingSpecificConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age", bindingConverter).getModel();

    textBox.setValue("321", true);
    assertEquals("Model not properly updated using custom converter", Integer.valueOf(1), model.getAge());

    model.setAge(123);
    assertEquals("Widget not properly updated using custom converter", "bindingSpecificConverter", textBox.getText());
  }

  @Test
  public void testInitialStateSyncWithConverter() {
    final Converter<Integer, String> converter = new Converter<Integer, String>() {
      @Override
      public Integer toModelValue(final String widgetValue) {
        return 1701;
      }

      @Override
      public String toWidgetValue(final Integer modelValue) {
        return "customConverter";
      }

      @Override
      public Class<Integer> getModelType() {
        return Integer.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }
    };

    final TextBox textBox = new TextBox();
    textBox.setValue("123");
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox, "age", converter, StateSync.FROM_UI);
    assertEquals("Model not initialized based on widget's state", Integer.valueOf(1701), binder.getModel().getAge());

    final TestModel model = new TestModel();
    model.setAge(123);
    DataBinder.forModel(model).bind(textBox, "name", new IdentityConverter<String>(String.class) {
      @Override
      public String toWidgetValue(final String modelValue) {
        return "customConverter";
      }
    }, StateSync.FROM_MODEL);
    assertEquals("Model not initialized based on widget's state", "customConverter", textBox.getValue());
  }

  @Test
  public void testEmptyStringConversionToBigInteger() {
    final TextBox textBox = new TextBox();
    textBox.setText("test");
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "amountInt").getModel();

    textBox.setValue("", true);
    assertEquals("Failed to convert empty String to BigInteger", null, model.getAmountInt());
  }

  @Test
  public void testEmptyStringConversionToBigDecimal() {
    final TextBox textBox = new TextBox();
    textBox.setText("test");
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "amountDec").getModel();

    textBox.setValue("", true);
    assertEquals("Failed to convert empty String to BigDecimal", null, model.getAmountDec());
  }

  @Test
  public void testEmptyStringConversionToPrimitiveWrapper() {
    final TextBox textBox = new TextBox();
    textBox.setText("test");
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age").getModel();

    textBox.setValue("", true);
    assertEquals("Failed to convert empty String to primitive wrapper type", null, model.getAge());
  }

  @Test
  public void testBindingListWithConverter() throws Exception {
    final TextBox textBox = new TextBox();
    final DataBinder<List<String>> binder = DataBinder.forListOfType(String.class);
    final Converter<Collection<String>, String> converter = new Converter<Collection<String>, String>() {

      @SuppressWarnings("unchecked")
      @Override
      public Class<Collection<String>> getModelType() {
        return (Class) Collection.class;
      }

      @Override
      public Class<String> getComponentType() {
        return String.class;
      }

      @Override
      public Collection<String> toModelValue(final String componentValue) {
        final List<String> retVal = new ArrayList<>();
        for (int i = 0; i < componentValue.length(); i++) {
          retVal.add(componentValue.substring(i, i+1));
        }

        return retVal;
      }

      @Override
      public String toWidgetValue(final Collection<String> modelValue) {
        final StringBuilder builder = new StringBuilder();
        modelValue.forEach(s -> builder.append(s));

        return builder.toString();
      }
    };

    binder.bind(textBox, "this", converter);
    binder.getModel().add("A");
    binder.getModel().add("B");
    binder.getModel().add("C");

    assertEquals("ABC", textBox.getValue());

    textBox.setValue("CBA", true);

    assertEquals(Arrays.asList("C", "B", "A"), binder.getModel());
  }
}
