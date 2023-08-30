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
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.ComponentAlreadyBoundException;
import org.jboss.errai.databinding.client.DeclarativeBindingModule;
import org.jboss.errai.databinding.client.DeclarativeBindingModuleUsingBinder;
import org.jboss.errai.databinding.client.DeclarativeBindingModuleUsingModel;
import org.jboss.errai.databinding.client.DeclarativeBindingModuleUsingParams;
import org.jboss.errai.databinding.client.DeclarativeBindingModuleWithKeyUpEvent;
import org.jboss.errai.databinding.client.HasBoundElemental2IsElement;
import org.jboss.errai.databinding.client.HasBoundIsElement;
import org.jboss.errai.databinding.client.InjectedDataBinderModuleBoundOnKeyUp;
import org.jboss.errai.databinding.client.ListOfStringWidget;
import org.jboss.errai.databinding.client.ModuleWithInjectedBindable;
import org.jboss.errai.databinding.client.ModuleWithInjectedDataBinder;
import org.jboss.errai.databinding.client.NonExistingPropertyException;
import org.jboss.errai.databinding.client.SimpleHTMLInputElementPresenter;
import org.jboss.errai.databinding.client.SimpleTextInputPresenter;
import org.jboss.errai.databinding.client.SingletonBindable;
import org.jboss.errai.databinding.client.TakesValueCheckInputPresenter;
import org.jboss.errai.databinding.client.TakesValueElemental2CheckInputPresenter;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.client.TestModelTakesValueWidget;
import org.jboss.errai.databinding.client.TestModelWidget;
import org.jboss.errai.databinding.client.TestModelWithList;
import org.jboss.errai.databinding.client.TestModelWithNestedConfiguredBindable;
import org.jboss.errai.databinding.client.TestModelWithoutBindableAnnotation;
import org.jboss.errai.databinding.client.ViewWithInput;
import org.jboss.errai.databinding.client.ViewWithOnKeyUpInput;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;
import org.jboss.errai.databinding.client.nonbindablepkg.TestModelInNonBindablePkg;
import org.jboss.errai.databinding.client.scan.TestModelBindable;
import org.jboss.errai.databinding.client.scan.TestModelWithoutBindableA;
import org.jboss.errai.databinding.client.scan.TestModelWithoutBindableB;
import org.jboss.errai.databinding.client.scan.TestModelWithoutBindableC;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.errai.common.client.util.EventTestingUtil.invokeEventListeners;
import static org.jboss.errai.common.client.util.EventTestingUtil.setupAddEventListenerInterceptor;

/**
 * Tests functionality provided by the {@link DataBinder} API.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class DataBindingIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.databinding.DataBindingTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    setupAddEventListenerInterceptor();
    super.gwtSetUp();
    final Logger logger = LoggerFactory.getLogger(getClass());
    GWT.setUncaughtExceptionHandler(e -> logger.error("Uncaught error.", e));
    Convert.deregisterDefaultConverters();
  }

  @Test
  public void testBasicBinding() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "value").getModel();

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testElementBinding() {
    final InputElement textInput = Document.get().createTextInputElement();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textInput, "value").getModel();

    textInput.setValue("UI change");
    invokeEventListeners(textInput, "change");
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Widget not properly updated", "model change", textInput.getValue());
  }

  @Test
  public void testBindingWithHasTextAndHasValue() {
    final IntegerBox integerBox = new IntegerBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(integerBox, "age").getModel();

    integerBox.setValue(5, true);
    assertEquals("Model not properly updated", (Integer) 5, model.getAge());

    model.setAge(3);
    assertEquals("Widget not properly updated", "3", integerBox.getText());

    integerBox.setValue(null, true);
    assertEquals("Model not properly updated", (Integer) null, model.getAge());

    model.setAge(null);
    assertEquals("Widget not properly updated", "", integerBox.getText());
  }

  @Test
  public void testBasicBindingWithInjection() {
    final ModuleWithInjectedDataBinder module =
        IOC.getBeanManager().lookupBean(ModuleWithInjectedDataBinder.class).getInstance();

    final TestModel model = module.getModel();
    final TextBox nameTextBox = module.getNameTextBox();

    model.setName("model change");
    assertEquals("Widget not properly updated", "model change", nameTextBox.getText());

    nameTextBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getName());
  }

  @Test
  public void testBasicBindingOfNonAnnotatedType() {
    final TextBox textBox = new TextBox();
    final TestModelWithoutBindableAnnotation model =
            DataBinder.forType(TestModelWithoutBindableAnnotation.class).bind(textBox, "value").getModel();

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testScanBindingOfNonAnnotatedTypes() {
    ////////////// A /////////////////
    {
      final TextBox textBoxA = new TextBox();

      final TestModelWithoutBindableA modelA =
              DataBinder.forType(TestModelWithoutBindableA.class).bind(textBoxA, "value").getModel();

      textBoxA.setValue("UI change", true);
      assertEquals("Model not properly updated", "UI change", modelA.getValue());

      modelA.setValue("model change");
      assertEquals("Widget not properly updated", "model change", textBoxA.getText());
    }

    ////////////// B /////////////////
    {
      final TextBox textBoxB = new TextBox();

      final TestModelWithoutBindableB modelB =
              DataBinder.forType(TestModelWithoutBindableB.class).bind(textBoxB, "value").getModel();

      textBoxB.setValue("UI change", true);
      assertEquals("Model not properly updated", "UI change", modelB.getValue());

      modelB.setValue("model change");
      assertEquals("Widget not properly updated", "model change", textBoxB.getText());
    }

    ////////////// C /////////////////
    {
      final TextBox textBoxC = new TextBox();

      final TestModelWithoutBindableC modelC =
              DataBinder.forType(TestModelWithoutBindableC.class).bind(textBoxC, "value").getModel();

      textBoxC.setValue("UI change", true);
      assertEquals("Model not properly updated", "UI change", modelC.getValue());

      modelC.setValue("model change");
      assertEquals("Widget not properly updated", "model change", textBoxC.getText());
    }

    ////////////// D /////////////////
    {
      final TextBox textBoxD = new TextBox();

      final TestModelBindable modelD =
              DataBinder.forType(TestModelBindable.class).bind(textBoxD, "value").getModel();

      textBoxD.setValue("UI change", true);
      assertEquals("Model not properly updated", "UI change", modelD.getValue());

      modelD.setValue("model change");
      assertEquals("Widget not properly updated", "model change", textBoxD.getText());
    }
  }

  @Test
  public void testNestedBindingOfNonAnnotatedType() {
    final TextBox textBox = new TextBox();
    final TestModelWithoutBindableAnnotation model =
        DataBinder.forType(TestModelWithoutBindableAnnotation.class).bind(textBox, "child.value").getModel();

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getValue());

    model.getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testNestedBindingWithModelChange() {
    final TextBox textBox = new TextBox();
    TestModel model = new TestModel();
    final DataBinder<TestModel> binder = DataBinder.forModel(model);
    model = binder.bind(textBox, "child.value", null, StateSync.FROM_MODEL).getModel();

    textBox.setValue("old string", true);
    assertEquals("Model not properly updated", "old string", model.getChild().getValue());

    final TestModel newChild = new TestModel();

    newChild.value = "new string";
    model.setChild(newChild);

    assertEquals("Widget not properly updated", "new string", textBox.getText());
  }

  @Test
  public void testBindingOfReadOnlyField() {
    final Label label = new Label();
    final TestModel model = DataBinder.forType(TestModel.class).bind(label, "id").getModel();

    model.setId(1701);
    assertEquals("Widget not properly updated", "1701", label.getText());
  }

  @Test
  public void testBindingWithDefaultConversion() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "age").getModel();

    model.setAge(25);
    assertEquals("Widget not properly updated", model.getAge().toString(), textBox.getText());

    textBox.setValue("52", true);
    assertEquals("Model not properly updated", Integer.valueOf(52), model.getAge());
  }

  @Test
  public void testBindingOfNonExistingPropertyThrowsException() {
    try {
      DataBinder.forType(TestModel.class).bind(new TextBox(), "non-existing");
      fail("Expected NonExistingPropertyException!");
    }
    catch (final NonExistingPropertyException ex) {
      assertTrue("Exception message does not contain correct property name: " + ex.getMessage(), ex.getMessage().contains("non-existing"));
      assertTrue("Exception message does not contain correct type name: " + ex.getMessage(), ex.getMessage().contains("TestModel"));
    }
  }

  @Test
  public void testBindingOfSingleWidgetToMultiplePropertiesThrowsException() {
    final TextBox textBox = new TextBox();
    try {
      DataBinder.forType(TestModel.class).bind(textBox, "value").bind(textBox, "name");
      fail("Binding a widget to multiple properties should fail with an exception!");
    }
    catch (final ComponentAlreadyBoundException e) {
      // expected
      assertTrue("Exception message does not contain property name", e.getMessage().contains("value"));
    }
  }

  @Test
  public void testBindingOfSinglePropertyToMultipleWidgets() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class)
        .bind(textBox1, "value")
        .bind(textBox2, "value").getModel();

    textBox1.setValue("UI change 1", true);
    assertEquals("Model not properly updated", "UI change 1", model.getValue());
    assertEquals("Widget not properly updated", "UI change 1", textBox2.getValue());

    textBox2.setValue("UI change 2", true);
    assertEquals("Model not properly updated", "UI change 2", model.getValue());
    assertEquals("Widget not properly updated", "UI change 2", textBox1.getValue());

    model.setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox1.getText());
    assertEquals("Widget not properly updated", "model change", textBox2.getText());
  }

  @Test
  public void testUnbindingSpecificProperty() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox textBox = new TextBox();
    final TestModel model = binder.bind(textBox, "value").getModel();

    binder.unbind("value");

    model.setValue("model change");
    assertEquals("Widget should not have been updated because unbind was called", "", textBox.getText());

    textBox.setValue("UI change", true);
    assertEquals("Model should not have been updated because unbind was called", "model change", model.getValue());
  }

  @Test
  public void testUnbindingAllProperties() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox textBox = new TextBox();
    final TestModel model = binder.bind(textBox, "value").getModel();

    binder.unbind();

    model.setValue("model change");
    assertEquals("Widget should not have been updated because unbind was called", "", textBox.getText());

    textBox.setValue("UI change", true);
    assertEquals("Model should not have been updated because unbind was called", "model change", model.getValue());
  }

  @Test
  public void testBindingOfMultipleProperties() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox valueTextBox = new TextBox();
    binder.bind(valueTextBox, "value");

    final TextBox nameTextBox = new TextBox();
    binder.bind(nameTextBox, "name");

    final TestModel model = binder.getModel();

    nameTextBox.setValue("ui.name", true);
    assertEquals("Name not properly updated", "ui.name", model.getName());
    assertNull("Value should not have been updated", model.getValue());

    model.setName("model.name");
    assertEquals("Widget for name not properly updated", "model.name", nameTextBox.getText());
    assertEquals("Widget for value should not have been updated", "", valueTextBox.getText());

    nameTextBox.setValue("ui.name", true);
    valueTextBox.setValue("ui.value", true);
    assertEquals("Name not properly updated", "ui.name", model.getName());
    assertEquals("Value not properly updated", "ui.value", model.getValue());

    model.setName("model.name");
    model.setValue("model.value");
    assertEquals("Widget for name not properly updated", "model.name", nameTextBox.getText());
    assertEquals("Widget for value not properly updated", "model.value", valueTextBox.getText());

    binder.unbind("name");
    nameTextBox.setValue("ui.name", true);
    valueTextBox.setValue("ui.value", true);
    assertEquals("Name should not have been updated", "model.name", model.getName());
    assertEquals("Value not properly updated", "ui.value", model.getValue());

    model.setName("model.name");
    model.setValue("model.value");
    assertEquals("Widget for name should not have been updated", "ui.name", nameTextBox.getText());
    assertEquals("Widget for value not properly updated", "model.value", valueTextBox.getText());
  }

  @Test
  public void testBindingWithModelInstanceChange() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox textBox = new TextBox();
    binder.bind(textBox, "name");

    TestModel model = new TestModel();
    model.setName("initial name");
    binder.setModel(model, StateSync.FROM_MODEL);
    assertEquals("Widget not updated after model change", "initial name", textBox.getText());

    model = new TestModel();
    textBox.setText("changed name");
    binder.setModel(model, StateSync.FROM_UI);
    assertEquals("Model not updated after model change", "changed name", model.getName());
  }

  @Test
  public void testBindingWithInitialStateSync() {
    final TextBox textBox = new TextBox();
    textBox.setValue("initial ui value");

    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox, "name", null, StateSync.FROM_UI);
    assertEquals("Model not initialized based on widget's state", "initial ui value", binder.getModel().getName());

    final TestModel model = new TestModel();
    model.setName("initial model value");
    DataBinder.forModel(model).bind(textBox, "name", null, StateSync.FROM_MODEL);
    assertEquals("Model not initialized based on widget's state", "initial model value", textBox.getValue());
  }

  @Test
  public void testBindingToCustomHasValueType() {
    final TestModelWidget widget = new TestModelWidget();

    final TestModel childModel = new TestModel();
    childModel.setName("child");

    final TestModel model = new TestModel();
    model.setChild(childModel);

    final DataBinder<TestModel> binder = DataBinder.forModel(model).bind(widget, "child", null, StateSync.FROM_MODEL);
    assertEquals("Widget not updated based on model's state", childModel, binder.getModel().getChild());
  }

  @Test
  public void testBindingToCustomTakesValueType() {
    final TestModelTakesValueWidget widget = new TestModelTakesValueWidget();

    final TestModel childModel = new TestModel();
    childModel.setName("child");

    final TestModel model = new TestModel();
    model.setChild(childModel);

    final DataBinder<TestModel> binder = DataBinder.forModel(model).bind(widget, "child", null, StateSync.FROM_MODEL);
    assertEquals("Widget not updated based on model's state", childModel, binder.getModel().getChild());
  }

  @Test
  public void testBindablePropertyChain() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.value").getModel();

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getValue());

    model.getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainTwoLevelsDeep() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.child.name").getModel();

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getChild().getName());

    model.getChild().getChild().setName("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainWithRootInstanceChange() {
    final TextBox textBox = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox, "child.child.value");

    TestModel model = new TestModel();
    final TestModel childModel = new TestModel();
    final TestModel grandChildModel = new TestModel("value1");
    childModel.setChild(grandChildModel);
    model.setChild(childModel);
    binder.setModel(model);
    assertEquals("Widget not properly updated", "value1", textBox.getText());

    final TestModel newGrandChildModel = new TestModel("value2");
    childModel.setChild(newGrandChildModel);
    model = binder.setModel(model);
    assertEquals("Widget not properly updated", "value2", textBox.getText());

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getChild().getValue());

    model.getChild().getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainWithNestedInstanceChange() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.value").getModel();

    model.setChild(new TestModel("value"));
    assertEquals("Widget not properly updated", "value", textBox.getText());

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getValue());

    model.setChild(new TestModel("newValue"));
    assertEquals("Widget not properly updated", "newValue", textBox.getText());

    model.getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainWithNestedConfiguredInstanceChange() {
    final TextBox textBox = new TextBox();
    final TestModelWithNestedConfiguredBindable model = DataBinder.forType(TestModelWithNestedConfiguredBindable.class)
            .bind(textBox, "nested.value").getModel();

    model.setNested(new TestModelWithoutBindableAnnotation("value"));
    assertEquals("Widget not properly updated", "value", textBox.getText());

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getNested().getValue());

    model.setNested(new TestModelWithoutBindableAnnotation("newValue"));
    assertEquals("Widget not properly updated", "newValue", textBox.getText());

    model.getNested().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainWithNestedInstanceChangeInNonAccessorMethod() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.child.value").getModel();

    // changing the nested bindable using a non accessor method
    model.resetChildren();
    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getChild().getValue());

    // changing the nested bindable using a non accessor method
    model.resetChildren();
    model.getChild().getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainWithInitialStateSync() {
    final TextBox textBox = new TextBox();
    textBox.setValue("initial ui value");

    DataBinder<TestModel> binder =
        DataBinder.forType(TestModel.class).bind(textBox, "child.name", null, StateSync.FROM_UI);
    assertEquals("Model not initialized based on widget's state", "initial ui value",
        binder.getModel().getChild().getName());

    TestModel childModel = new TestModel();
    childModel.setName("initial model value");
    TestModel model = new TestModel();
    model.setChild(childModel);

    binder = DataBinder.forModel(model).bind(textBox, "child.name", null, StateSync.FROM_MODEL);
    assertEquals("Model not initialized based on widget's state", "initial model value", textBox.getValue());

    childModel = new TestModel();
    childModel.setName("updated model value");
    model = new TestModel();
    model.setChild(childModel);

    binder.setModel(model, StateSync.FROM_MODEL);
    assertEquals("Model not initialized based on widget's state", "updated model value", textBox.getValue());
  }

  @Test
  public void testBindablePropertyChainWithUnbinding() {
    final TextBox valueTextBox = new TextBox();
    final TextBox nameTextBox = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class)
        .bind(valueTextBox, "child.value")
        .bind(nameTextBox, "child.name");

    final TestModel model = binder.getModel();

    // unbind specific nested property
    binder.unbind("child.name");
    assertEquals("Only one bound property should be left", 1, binder.getBoundProperties().size());
    model.getChild().setName("model change");
    assertEquals("Widget should not have been updated because unbind was called for this property", "",
        nameTextBox.getText());

    nameTextBox.setValue("UI change", true);
    assertEquals("Model should not have been updated because unbind was called for this property", "model change",
        model.getChild().getName());

    model.getChild().setValue("model change");
    assertEquals("Widget not properly updated", "model change", valueTextBox.getText());

    valueTextBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getChild().getValue());

    model.getChild().setValue("");

    // unbind all properties
    binder.unbind();
    assertEquals("No property should be bound", 0, binder.getBoundProperties().size());

    model.getChild().setValue("model change");
    assertEquals("Widget should not have been updated because unbind was called", "", valueTextBox.getText());

    valueTextBox.setValue("UI change", true);
    assertEquals("Model should not have been updated because unbind was called", "model change",
        model.getChild().getValue());
  }

  @Test
  public void testBindingUsingNonAccessorMethod() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "active").getModel();

    // change the property 'active' using a non accessor method.
    model.activate();
    assertTrue("Model not properly updated", model.isActive());
    assertEquals("Widget not properly updated", "true", textBox.getText());
  }

  @Test
  public void testBindingUsingNonAccessorMethodCalledSet() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "active").getModel();

    // change the property 'active' using a non accessor method.
    model.setActivateStatus(true);
    assertTrue("Model not properly updated", model.isActive());
    assertTrue("Model not properly updated", model.getActivateStatus("ignore"));
    assertEquals("Widget not properly updated", "true", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainsUsingNonAccessorMethod() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.active").getModel();

    // change the property 'active' on the model and all children using a non accessor method.
    model.activate();
    assertTrue("Model not properly updated", model.getChild().isActive());
    assertEquals("Widget not properly updated", "true", textBox.getText());
  }

  @Test
  public void testBindablePropertyChainsUsingNonAccesssorMethodOnChild() {
    final TextBox textBox = new TextBox();
    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.active").getModel();

    // change the property 'active' using a non accessor method.
    model.getChild().activate();
    assertTrue("Model not properly updated", model.getChild().isActive());
    assertEquals("Widget not properly updated", "true", textBox.getText());
  }

  @Test
  public void testBindableProxyToString() {
    final TestModel model = new TestModel();
    model.setName("test");

    final DataBinder<TestModel> binder = DataBinder.forModel(model);
    assertEquals(model.toString(), binder.getModel().toString());
  }

  @Test
  public void testGetWidgets() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox1, "value").bind(textBox2, "value");

    assertEquals("Bound widget not found", textBox1, binder.getComponents("value").get(0));
    assertEquals("Bound widget not found", textBox2, binder.getComponents("value").get(1));
    assertEquals("Should have exactly 2 bound widgets", 2, binder.getComponents("value").size());
  }

  @Test
  public void testGetBoundProperties() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class)
        .bind(new TextBox(), "value")
        .bind(new TextBox(), "child.child.value");

    final Set<String> boundProperties = binder.getBoundProperties();
    assertNotNull("Bound properties set should not be null", boundProperties);
    assertEquals("There should be exactly two bound properties", 2, boundProperties.size());
    assertTrue("value should be a bound property", boundProperties.contains("value"));
    assertTrue("child.child.value should be a bound property", boundProperties.contains("child.child.value"));
  }

  @Test
  public void testUpdateWidgets() {
    final List<String> changedProperties = new ArrayList<>();

    final TestModel model = new TestModel();
    final TextBox textBox = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forModel(model).bind(textBox, "value");

    binder.addPropertyChangeHandler(new PropertyChangeHandler<Long>() {
      @Override
      public void onPropertyChange(final PropertyChangeEvent<Long> event) {
        changedProperties.add(event.getPropertyName());
      }
    });

    // using direct field access on the target object so the bindable proxy has no chance of seeing
    // the change
    model.value = "model change";
    assertEquals("TextBox should be empty", "", textBox.getText());

    // This call is used by Errai JPA, to update the widgets after an entity was changed
    // using direct field access (e.g. the id was set).
    ((BindableProxy<?>) binder.getModel()).updateWidgets();
    assertEquals("TextBox should have been updated", "model change", textBox.getText());
    assertEquals("Unexpected property change events received", Arrays.asList("value"), changedProperties);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testDeepUnwrap() {
    final TestModel parent = new TestModel("v0");
    parent.setName("parent");

    final TestModel child = new TestModel("v1");
    child.setName("child");

    final TestModel grandChild = new TestModel("v2");
    grandChild.setName("grandChild");

    child.setChild(grandChild);
    parent.setChild(child);

    final DataBinder<TestModel> binder = DataBinder.forModel(parent)
      .bind(new TextBox(), "child.value")
      .bind(new TextBox(), "child.child.value");

    final TestModel unwrapped = ((BindableProxy<TestModel>) binder.getModel()).deepUnwrap();
    assertNotNull(unwrapped);
    assertNotNull(unwrapped.getChild());
    assertNotNull(unwrapped.getChild().getChild());

    assertNotSame(unwrapped, parent);
    assertNotSame(unwrapped.getChild(), parent.getChild());
    assertNotSame(unwrapped.getChild().getChild(), parent.getChild().getChild());

    assertFalse(unwrapped instanceof BindableProxy);
    assertFalse(unwrapped.getChild() instanceof BindableProxy);
    assertFalse(unwrapped.getChild().getChild() instanceof BindableProxy);

    assertEquals(unwrapped, parent);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testDeepUnwrapClonesUnproxiedProperties() {
    final TestModel parent = new TestModel("v0");
    parent.setName("parent");

    final TestModel child = new TestModel("v1");
    child.setName("child");

    final TestModel grandChild = new TestModel("v2");
    grandChild.setName("grandChild");

    child.setChild(grandChild);
    parent.setChild(child);

    final DataBinder<TestModel> binder = DataBinder.forModel(parent);

    final TestModel unwrapped = ((BindableProxy<TestModel>) binder.getModel()).deepUnwrap();
    assertNotNull(unwrapped);
    assertNotNull(unwrapped.getChild());
    assertNotNull(unwrapped.getChild().getChild());

    assertNotSame(unwrapped, parent);
    assertNotSame(unwrapped.getChild(), parent.getChild());
    assertNotSame(unwrapped.getChild().getChild(), parent.getChild().getChild());

    assertFalse(unwrapped instanceof BindableProxy);
    assertFalse(unwrapped.getChild() instanceof BindableProxy);
    assertFalse(unwrapped.getChild().getChild() instanceof BindableProxy);

    assertEquals(unwrapped, parent);
  }

  @Test
  public void testUpdateWidgetsWithBindablePropertyChain() {
    final TestModel grandChildModel = new TestModel();
    final TestModel childModel = new TestModel();
    childModel.setChild(grandChildModel);
    final TestModel model = new TestModel();
    model.setChild(childModel);

    final TextBox textBox = new TextBox();
    final DataBinder<TestModel> binder = DataBinder.forModel(model).bind(textBox, "child.child.value");

    // using direct field access on the target object so the bindable proxy has no chance of seeing
    // the change
    model.child = new TestModel();
    model.child.child = new TestModel();
    model.child.child.value = "model change";
    assertEquals("TextBox should be empty", "", textBox.getText());

    // This call is used by Errai JPA, to update the widgets after an entity was changed
    // using direct field access (e.g. the id was set).
    ((BindableProxy<?>) binder.getModel()).updateWidgets();
    assertEquals("TextBox should have been updated", "model change", textBox.getText());
  }

  @Test
  public void testDeclarativeBindingUsingBinder() {
    final DeclarativeBindingModule module =
        IOC.getBeanManager().lookupBean(DeclarativeBindingModuleUsingBinder.class).getInstance();
    testDeclarativeBinding(module);
  }

  @Test
  public void testDeclarativeBindingUsingModel() {
    final DeclarativeBindingModule module =
        IOC.getBeanManager().lookupBean(DeclarativeBindingModuleUsingModel.class).getInstance();
    testDeclarativeBinding(module);
  }

  @Test
  public void testDeclarativeBindingUsingModelSetter() {
    final DeclarativeBindingModuleUsingModel module =
         IOC.getBeanManager().lookupBean(DeclarativeBindingModuleUsingModel.class).getInstance();
    final TestModel model = new TestModel();
    model.setId(123);
    model.setName("custom name");
    module.setModel(model);

    // ensure the model is proxied, caused by @ModelSetter
    assertTrue(module.getModel() instanceof BindableProxy);
    assertTrue(module.getModel().getName().equals(model.getName()));
    assertEquals("123", module.getLabel().getText());

    module.getLabel().setText("");
    module.getDateTextBox().setText("");
    testDeclarativeBinding(module);
  }

  @Test
  public void testDeclarativeBindingUsingParams() {
    final DeclarativeBindingModule module =
        IOC.getBeanManager().lookupBean(DeclarativeBindingModuleUsingParams.class).getInstance();
    testDeclarativeBinding(module);
  }

  public void testDeclarativeBinding(final DeclarativeBindingModule module) {
    final Label idLabel = module.getLabel();
    assertNotNull(idLabel);
    assertEquals("", idLabel.getText());

    final TextBox nameTextBox = module.getNameTextBox();
    assertNotNull(nameTextBox);
    assertEquals("", nameTextBox.getValue());

    final TextBox dateTextBox = module.getDateTextBox();
    assertNotNull(dateTextBox);
    assertEquals("", dateTextBox.getValue());

    final TextBox age = module.getAge();
    assertNotNull(age);
    assertEquals("", age.getValue());

    final TestModel model = module.getModel();
    model.setId(1711);
    model.getChild().setName("errai");
    model.setLastChanged(new Date());
    model.setAge(47);
    assertEquals("Label (id) was not updated!", Integer.valueOf(model.getId()).toString(), idLabel.getText());
    assertEquals("TextBox (name) was not updated!", model.getChild().getName(), nameTextBox.getValue());
    assertEquals("TextBox (date) was not updated using custom converter!", "testdate", dateTextBox.getValue());
    assertEquals("TextBox (age) was not updated", model.getAge().toString(), age.getValue());

    nameTextBox.setValue("updated", true);
    dateTextBox.setValue("updated", true);
    age.setValue("0", true);

    assertEquals("Model (name) was not updated!", nameTextBox.getValue(), model.getChild().getName());
    assertEquals("Model (lastUpdate) was not updated using custom converter!",
        DeclarativeBindingModuleUsingBinder.TEST_DATE, model.getLastChanged());
    assertEquals("Model (phoneNumber) was not updated!", age.getValue(), model.getAge().toString());
  }

  @Test
  public void testInjectedBindableTypeIsNotBound() {
    final ModuleWithInjectedBindable module =
        IOC.getBeanManager().lookupBean(ModuleWithInjectedBindable.class).getInstance();

    // injecting a bindable type without @Model qualification shouldn't result in a bindable proxy
    assertEquals(SingletonBindable.class, Factory.maybeUnwrapProxy(module.getUnboundModel()).getClass());
    // verify that the injection scope is correct
    assertSame(Factory.maybeUnwrapProxy(module.getUnboundModel2()), Factory.maybeUnwrapProxy(module.getUnboundModel()));
  }

  @Test
  public void testBindingWithSharedModel() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(model).bind(textBox2, "value");
    // Ensure we got the same proxy instance for our model instance
    assertSame(binder1.getModel(), binder2.getModel());

    textBox1.setValue("UI change 1", true);
    assertEquals("Model not properly updated", "UI change 1", model.getValue());
    assertEquals("Widget not properly updated", "UI change 1", textBox2.getText());

    textBox2.setValue("UI change 2", true);
    assertEquals("Model not properly updated", "UI change 2", model.getValue());
    assertEquals("Widget not properly updated", "UI change 2", textBox1.getText());

    binder1.getModel().setValue("model change 1");
    assertEquals("Widget not properly updated", "model change 1", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 1", textBox2.getText());

    binder2.getModel().setValue("model change 2");
    assertEquals("Widget not properly updated", "model change 2", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 2", textBox2.getText());
  }

  @Test
  public void testBindingWithSharedModelProxy() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(binder1.getModel()).bind(textBox2, "value");
    // Ensure we got the same proxy instance for our model instance
    assertSame(binder1.getModel(), binder2.getModel());

    textBox1.setValue("UI change 1", true);
    assertEquals("Model not properly updated", "UI change 1", model.getValue());
    assertEquals("Widget not properly updated", "UI change 1", textBox2.getText());

    textBox2.setValue("UI change 2", true);
    assertEquals("Model not properly updated", "UI change 2", model.getValue());
    assertEquals("Widget not properly updated", "UI change 2", textBox1.getText());

    binder1.getModel().setValue("model change 1");
    assertEquals("Widget not properly updated", "model change 1", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 1", textBox2.getText());

    binder2.getModel().setValue("model change 2");
    assertEquals("Widget not properly updated", "model change 2", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 2", textBox2.getText());
  }

  @Test
  public void testUnbindingWithSharedModel() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(model).bind(textBox2, "value");
    assertSame(binder1.getModel(), binder2.getModel());
    binder1.unbind();

    textBox2.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.getValue());
    assertEquals("Widget should not have been updated", "", textBox1.getText());

    binder2.getModel().setValue("model change 1");
    assertEquals("Widget should not have been updated", "", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 1", textBox2.getText());

    binder1.getModel().setValue("model change 2");
    assertEquals("Widget should not have been updated", "", textBox1.getText());
    assertEquals("Widget not properly updated", "model change 2", textBox2.getText());
  }

  @Test
  public void testSetModelWithSharedProxies() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(model).bind(textBox2, "name");
    final TestModel modelProxy1 = binder1.getModel();
    final TestModel modelProxy2 = binder2.getModel();
    assertSame(modelProxy1, modelProxy2);

    assertEquals("", textBox1.getText());
    assertEquals("", textBox2.getText());

    TestModel model2 = new TestModel();
    model2.setValue("value");

    binder1.setModel(model2);
    assertEquals("value", textBox1.getText());
    assertTrue(binder1.getBoundProperties().contains("value"));

    model2 = new TestModel();
    model2.setName("name");

    binder2.setModel(model2);
    assertEquals("name", textBox2.getText());
    assertTrue(binder2.getBoundProperties().contains("name"));
  }

  @Test
  public void testSetModelWithSharedProxiesAndPropertyChain() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "child.value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(model).bind(textBox2, "child.value");
    final TestModel modelProxy1 = binder1.getModel();
    final TestModel modelProxy2 = binder2.getModel();
    assertSame(modelProxy1, modelProxy2);

    assertEquals("", textBox1.getText());
    assertEquals("", textBox2.getText());

    final TestModel model2 = new TestModel();
    model2.setChild(new TestModel("value1"));
    binder1.setModel(model2);
    assertEquals("value1", textBox1.getText());
    assertTrue(binder1.getBoundProperties().contains("child.value"));

    model2.setChild(new TestModel("value2"));
    binder2.setModel(model2);
    assertEquals("value2", textBox2.getText());
    assertTrue(binder2.getBoundProperties().contains("child.value"));

    binder2.setModel(binder1.getModel());
    textBox2.setValue("value3", true);
    assertEquals("value3", binder2.getModel().getChild().getValue());
    assertEquals("value3", textBox1.getText());
    assertEquals("value3", binder1.getModel().getChild().getValue());
  }

  @Test
  public void testSharedProxyCleanup() {
    final TextBox textBox1 = new TextBox();
    final TextBox textBox2 = new TextBox();
    final TestModel model = new TestModel();

    final DataBinder<TestModel> binder1 = DataBinder.forModel(model).bind(textBox1, "value");
    final DataBinder<TestModel> binder2 = DataBinder.forModel(model).bind(textBox2, "value");
    final TestModel modelProxy1 = binder1.getModel();
    final TestModel modelProxy2 = binder2.getModel();
    assertSame(modelProxy1, modelProxy2);

    // Unbinding all binders for a given proxy should clear it from the cache we keep to ensure that
    // we always use the same proxy for the same model instance
    binder1.unbind();
    TestModel modelProxy3 = DataBinder.forModel(model).getModel();
    assertSame(modelProxy1, modelProxy3);
    assertSame(modelProxy2, modelProxy3);

    binder2.unbind();
    modelProxy3 = DataBinder.forModel(model).getModel();
    assertNotSame(modelProxy1, modelProxy3);
    assertNotSame(modelProxy2, modelProxy3);
  }

  @Test
  public void testListChangesTriggerWidgetUpdates() {
    final ListOfStringWidget widget = new ListOfStringWidget();
    final TestModelWithList model = DataBinder.forType(TestModelWithList.class).bind(widget, "list").getModel();

    model.getList().add("1");
    assertEquals("Widget not properly updated", 1, widget.getValue().size());
    assertTrue("Widget not properly updated", widget.getValue().contains("1"));

    model.getList().remove("1");
    assertEquals("Widget not properly updated", 0, widget.getValue().size());
  }

  @Test
  public void testDeclarativeBindingWithKeyUpBindingEvent() {
    final DeclarativeBindingModule module =
        IOC.getBeanManager().lookupBean(DeclarativeBindingModuleWithKeyUpEvent.class).getInstance();

    final TestModel model = module.getModel();
    model.setAge(1);
    assertEquals("Widget not properly updated", model.getAge(), new Integer(module.getAge().getValue()));

    module.getAge().setValue("6");
    // fire key event manually
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false,
                                                              KeyCodes.KEY_NUM_SIX), module.getAge());
    assertEquals("Model not properly updated", module.getAge().getValue(), model.getAge().toString());
  }

  @Test
  public void testDeclarativeBindingWithKeyUpEventAndChainedProperty () {
    final DeclarativeBindingModule module =
      IOC.getBeanManager().lookupBean(DeclarativeBindingModuleWithKeyUpEvent.class).getInstance();

    final TestModel model = module.getModel();
    model.getChild().setName("model change");
    assertEquals("Widget not properly updated", model.getChild().getName(), module.getNameTextBox().getValue());

    module.getNameTextBox().setValue("widget name");
    // fire key event manually
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_E), module
                                                                                                            .getNameTextBox());
    assertEquals("Model and widget do not match", module.getNameTextBox().getValue(), model.getChild().getName());
  }

  @Test
  public void testInjectedDataBinderWithKeyUpEvent () {
     final InjectedDataBinderModuleBoundOnKeyUp module =
        IOC.getBeanManager().lookupBean(InjectedDataBinderModuleBoundOnKeyUp.class).getInstance();

    final TestModel model = module.getModel();
    final TextBox nameTextBox = module.getNameTextBox();

    model.setName("model change");
    assertEquals("Widget not properly updated", "model change", nameTextBox.getText());

    nameTextBox.setValue("UI change", true);
    // fire key event manually
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_E),
                              nameTextBox);

    assertEquals("Model not properly updated", "UI change", model.getName());
  }

  @Test
  public void testUnbindingWithKeyUpEvent() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox textBox = new TextBox();
    final TestModel model = binder.bind(textBox, "value", null, StateSync.FROM_MODEL, true).getModel();

    textBox.setValue("UI change");
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_E), textBox);
    assertEquals("Model not properly updated", textBox.getValue(), model.getValue());

    binder.unbind("value");

    model.setValue("model change");
    // textBox value should be same as before
    assertEquals("Widget should not have been updated because unbind was called", "UI change", textBox.getText());

    textBox.setValue("Another UI change", true);
    // model value should be same as before
    assertEquals("Model should not have been updated because unbind was called", "model change", model.getValue());

  }

  @Test
  public void testSetModelWithKeyUpEvent() throws Exception {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class);
    final TextBox textBox = new TextBox();
    final TestModel model = binder.bind(textBox, "value", null, StateSync.FROM_MODEL, true).getModel();

    textBox.setValue("UI change");
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_E), textBox);
    assertEquals("Model not properly updated", textBox.getValue(), model.getValue());

    binder.setModel(new TestModel());

    final String newValue = "new value";
    textBox.setValue(newValue, false);
    assertFalse("Model should not have updated, no event should have been fired.", newValue.equals(binder.getModel().getValue()));

    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_A), textBox);
    assertEquals("Model should have updated after key up event fired.", newValue, binder.getModel().getValue());
  }

  @Test
  public void testBindingNonTextWidgetOnUnhandledEvent() {
    final CheckBox checkBox = new CheckBox();
    try {
      // bind non-ValueBoxBase widget on KeyUpEvents
      DataBinder.forType(TestModel.class).bind(checkBox, "active", null, StateSync.FROM_MODEL, true);
      fail("Widgets that do not extend ValueBoxBase should not bind on KeyUpEvents.");
    } catch (final Exception e) {
      // this is the expected behavior
    }
  }

  @Test
  public void testKeyUpWithMultipleWidgetsBoundToChainedProperty() {
    final TextBox textBox = new TextBox();
    final Label label = new Label();

    final TestModel model = DataBinder.forType(TestModel.class).bind(textBox, "child.name", null, StateSync.FROM_MODEL, true)
                        .bind(label, "child.name").getModel();

    textBox.setValue("new value");
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_E), textBox);

    assertEquals("Model not updated", textBox.getValue(), model.getChild().getName());
    assertEquals("Second widget not updated", textBox.getValue(), label.getText());
  }

  @Test
  public void testBidirectionalChainedKeyUpEventBinding() {
    final TextBox tb1 = new TextBox();
    final TextBox tb2 = new TextBox();

    DataBinder.forType(TestModel.class).bind(tb1, "child.name", null, StateSync.FROM_MODEL, true)
      .bind(tb2, "child.name", null, StateSync.FROM_MODEL, true);

    tb1.setValue("change in tb1");
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_NUM_ONE), tb1);

    assertEquals("Second widget not updated", tb1.getValue(), tb2.getValue());

    tb2.setValue("change in tb2");
    DomEvent.fireNativeEvent(Document.get().createKeyUpEvent(false, false, false, false, KeyCodes.KEY_NUM_TWO), tb2);

    assertEquals("First widget not updated", tb2.getValue(), tb1.getValue());
  }

  @Test
  public void testPauseResumeFromUI() {
    final TextBox parentTextBox = new TextBox();
    final TextBox childTextBox = new TextBox();

    final DataBinder<TestModel> binder = DataBinder
                                          .forType(TestModel.class)
                                          .bind(parentTextBox, "value")
                                          .bind(childTextBox, "child.value");

    final TestModel model = binder.getModel();

    binder.pause();
    assertSame("Pause should not change model instance", model, binder.getModel());

    parentTextBox.setValue("UI change paused", true);
    childTextBox.setValue("UI change paused", true);
    assertEquals("Parent model should not have been updated while paused", null, model.getValue());
    assertEquals("Child model should not have been updated while paused", null, model.getChild().getValue());
    model.setValue("model change while paused");
    model.getChild().setValue("model change while paused");
    assertEquals("Parent bound widget should not have been updated while paused", "UI change paused", parentTextBox.getText());
    assertEquals("Child bound widget should not have been updated while paused", "UI change paused", childTextBox.getText());

    binder.resume(StateSync.FROM_UI);
    assertEquals("Parent model not properly updated", "UI change paused", model.getValue());
    assertEquals("Child model not properly updated", "UI change paused", model.getChild().getValue());
    assertEquals("Parent bound widget should not have been updated", "UI change paused", parentTextBox.getText());
    assertEquals("Child bound widget should not have been updated", "UI change paused", childTextBox.getText());

    parentTextBox.setValue("UI change resumed", true);
    childTextBox.setValue("UI change resumed", true);
    assertEquals("Parent model not properly updated", "UI change resumed", model.getValue());
    assertEquals("Child model not properly updated", "UI change resumed", model.getChild().getValue());
    model.setValue("model change resumed");
    model.getChild().setValue("model change resumed");
    assertEquals("Parent bound widget not properly updated", "model change resumed", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model change resumed", childTextBox.getText());
  }

  @Test
  public void testPauseResumeFromModel() {
    final TextBox parentTextBox = new TextBox();
    final TextBox childTextBox = new TextBox();

    final DataBinder<TestModel> binder = DataBinder
                                          .forType(TestModel.class)
                                          .bind(parentTextBox, "value")
                                          .bind(childTextBox, "child.value");
    final TestModel model = binder.getModel();

    binder.pause();
    assertSame("Pause should not change model instance", model, binder.getModel());

    parentTextBox.setValue("UI change paused", true);
    childTextBox.setValue("UI change paused", true);
    assertEquals("Parent model should not have been updated", null, model.getValue());
    assertEquals("Child model should not have been updated", null, model.getChild().getValue());
    model.setValue("model change paused");
    model.getChild().setValue("model change paused");
    assertEquals("Parent bound widget should not have been updated", "UI change paused", parentTextBox.getText());
    assertEquals("Child bound widget should not have been updated", "UI change paused", childTextBox.getText());

    binder.resume(StateSync.FROM_MODEL);
    assertEquals("Parent bound widget not properly updated", "model change paused", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model change paused", childTextBox.getText());
    assertEquals("Parent model should not have been updated", "model change paused", model.getValue());
    assertEquals("Child model should not have been updated", "model change paused", model.getChild().getValue());

    parentTextBox.setValue("UI change resumed", true);
    childTextBox.setValue("UI change resumed", true);
    assertEquals("Parent model not properly updated", "UI change resumed", model.getValue());
    assertEquals("Child model not properly updated", "UI change resumed", model.getChild().getValue());
    model.setValue("model change resumed");
    model.getChild().setValue("model change resumed");
    assertEquals("Parent bound widget not properly updated", "model change resumed", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model change resumed", childTextBox.getText());
  }

  @Test
  public void testPauseResumeWithSetModel() {
    final TextBox parentTextBox = new TextBox();
    final TextBox childTextBox = new TextBox();

    final DataBinder<TestModel> binder = DataBinder
                                          .forType(TestModel.class)
                                          .bind(parentTextBox, "value")
                                          .bind(childTextBox, "child.value");
    final TestModel model = binder.getModel();

    binder.pause();
    assertSame("Pause should not change model instance", model, binder.getModel());

    parentTextBox.setValue("UI change paused", true);
    childTextBox.setValue("UI change paused", true);
    assertEquals("Parent model should not have been updated", null, model.getValue());
    assertEquals("Child model should not have been updated", null, model.getChild().getValue());
    model.setValue("model change paused");
    model.getChild().setValue("model change paused");
    assertEquals("Parent bound widget should not have been updated", "UI change paused", parentTextBox.getText());
    assertEquals("Child bound widget should not have been updated", "UI change paused", childTextBox.getText());

    final TestModel unproxiedModel = new TestModel("model update");
    unproxiedModel.setChild(new TestModel("model update"));
    // Resume using setModel
    final TestModel model2 = binder.setModel(unproxiedModel);
    assertEquals("Parent bound widget not properly updated", "model update", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model update", childTextBox.getText());
    assertEquals("Parent model should not have been updated", "model update", model2.getValue());
    assertEquals("Child model should not have been updated", "model update", model2.getChild().getValue());

    parentTextBox.setValue("UI change resumed", true);
    childTextBox.setValue("UI change resumed", true);
    assertEquals("Parent model not properly updated", "UI change resumed", model2.getValue());
    assertEquals("Child model not properly updated", "UI change resumed", model2.getChild().getValue());
    model2.setValue("model change resumed");
    model2.getChild().setValue("model change resumed");
    assertEquals("Parent bound widget not properly updated", "model change resumed", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model change resumed", childTextBox.getText());

    // Explicit resume should have no effect after resuming with setModel
    binder.resume(StateSync.FROM_MODEL);

    parentTextBox.setValue("UI change resumed", true);
    childTextBox.setValue("UI change resumed", true);
    assertEquals("Parent model not properly updated", "UI change resumed", model2.getValue());
    assertEquals("Child model not properly updated", "UI change resumed", model2.getChild().getValue());
    model2.setValue("model change resumed");
    model2.getChild().setValue("model change resumed");
    assertEquals("Parent bound widget not properly updated", "model change resumed", parentTextBox.getText());
    assertEquals("Child bound widget not properly updated", "model change resumed", childTextBox.getText());
  }

  @Test
  public void testResumeWithoutPauseDoesntThrowException() {
    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(new TextBox(), "value");
    binder.resume(StateSync.FROM_MODEL);
  }

  @Test
  public void testPausingMultipeTimes() {
    final TextBox textBox = new TextBox();

    final DataBinder<TestModel> binder = DataBinder.forType(TestModel.class).bind(textBox, "value");
    final TestModel model = binder.getModel();

    binder.pause();
    binder.pause();
    assertSame("Pause should not change model instance", model, binder.getModel());
  }

  @Test
  public void testBindingUsingFqcn() {
    final TextBox textBox = new TextBox();
    final String fqcn = TestModel.class.getName();

    final BindableProxy<?> model = BindableProxyFactory.getBindableProxy(fqcn);
    DataBinder.forModel(model).bind(textBox, "value");

    textBox.setValue("UI change", true);
    assertEquals("Model not properly updated", "UI change", model.get("value"));

    model.set("value", "model change");
    model.updateWidgets();
    assertEquals("Widget not properly updated", "model change", textBox.getText());
  }

  @Test
  public void testBindingToIsElement() throws Exception {
    final SimpleTextInputPresenter presenter = new SimpleTextInputPresenter();
    final TestModel model = DataBinder.forType(TestModel.class).bind(presenter, "value").getModel();

    presenter.setValue("UI change");
    invokeEventListeners(presenter.getElement(), "change");
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Component not properly updated", "model change", presenter.getValue());
  }

  @Test
  public void testBindingToIsElementWithTakesValue() throws Exception {
    final TakesValueCheckInputPresenter presenter = new TakesValueCheckInputPresenter();
    final TestModel model = DataBinder.forType(TestModel.class).bind(presenter, "active", Convert.identityConverter(Boolean.class)).getModel();

    assertFalse("Expected model.isActive() to start as false.", model.isActive());

    presenter.setValue(true);
    invokeEventListeners(presenter.getElement(), "change");
    assertTrue("Model not properly updated", model.isActive());

    model.setActive(false);
    assertFalse("Component not properly updated", presenter.getValue());
  }

  @Test
  public void testDeclarativeBindingToIsElement() throws Exception {
    final HasBoundIsElement instance = IOC.getBeanManager().lookupBean(HasBoundIsElement.class).getInstance();
    final SimpleTextInputPresenter presenter = instance.getTextPresenter();
    final TestModel model = instance.getBinder().getModel();

    presenter.setValue("UI change");
    invokeEventListeners(presenter.getElement(), "change");
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Component not properly updated", "model change", presenter.getValue());
  }

  @Test
  public void testDeclarativeBindingToIsElementWithTakesValue() throws Exception {
    final HasBoundIsElement instance = IOC.getBeanManager().lookupBean(HasBoundIsElement.class).getInstance();
    final TakesValueCheckInputPresenter presenter = instance.getCheckPresenter();
    final TestModel model = instance.getBinder().getModel();

    assertFalse("Expected model.isActive() to start as false.", model.isActive());

    presenter.setValue(true);
    invokeEventListeners(presenter.getElement(), "change");
    assertTrue("Model not properly updated", model.isActive());

    model.setActive(false);
    assertFalse("Component not properly updated", presenter.getValue());
  }

  @Test
  public void testBindingToElemental2IsElement() throws Exception {
    final SimpleHTMLInputElementPresenter presenter = new SimpleHTMLInputElementPresenter();
    final TestModel model = DataBinder.forType(TestModel.class).bind(presenter, "value").getModel();

    presenter.setValue("UI change");
    invokeEventListeners(presenter.getElement(), "change");
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Component not properly updated", "model change", presenter.getValue());
  }

  @Test
  public void testBindingToElemental2IsElementWithTakesValue() throws Exception {
    final TakesValueElemental2CheckInputPresenter presenter = new TakesValueElemental2CheckInputPresenter();
    final TestModel model = DataBinder.forType(TestModel.class).bind(presenter, "active", Convert.identityConverter(Boolean.class)).getModel();

    assertFalse("Expected model.isActive() to start as false.", model.isActive());

    presenter.setValue(true);
    invokeEventListeners(presenter.getElement(), "change");
    assertTrue("Model not properly updated", model.isActive());

    model.setActive(false);
    assertFalse("Component not properly updated", presenter.getValue());
  }

  @Test
  public void testDeclarativeBindingToElemental2IsElement() throws Exception {
    final HasBoundElemental2IsElement instance = IOC.getBeanManager().lookupBean(HasBoundElemental2IsElement.class).getInstance();
    final SimpleHTMLInputElementPresenter presenter = instance.getTextPresenter();
    final TestModel model = instance.getBinder().getModel();

    presenter.setValue("UI change");
    invokeEventListeners(presenter.getElement(), "change");
    assertEquals("Model not properly updated", "UI change", model.getValue());

    model.setValue("model change");
    assertEquals("Component not properly updated", "model change", presenter.getValue());
  }

  @Test
  public void testDeclarativeBindingToElemental2IsElementWithTakesValue() throws Exception {
    final HasBoundElemental2IsElement instance = IOC.getBeanManager().lookupBean(HasBoundElemental2IsElement.class).getInstance();
    final TakesValueElemental2CheckInputPresenter presenter = instance.getCheckPresenter();
    final TestModel model = instance.getBinder().getModel();

    assertFalse("Expected model.isActive() to start as false.", model.isActive());

    presenter.setValue(true);
    invokeEventListeners(presenter.getElement(), "change");
    assertTrue("Model not properly updated", model.isActive());

    model.setActive(false);
    assertFalse("Component not properly updated", presenter.getValue());
  }

  /**
   * Regression tests for ERRAI-945.
   */
  @Test
  public void testDeclarativeBindingToErraiTextInput() throws Exception {
    final ViewWithInput view = IOC.getBeanManager().lookupBean(ViewWithInput.class).getInstance();
    final TestModel model = view.binder.getModel();
    // Preconditions
    assertNull(model.getValue());
    assertTrue(view.value.getValue().isEmpty());

    // Test
    model.setValue("foo");
    assertEquals(model.getValue(), view.value.getValue());

    view.value.setValue("bar");
    invokeEventListeners(view.value, "change");
    assertEquals(view.value.getValue(), model.getValue());
  }

  @Test
  public void testDeclarativeBindingWithOnKeyUpForTextInput() throws Exception {
    final ViewWithOnKeyUpInput view = IOC.getBeanManager().lookupBean(ViewWithOnKeyUpInput.class).getInstance();
    final TestModel model = view.binder.getModel();
    // Preconditions
    assertNull(model.getValue());
    assertTrue(view.value.getValue().isEmpty());

    // Test
    model.setValue("foo");
    assertEquals(model.getValue(), view.value.getValue());

    view.value.setValue("bar");
    invokeEventListeners(view.value, "keyup");
    assertEquals(view.value.getValue(), model.getValue());
  }

  @Test
  public void testTypeInNonBindablePackageIsNotBound() {
    try {
      final DataBinder<TestModelInNonBindablePkg> binder = DataBinder.forType(TestModelInNonBindablePkg.class);
      binder.getModel();
      fail("Expected 'No proxy' exception. TestModelInNonBindablePkg should be non-bindable.");
    } catch (AssertionError ae) {
      throw ae;
    } catch (Exception ex) {
      assertTrue(ex.getMessage().contains("No proxy"));
    }
  }
}
