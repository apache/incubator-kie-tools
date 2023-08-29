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


package org.jboss.errai.ui.test.binding.client;

import java.util.Date;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.test.binding.client.res.BindableEmailAnchor;
import org.jboss.errai.ui.test.binding.client.res.BindingDateConverter;
import org.jboss.errai.ui.test.binding.client.res.BindingTemplate;
import org.jboss.errai.ui.test.binding.client.res.InputElementsModel;
import org.jboss.errai.ui.test.binding.client.res.JsOverlayNumberInputElement;
import org.jboss.errai.ui.test.binding.client.res.JsPropertyDivElement;
import org.jboss.errai.ui.test.binding.client.res.TemplateFragmentWithoutFragmentId;
import org.jboss.errai.ui.test.binding.client.res.TemplateWithDefaultJsOverlayHasValueOverride;
import org.jboss.errai.ui.test.binding.client.res.TemplateWithInputElements;
import org.jboss.errai.ui.test.binding.client.res.TemplateWithJsOverlayHasValue;
import org.jboss.errai.ui.test.binding.client.res.TemplateWithJsPropertyHasValue;
import org.jboss.errai.ui.test.common.client.TestModel;
import org.jboss.errai.ui.test.common.client.dom.Element;
import org.jboss.errai.ui.test.common.client.dom.TextInputElement;
import org.junit.Test;

import static org.jboss.errai.common.client.util.EventTestingUtil.invokeEventListeners;
import static org.jboss.errai.common.client.util.EventTestingUtil.setupAddEventListenerInterceptor;

/**
 * Tests for the Errai UI/DataBinding integration.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BindingTemplateTest extends AbstractErraiCDITest {

  private static interface PropertyHandler<V> {
    void setProperty(V value);
    V getProperty();
  }

  private static class DefaultInputElementHandler implements PropertyHandler<String> {

    private final InputElement element;

    private DefaultInputElementHandler(final InputElement element) {
      this.element = element;
    }

    @Override
    public void setProperty(final String value) {
      element.setValue(value);
    }

    @Override
    public String getProperty() {
      return element.getValue();
    }

  }

  private static class CheckboxHandler implements PropertyHandler<Boolean> {

    private final InputElement element;

    private CheckboxHandler(final InputElement element) {
      this.element = element;
    }

    @Override
    public void setProperty(final Boolean value) {
      element.setChecked(value);
    }

    @Override
    public Boolean getProperty() {
      return element.isChecked();
    }

  }

  private static class IdentityConverter<T> implements Converter<T, T> {

    private final Class<T> type;

    private IdentityConverter(final Class<T> type) {
      this.type = type;
    }

    @Override
    public T toModelValue(final T widgetValue) {
      return widgetValue;
    }

    @Override
    public T toWidgetValue(final T modelValue) {
      return modelValue;
    }

    @Override
    public Class<T> getModelType() {
      return type;
    }

    @Override
    public Class<T> getComponentType() {
      return type;
    }

  }

  private static class DoubleConverter implements Converter<Double, String> {

    @Override
    public Double toModelValue(final String widgetValue) {
      return Double.valueOf(widgetValue);
    }

    @Override
    public String toWidgetValue(final Double modelValue) {
      return String.valueOf(modelValue);
    }

    @Override
    public Class<Double> getModelType() {
      return Double.class;
    }

    @Override
    public Class<String> getComponentType() {
      return String.class;
    }

  }

  private static class IntegerConverter implements Converter<Integer, String> {

    @Override
    public Integer toModelValue(final String widgetValue) {
      return Integer.valueOf(widgetValue);
    }

    @Override
    public String toWidgetValue(final Integer modelValue) {
      return String.valueOf(modelValue);
    }

    @Override
    public Class<Integer> getModelType() {
      return Integer.class;
    }

    @Override
    public Class<String> getComponentType() {
      return String.class;
    }

  }

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Override
  protected void gwtSetUp() throws Exception {
    setupAddEventListenerInterceptor();
    super.gwtSetUp();
  }

  @Test
  public void testAutomaticBindingWithCompositeTemplated() {
    final BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    automaticBindingAssertions(app.getCompositeTemplate());
  }

  @Test
  public void testAutomaticBindingWithNonCompositeTemplated() {
    final BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    automaticBindingAssertions(app.getNonCompositeTemplate());
  }

  @Test
  public void testInputElementBindings() throws Exception {
    final TemplateWithInputElements bean = IOC.getBeanManager().lookupBean(TemplateWithInputElements.class).getInstance();
    final InputElementsModel model = bean.binder.getModel();

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setText(value);
      }

      @Override
      public String getProperty() {
        return model.getText();
      }
    }, new DefaultInputElementHandler(bean.text), new IdentityConverter<>(String.class), "text", "test value", "other test value", bean.text);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setPassword(value);
      }

      @Override
      public String getProperty() {
        return model.getPassword();
      }
    }, new DefaultInputElementHandler(bean.password), new IdentityConverter<>(String.class), "password", "password123", "letmein123", bean.password);

    inputElementAssertions(new PropertyHandler<Double>() {

      @Override
      public void setProperty(final Double value) {
        model.setNumber(value);
      }

      @Override
      public Double getProperty() {
        return model.getNumber();
      }
    }, new DefaultInputElementHandler(bean.number), new DoubleConverter(), "number", 1.0, "2.0", bean.number);

    inputElementAssertions(new PropertyHandler<Integer>() {

      @Override
      public void setProperty(final Integer value) {
        model.setRange(value);
      }

      @Override
      public Integer getProperty() {
        return model.getRange();
      }
    }, new DefaultInputElementHandler(bean.range), new IntegerConverter(), "range", 10, "20", bean.range);

    inputElementAssertions(new PropertyHandler<Boolean>() {

      @Override
      public void setProperty(final Boolean value) {
        model.setCheckbox(value);
      }

      @Override
      public Boolean getProperty() {
        return model.isCheckbox();
      }
    }, new CheckboxHandler(bean.checkbox), new IdentityConverter<>(Boolean.class), "checkbox", true, false, bean.checkbox);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setFile(value);
      }

      @Override
      public String getProperty() {
        return model.getFile();
      }
    }, new DefaultInputElementHandler(bean.file), new IdentityConverter<>(String.class), "file", "file:///tmp/foo", "file:///tmp/bar", bean.file);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setEmail(value);
      }

      @Override
      public String getProperty() {
        return model.getEmail();
      }
    }, new DefaultInputElementHandler(bean.email), new IdentityConverter<>(String.class), "email", "a@b.c", "y@z", bean.email);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setColor(value);
      }

      @Override
      public String getProperty() {
        return model.getColor();
      }
    }, new DefaultInputElementHandler(bean.color), new IdentityConverter<>(String.class), "color", "#000000", "#FFFFFF", bean.color);

    inputElementAssertions(new PropertyHandler<Boolean>() {

      @Override
      public void setProperty(final Boolean value) {
        model.setRadio(value);
      }

      @Override
      public Boolean getProperty() {
        return model.getRadio();
      }
    }, new CheckboxHandler(bean.radio), new IdentityConverter<>(Boolean.class), "radio", true, false, bean.radio);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setTel(value);
      }

      @Override
      public String getProperty() {
        return model.getTel();
      }
    }, new DefaultInputElementHandler(bean.tel), new IdentityConverter<>(String.class), "tel", "4161234567", "6473217654", bean.tel);

    inputElementAssertions(new PropertyHandler<String>() {

      @Override
      public void setProperty(final String value) {
        model.setUrl(value);
      }

      @Override
      public String getProperty() {
        return model.getUrl();
      }
    }, new DefaultInputElementHandler(bean.url), new IdentityConverter<>(String.class), "url", "http://jboss.org", "https://redhat.com", bean.url);
  }

  @Test
  public void testBindingToJsTypeWithJsOverlayHasValue() throws Exception {
    final TemplateWithJsOverlayHasValue bean = IOC.getBeanManager().lookupBean(TemplateWithJsOverlayHasValue.class).getInstance();
    final InputElementsModel model = bean.binder.getModel();
    final JsOverlayNumberInputElement presenter = bean.number;

    assertNull("Model value should be null.", model.getNumber());
    assertNull("UI value should be null.", presenter.getValue());

    try {
      model.setNumber(1.0);
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting model property: " + t.getMessage(), t);
    }

    assertEquals("UI value was not updated.", model.getNumber(), presenter.getValue());

    try {
      presenter.setValue(2.0);
      invokeEventListeners(TemplateUtil.asElement(presenter), "change");
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting the UI value: " + t.getMessage(), t);
    }

    assertEquals("Model value was not updated.", presenter.getValue(), model.getNumber());
  }

  @Test
  public void testBindingToJsTypeWithJsPropertyHasValue() throws Exception {
    final TemplateWithJsPropertyHasValue bean = IOC.getBeanManager().lookupBean(TemplateWithJsPropertyHasValue.class).getInstance();
    final InputElementsModel model = bean.binder.getModel();
    final JsPropertyDivElement elementWrapper = bean.text;

    assertNull("Model value should be null.", model.getText());
    assertNull("UI value should be null.", elementWrapper.getValue());

    try {
      model.setText("1.0");
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting model property: " + t.getMessage(), t);
    }

    assertEquals("UI value was not updated.", model.getText(), elementWrapper.getValue());

    try {
      elementWrapper.setValue("2.0");
      invokeEventListeners(TemplateUtil.asElement(elementWrapper), "change");
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting the UI value: " + t.getMessage(), t);
    }

    assertEquals("Model value was not updated.", elementWrapper.getValue(), model.getText());
  }

  @Test
  public void testBindingToJsTypeInterfaceWithJsOverlayHasValue() throws Exception {
    final TemplateWithDefaultJsOverlayHasValueOverride bean = IOC.getBeanManager().lookupBean(TemplateWithDefaultJsOverlayHasValueOverride.class).getInstance();
    final InputElementsModel model = bean.binder.getModel();
    final BindableEmailAnchor elementWrapper = bean.email;

    assertNull("Model value should be null.", model.getEmail());
    assertEquals("UI value should be empty.", "", elementWrapper.getValue());

    try {
      model.setEmail("a@b");
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting model property: " + t.getMessage(), t);
    }

    assertEquals("UI value was not updated.", model.getEmail(), elementWrapper.getValue());

    try {
      elementWrapper.setValue("b@a");
      invokeEventListeners(TemplateUtil.asElement(elementWrapper), "change");
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred while setting the UI value: " + t.getMessage(), t);
    }

    assertEquals("Model value was not updated.", elementWrapper.getValue(), model.getEmail());
  }

  /**
   * Regression test for ERRAI-779
   */
  @Test
  public void testTemplateFragmentContainingWordBodyIsParsedWithoutError() throws Exception {
    try {
      IOC.getBeanManager()
              .lookupBean(TemplateFragmentWithoutFragmentId.class).getInstance();
    }
    catch (final Exception e) {
      fail("Loading templated instance caused an error: " + e.getMessage());
    }
  }

  private <M, U> void inputElementAssertions(final PropertyHandler<M> model, final PropertyHandler<U> ui,
          final Converter<M, U> converter, final String type, final M value1, final U value2, final InputElement element) {
    assertNotNull("The element for input[type='" + type + "'] was not injected.", element);
    assertEquals("The element for input[type='" + type + "'] has the wrong tag name.", "INPUT", element.getTagName());
    assertEquals("The element for input[type='" + type + "'] has the wrong type.", type, element.getType());

    assertFalse("Precondition failed: ui property already set to [" + value1 + "].", converter.toWidgetValue(value1).equals(ui.getProperty()));

    try {
      model.setProperty(value1);
    } catch (final Throwable t) {
      throw new RuntimeException("An error occurred setting [" + value1.toString() + "] for the model property.", t);
    }
    assertEquals("The UI value for input[type='" + type + "'] was not updated after a model change.",
            converter.toWidgetValue(value1), ui.getProperty());

    try {
      ui.setProperty(value2);
      invokeEventListeners(TemplateUtil.asElement(element), "change");
    } catch (final Throwable t) {
      throw new RuntimeException(
              "An error occurred for binding of input[type=" + type + "] setting ["
              + value2.toString() + "] for the ui value.", t);
    }
    assertEquals("The model value for input[type='" + type + "'] was not updated after a UI change.",
            converter.toModelValue(value2), model.getProperty());
  }

  private void automaticBindingAssertions(final BindingTemplate<?> template) {
    assertNotNull("Template instance was not injected!", template);

    final Label idLabel = template.getIdLabel();
    assertNotNull(idLabel);
    assertEquals("", idLabel.getText());

    final DivElement idDiv = template.getIdDiv();
    assertNotNull(idDiv);
    assertEquals("", idDiv.getInnerText());

    final TextBox nameTextBox = template.getNameTextBox();
    assertNotNull(nameTextBox);
    assertEquals("", nameTextBox.getValue());

    final TextBox dateTextBox = template.getDateTextBox();
    assertNotNull(dateTextBox);
    assertEquals("", dateTextBox.getValue());

    final TextBox phoneNumberBox = template.getPhoneNumberBox();
    assertNotNull(phoneNumberBox);
    assertEquals("", phoneNumberBox.getValue());

    final Element title = template.getTitleField();
    assertNotNull(title);
    assertEquals("", title.getInnerHTML());

    final TextInputElement age = template.getAge();
    assertNotNull(age);
    assertEquals("", age.getValue());

    final TestModel model = template.getModel();
    model.setId(1711);
    model.getChild().setName("errai");
    model.setLastChanged(new Date());
    model.setPhoneNumber("+1 555");
    model.setAge(50);
    model.setTitle("Mr.");
    assertEquals("Div (id) was not updated!", Integer.valueOf(model.getId()).toString(), idDiv.getInnerText());
    assertEquals("Label (id) was not updated!", Integer.valueOf(model.getId()).toString(), idLabel.getText());
    assertEquals("TextBox (name) was not updated!", model.getChild().getName(), nameTextBox.getValue());
    assertEquals("TextBox (date) was not updated using custom converter!", "testdate", dateTextBox.getValue());
    assertEquals("TextBox (phoneNumber) was not updated", model.getPhoneNumber(), phoneNumberBox.getValue());
    assertEquals("Element (titleField) was not updated!", model.getTitle(), title.getInnerHTML());
    assertEquals("Element (age) was not updated!", Integer.valueOf(model.getAge()).toString(), age.getValue());

    nameTextBox.setValue("updated", true);
    dateTextBox.setValue("updated", true);
    phoneNumberBox.setValue("+43 555", true);
    age.setValue("51");
    fireChangeEvent(age);

    assertEquals("Model (name) was not updated!", nameTextBox.getValue(), model.getChild().getName());
    assertEquals("Model (lastUpdate) was not updated using custom converter!",
        BindingDateConverter.TEST_DATE, model.getLastChanged());
    assertEquals("Model (phoneNumber) was not updated!", phoneNumberBox.getValue(), model.getPhoneNumber());
    assertEquals("Model (age) was not updated!", Integer.valueOf(age.getValue()), model.getAge());
  }

  private void fireChangeEvent(final Object element) {
    final NativeEvent changeEvent = Document.get().createChangeEvent();
    TemplateUtil.asElement(element).dispatchEvent(changeEvent);
  }
}
