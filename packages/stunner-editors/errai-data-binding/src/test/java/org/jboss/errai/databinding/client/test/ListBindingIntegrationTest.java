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
import java.util.Collections;
import java.util.List;

import org.jboss.errai.databinding.client.DirectBindingListComponentModule;
import org.jboss.errai.databinding.client.ListComponentContainerModule;
import org.jboss.errai.databinding.client.ListComponentModule;
import org.jboss.errai.databinding.client.QualifiedListComponentModule;
import org.jboss.errai.databinding.client.TestModel;
import org.jboss.errai.databinding.client.TestModelWidget;
import org.jboss.errai.databinding.client.TestModelWithList;
import org.jboss.errai.databinding.client.TestModelWithListOfTestModels;
import org.jboss.errai.databinding.client.TestModelWithListWidget;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;

import static org.jboss.errai.databinding.client.components.ListComponent.forIsWidgetComponent;

/**
 * Tests binding to lists with the {@link DataBinder}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ListBindingIntegrationTest extends AbstractErraiIOCTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.databinding.DataBindingTestModule";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Convert.deregisterDefaultConverters();
  }

  public void testListHandlerBindingToListProperty() throws Exception {
    final ListComponent<TestModel, TestModelWidget> component = forIsWidgetComponent(TestModelWidget::new, c -> {}).inDiv();
    final TestModelWithListOfTestModels model = DataBinder.forType(TestModelWithListOfTestModels.class)
            .bind(component, "list", Convert.identityConverter(List.class)).getModel();
    model.setList(new ArrayList<>());
    final List<TestModel> list = model.getList();
    final TestModel one = new TestModel("one");
    final TestModel two = new TestModel("two");
    final TestModel three = new TestModel("three");

    runTestModelListAssertions(list, component, one, two, three);
  }

  public void testDeclarativeListHandlerBindingWithInjectedListComponent() throws Exception {
    final ListComponentModule module = IOC.getBeanManager().lookupBean(ListComponentModule.class).getInstance();
    final ListComponent<TestModel, TestModelWidget> component = module.list;
    final TestModelWithListOfTestModels model = module.binder.getModel();
    model.setList(new ArrayList<>());
    final List<TestModel> list = model.getList();
    final TestModel one = new TestModel("one");
    final TestModel two = new TestModel("two");
    final TestModel three = new TestModel("three");

    list.add(one);
    assertFalse(component.getComponent(0).isQualified());
    assertIndexOutOfBounds(component, 1);
    list.remove(0);
    assertIndexOutOfBounds(component, 0);

    runTestModelListAssertions(list, component, one, two, three);
  }
  public void testDeclarativeListHandlerBindingWithInjectedListDestroyerCallAfterRemove() throws Exception {
    final ListComponentModule module = IOC.getBeanManager().lookupBean(ListComponentModule.class).getInstance();
    final ListComponent<TestModel, TestModelWidget> component = module.list;
    final TestModelWithListOfTestModels model = module.binder.getModel();
    model.setList(new ArrayList<>());
    final List<TestModel> list = model.getList();
    final TestModel one = new TestModel("one");

    list.add(one);
    TestModelWidget testBean = component.getComponent(0);
    assertFalse(component.getComponent(0).isQualified());
    assertIndexOutOfBounds(component, 1);
    assertFalse(testBean.isDestroyed());
    list.remove(0);
    assertTrue(testBean.isDestroyed());
    assertIndexOutOfBounds(component, 0);

  }

  public void testDeclarativeBindingDirectlyToList() throws Exception {
    final DirectBindingListComponentModule module = IOC.getBeanManager()
            .lookupBean(DirectBindingListComponentModule.class).getInstance();
    final ListComponent<TestModel, TestModelWidget> component = module.list;
    final List<TestModel> list = module.binder.getModel();
    final TestModel one = new TestModel("one");
    final TestModel two = new TestModel("two");
    final TestModel three = new TestModel("three");

    list.add(one);
    assertFalse(component.getComponent(0).isQualified());
    assertIndexOutOfBounds(component, 1);
    list.remove(0);
    assertIndexOutOfBounds(component, 0);

    runTestModelListAssertions(list, component, one, two, three);
  }

  public void testListComponentElementTypesThroughIOC() throws Exception {
    final ListComponentContainerModule module = IOC.getBeanManager().lookupBean(ListComponentContainerModule.class).getInstance();
    assertEquals("div", module.defaultComponent.getElement().getTagName().toLowerCase());
    assertEquals("tbody", module.tableComponent.getElement().getTagName().toLowerCase());
  }

  public void testDeclarativeQualifiedListHandlerBindingWithInjectedListComponent() throws Exception {
    final QualifiedListComponentModule module = IOC.getBeanManager().lookupBean(QualifiedListComponentModule.class).getInstance();
    final ListComponent<TestModel, TestModelWidget> component = module.list;
    final TestModelWithListOfTestModels model = module.binder.getModel();
    model.setList(new ArrayList<>());
    final List<TestModel> list = model.getList();
    final TestModel one = new TestModel("one");

    list.add(one);
    assertTrue(component.getComponent(0).isQualified());
    assertIndexOutOfBounds(component, 1);
  }

  public void testListHandlerBindingToListDirectly() throws Exception {
    final ListComponent<TestModel, TestModelWidget> component = forIsWidgetComponent(TestModelWidget::new, c -> {}).inDiv();
    final List<TestModel> list = DataBinder.forListOfType(TestModel.class)
            .bind(component, "this", Convert.identityConverter(List.class)).getModel();
    final TestModel one = new TestModel("one");
    final TestModel two = new TestModel("two");
    final TestModel three = new TestModel("three");

    runTestModelListAssertions(list, component, one, two, three);
  }

  public void testListHandlerPauseAndResume() throws Exception {
    final ListComponent<TestModel, TestModelWidget> component = forIsWidgetComponent(TestModelWidget::new, c -> {}).inDiv();
    final DataBinder<List<TestModel>> binder = DataBinder.forListOfType(TestModel.class)
            .bind(component, "this", Convert.identityConverter(List.class));
    final TestModel one = new TestModel("one");
    final TestModel two = new TestModel("two");

    binder.getModel().add(one);
    assertEquals("Binding failed before pause called.", binder.getModel(), component.getValue());

    binder.pause();
    binder.getModel().add(two);
    assertEquals("Component was updated after binding paused.", Collections.singletonList(one), component.getValue());

    binder.resume(StateSync.FROM_MODEL);
    assertEquals(Arrays.asList(one, two), binder.getModel());
    assertEquals("Component not updated after resume.", binder.getModel(), component.getValue());

    binder.pause();
    binder.getModel().clear();
    assertEquals("Component was updated after binding paused.", Arrays.asList(one, two), component.getValue());

    binder.resume(StateSync.FROM_UI);
    assertEquals(Arrays.asList(one, two), component.getValue());
    assertEquals(component.getValue(), binder.getModel());
  }

  public void testListHasValueBindingToListProperty() throws Exception {
    final TestModelWithListWidget component = new TestModelWithListWidget();
    final TestModelWithList model = DataBinder.forType(TestModelWithList.class)
            .bind(component, "list", Convert.identityConverter(List.class)).getModel();
    model.setList(new ArrayList<>());
    final List<String> list = model.getList();
    runStringListAssertions(component, list, "one", "two", "three");
  }

  public void testListHasValueBindingToListDirectly() throws Exception {
    final TestModelWithListWidget component = new TestModelWithListWidget();
    final List<String> list = DataBinder.forListOfType(String.class)
            .bind(component, "this", Convert.identityConverter(String.class)).getModel();
    runStringListAssertions(component, list, "one", "two", "three");
  }

  private void runStringListAssertions(final TestModelWithListWidget component, final List<String> list, final String one,
          final String two, final String three) {
    list.add(one);
    assertEquals(list, component.getValue());

    list.add(three);
    assertEquals(list, component.getValue());

    list.add(1, two);
    assertEquals(list, component.getValue());

    list.remove(2);
    assertEquals(list, component.getValue());

    list.removeAll(Collections.singleton(two));
    assertEquals(list, component.getValue());

    list.addAll(Collections.singleton(three));
    assertEquals(list, component.getValue());

    list.addAll(1, Collections.singleton(two));
    assertEquals(list, component.getValue());

    list.clear();
    assertEquals(list, component.getValue());
  }

  private void runTestModelListAssertions(final List<TestModel> boundList, final ListComponent<TestModel, TestModelWidget> component, final TestModel one,
          final TestModel two, final TestModel three) {
    assertIndexOutOfBounds(component, 0);
    boundList.add(one);
    assertEquals(one, component.getComponent(0).getValue());
    assertIndexOutOfBounds(component, 1);

    boundList.add(three);
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(three, component.getComponent(1).getValue());
    assertIndexOutOfBounds(component, 2);

    boundList.add(1, two);
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(two, component.getComponent(1).getValue());
    assertEquals(three, component.getComponent(2).getValue());
    assertIndexOutOfBounds(component, 3);

    boundList.remove(2);
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(two, component.getComponent(1).getValue());
    assertIndexOutOfBounds(component, 2);

    boundList.removeAll(Collections.singleton(two));
    assertEquals(one, component.getComponent(0).getValue());
    assertIndexOutOfBounds(component, 1);

    boundList.addAll(Collections.singleton(three));
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(three, component.getComponent(1).getValue());
    assertIndexOutOfBounds(component, 2);

    boundList.addAll(1, Collections.singleton(two));
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(two, component.getComponent(1).getValue());
    assertEquals(three, component.getComponent(2).getValue());
    assertIndexOutOfBounds(component, 3);

    boundList.get(0).setValue("one!");
    assertEquals(one, component.getComponent(0).getValue());
    assertEquals(two, component.getComponent(1).getValue());
    assertEquals(three, component.getComponent(2).getValue());
    assertIndexOutOfBounds(component, 3);

    boundList.clear();
    assertIndexOutOfBounds(component, 0);
  }

  private static void assertIndexOutOfBounds(final ListComponent<TestModel, TestModelWidget> component, final int index) {
    try {
      component.getComponent(index);
    } catch (IndexOutOfBoundsException ex) {
      return;
    }

    fail("Index " + index + " was not out of bounds.");
  }

}
