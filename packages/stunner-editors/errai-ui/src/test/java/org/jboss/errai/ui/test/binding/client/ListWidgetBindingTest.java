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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.databinding.client.BindableListWrapper;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.client.local.spi.InvalidBeanScopeException;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.test.binding.client.res.AppScopedBindingListWidget;
import org.jboss.errai.ui.test.binding.client.res.BindingItem;
import org.jboss.errai.ui.test.binding.client.res.BindingItemWidget;
import org.jboss.errai.ui.test.binding.client.res.BindingListWidget;
import org.jboss.errai.ui.test.binding.client.res.BindingTemplate;
import org.jboss.errai.ui.test.binding.client.res.SingletonBindingListWidget;
import org.jboss.errai.ui.test.common.client.TestModel;
import org.junit.Test;

/**
 * Tests data binding with {@link ListWidget}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ListWidgetBindingTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  @Test
  public void testAutomaticListBinding() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    automaticListBindingAssertions(app.getCompositeTemplate());
    automaticListBindingAssertions(app.getNonCompositeTemplate());
  }

  private void automaticListBindingAssertions(BindingTemplate<?> template) {
    assertNotNull("Template instance was not injected!", template);
    assertEquals("Expected zero widgets", 0, template.getListWidget().getComponentCount());

    TestModel model = template.getModel();
    assertEquals("Expected zero widgets", 0, template.getListWidget().getComponentCount());
    model.getChildren().add(new TestModel(3, "c3"));
    assertEquals("Expected one widget", 1, template.getListWidget().getComponentCount());
    model.getChildren().remove(0);
    assertEquals("Expected zero widgets", 0, template.getListWidget().getComponentCount());

    List<TestModel> children = new ArrayList<TestModel>();
    children.add(new TestModel(1, "c1"));
    children.add(new TestModel(2, "c2"));
    model.setChildren(children);
    assertEquals("Expected two widgets", 2, template.getListWidget().getComponentCount());
    model.getChildren().add(new TestModel(3, "c3"));
    assertEquals("Expected three widgets", 3, template.getListWidget().getComponentCount());
  }

  @Test
  public void testManualListBinding() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    manualListBindingAssertions(app.getCompositeTemplate().getListWidget());
    manualListBindingAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void manualListBindingAssertions(BindingListWidget<?> listWidget) {
    final List<TestModel> modelList = new ArrayList<TestModel>();
    final TestModel model0, model1;

    modelList.add(model0 = new TestModel());
    modelList.add(model1 = new TestModel());
    // binding a list of model objects
    listWidget.setItems(modelList);

    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());
    assertEquals("", listWidget.getComponent(0).getTextBox().getText());
    assertEquals("", listWidget.getComponent(1).getTextBox().getText());
    assertEquals("onItemsRendered should be called exactly one time", 1, listWidget.getItemsRenderedCalled());

    BindingItem itemWidget0 = listWidget.getComponent(0);
    assertEquals(itemWidget0, listWidget.getComponent(model0));
    BindingItem itemWidget1 = listWidget.getComponent(1);
    assertEquals(itemWidget1, listWidget.getComponent(model1));

    itemWidget0.getModel().setName("0");
    itemWidget1.getModel().setName("1");
    assertEquals("First item widget was not updated!", "0", itemWidget0.getTextBox().getText());
    assertEquals("Second item widget was not updated!", "1", itemWidget1.getTextBox().getText());

    itemWidget0.getTextBox().setValue("0-updated", true);
    itemWidget1.getTextBox().setValue("1-updated", true);
    assertEquals("First model object was not updated!", "0-updated", itemWidget0.getModel().getName());
    assertEquals("Second model object was not updated!", "1-updated", itemWidget1.getModel().getName());
  }

  @Test
  public void testListBindingOfNullList() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingOfNullListAssertions(app.getCompositeTemplate().getListWidget());
    bindingOfNullListAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingOfNullListAssertions(BindingListWidget<?> listWidget) {
    listWidget.setValue(null);
    List<TestModel> proxiedList = listWidget.getValue();

    assertNotNull(proxiedList);
    assertTrue(proxiedList instanceof BindableListWrapper);
    assertTrue(proxiedList.isEmpty());
  }

  @Test
  public void testUnorderedListWidget() {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel());

    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    ListWidget<TestModel, BindingItemWidget> listWidget = app.getUlListWidget();
    assertNotNull(listWidget);
    listWidget.setItems(modelList);


    Widget item = listWidget.getComponent(0);
    assertNotNull(item);
    Widget panel = item.getParent();
    assertTrue(panel instanceof HTMLPanel);
    assertEquals(panel.getElement().getNodeName(), "UL");
  }

  @Test
  public void testTableListWidget() {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel());

    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    ListWidget<TestModel, BindingItemWidget> listWidget = app.getTableListWidget();
    assertNotNull(listWidget);
    listWidget.setItems(modelList);

    Widget item = listWidget.getComponent(0);
    assertNotNull(item);
    Widget panel = item.getParent();
    assertTrue(panel instanceof HTMLPanel);
    assertEquals(panel.getElement().getNodeName(), "TABLE");
  }

  @Test
  public void testAppScopedWidgetInListCausesError() {
    List<TestModel> list = new ArrayList<TestModel>();
    AppScopedBindingListWidget listWidget = new AppScopedBindingListWidget();
    try {
      listWidget.setItems(list);
      fail("Did not throw InvalidBeanScopeException");
    } catch (InvalidBeanScopeException e) {}
  }

  @Test
  public void testSingletonWidgetInListCausesError() {
    List<TestModel> list = new ArrayList<TestModel>();
    SingletonBindingListWidget listWidget = new SingletonBindingListWidget();
    try {
      listWidget.setItems(list);
      fail("Did not throw InvalidBeanScopeException");
    } catch (InvalidBeanScopeException e) {}
  }

  @Test
  public void testListBindingAndAddItem() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));

    listWidget.setItems(modelList);
    assertEquals("Expected one widget", 1, listWidget.getComponentCount());

    // Add a widget to the UI by adding a model instance to the model list
    listWidget.getItems().add(new TestModel(1, "1"));
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    BindingItem itemWidget1 = listWidget.getComponent(1);
    assertEquals("Second item widget was not rendered correctly!", "1", itemWidget1.getTextBox().getText());
  }

  @Test
  public void testListBindingAndAddManyItems() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddManyItemsAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddManyItemsAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddManyItemsAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(1, "0"));

    listWidget.setItems(modelList);
    assertEquals("Expected one widgets", 1, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    for (int i = 1; i < 500; i++) {
      addModelList.add(new TestModel(i, ""+i));
    }

    // Add widgets to the UI by adding model instances to the model list
    listWidget.getItems().addAll(addModelList);
    assertEquals("Expected exactly 100 widgets", 500, listWidget.getComponentCount());
  }

  @Test
  public void testListBindingAndAddItemByIndex() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemByIndexAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemByIndexAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemByIndexAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 3, listWidget.getComponentCount());

    // Add a widget to the UI by adding a model instance to the model list
    listWidget.getItems().add(1, new TestModel(1, "1"));
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemByIndexUsingHtmlPanel() {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    ListWidget<TestModel, BindingItemWidget> listWidget = app.getTableListWidget();
    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 3, listWidget.getComponentCount());

    // Add a widget to the UI by adding a model instance to the model list
    listWidget.getValue().add(1, new TestModel(1, "1"));
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemByIndexAtHead() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemByIndexAtHeadAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemByIndexAtHeadAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemByIndexAtHeadAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 3, listWidget.getComponentCount());

    // Add a widget to the UI by adding a model instance to the model list
    listWidget.getItems().add(0, new TestModel(0, "0"));
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemByIndexAtTail() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemByIndexAtTailAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemByIndexAtTailAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemByIndexAtTailAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(2, "2"));

    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 3, listWidget.getComponentCount());

    // Add a widget to the UI by adding a model instance to the model list
    listWidget.getItems().add(3, new TestModel(3, "3"));
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItems() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemsAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemsAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemsAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    addModelList.add(new TestModel(2, "2"));
    addModelList.add(new TestModel(3, "3"));

    // Add widgets to the UI by adding model instances to the model list
    listWidget.getItems().addAll(addModelList);
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemsByIndex() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemsByIndexAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemsByIndexAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemsByIndexAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    addModelList.add(new TestModel(1, "1"));
    addModelList.add(new TestModel(2, "2"));

    // Add widgets to the UI by adding model instances to the model list
    listWidget.getItems().addAll(1, addModelList);
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemsByIndexUsingHtmlPanel() {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(3, "3"));

    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    ListWidget<TestModel, BindingItemWidget> listWidget = app.getUlListWidget();
    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    addModelList.add(new TestModel(1, "1"));
    addModelList.add(new TestModel(2, "2"));

    // Add widgets to the UI by adding model instances to the model list
    listWidget.getValue().addAll(1, addModelList);
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemsByIndexAtHead() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemsByIndexAtHeadAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemsByIndexAtHeadAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemsByIndexAtHeadAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    addModelList.add(new TestModel(0, "0"));
    addModelList.add(new TestModel(1, "1"));

    // Add widgets to the UI by adding model instances to the model list
    listWidget.getItems().addAll(0, addModelList);
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndAddItemsByIndexAtTail() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndAddItemsByIndexAtTailAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndAddItemsByIndexAtTailAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndAddItemsByIndexAtTailAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List<TestModel> addModelList = new ArrayList<TestModel>();
    addModelList.add(new TestModel(2, "2"));
    addModelList.add(new TestModel(3, "3"));

    // Add a widgets to the UI by adding model instances to the model list
    listWidget.getItems().addAll(2, addModelList);
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndClearItems() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndClearItemsAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndClearItemsAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndClearItemsAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 2, listWidget.getComponentCount());

    List <BindingItem> testWidgetsCleared = new ArrayList<BindingItem>();
    for (int i = 0; i < listWidget.getComponentCount(); i++) {
      testWidgetsCleared.add(listWidget.getComponent(i));
    }
    listWidget.getItems().clear();
    assertEquals("Expected zero widgets", 0, listWidget.getComponentCount());

    Iterator<BindingItem> itr = testWidgetsCleared.iterator();
    while(itr.hasNext()) {
      //BindingItemWidget.getNum() is incremented in a pre-destroy method
      assertEquals(1, itr.next().getNum());
    }
  }

  @Test
  public void testListBindingAndSetItems() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndSetItemsAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndSetItemsAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndSetItemsAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected two widgets", 4, listWidget.getComponentCount());

    listWidget.getItems().set(1, new TestModel(1, "1"));
    listWidget.getItems().set(2, new TestModel(2, "2"));
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndRemoveItem() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndRemoveItemAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndRemoveItemAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndRemoveItemAssertions(BindingListWidget<?> listWidget) {
    TestModel itemToRemove = new TestModel(4, "4");
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(itemToRemove);
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected five widgets", 5, listWidget.getComponentCount());

    BindingItem widget = listWidget.getComponent(2);
    listWidget.getItems().remove(itemToRemove);
    assertEquals(1, widget.getNum());
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndRemoveItems() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    BindingListWidget<?> listWidget = app.getCompositeTemplate().getListWidget();
    bindingAndRemoveItemsAssertions(listWidget);
  }

  private void bindingAndRemoveItemsAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> removeList = new ArrayList<TestModel>();
    TestModel removeModel1 = (new TestModel(4, "4"));
    TestModel removeModel2 = (new TestModel(5, "5"));
    removeList.add(removeModel1);
    removeList.add(removeModel2);

    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(removeModel1);
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(removeModel2);
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected six widgets", 6, listWidget.getComponentCount());

    List<BindingItem> testWidgetsRemoved = new ArrayList<BindingItem>();

    for(int i = 0; i < listWidget.getComponentCount(); i++) {
      testWidgetsRemoved.add(listWidget.getComponent(i));
    }

    listWidget.getItems().removeAll(removeList);

    for(int i = 0; i < listWidget.getComponentCount(); i++) {
      if ((i == 1) || (i == 4))
        assertEquals("Widget " + i + " failed", 1, testWidgetsRemoved.get(i).getNum());
      else
        assertEquals("Widget " + i + " failed", 0, testWidgetsRemoved.get(i).getNum());
    }
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndRemoveItemByIndex() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndRemoveItemByIndexAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndRemoveItemByIndexAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndRemoveItemByIndexAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(4, "4"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected five widgets", 5, listWidget.getComponentCount());

    BindingItem widget = listWidget.getComponent(2);
    listWidget.getItems().remove(2);
    assertEquals(1, widget.getNum());
    assertItemsRendered(listWidget);
  }

  @Test
  public void testListBindingAndCollectionsSort() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndCollectionSoryAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndCollectionSoryAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndCollectionSoryAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(3, "3"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(0, "0"));

    listWidget.setItems(modelList);
    assertEquals("Expected four widgets", 4, listWidget.getComponentCount());

    Collections.sort(listWidget.getItems(), new Comparator<TestModel>() {
      @Override
      public int compare(TestModel o1, TestModel o2) {
        return o1.getId() - o2.getId();
      }
    });

    assertItemsRendered(listWidget);
  }

  @Test
  public void testBindableListIteratorAdd() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingAndListIteratorAddAssertions(app.getCompositeTemplate().getListWidget());
    bindingAndListIteratorAddAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingAndListIteratorAddAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 3, listWidget.getComponentCount());

    ListIterator<TestModel> iter = listWidget.getValue().listIterator();
    iter.next();
    iter.next();
    iter.add(new TestModel(2, "2"));

    assertItemsRendered(listWidget);
  }

  @Test
  public void testBindableListIteratorRemove() {
    BindingTemplateTestApp app = IOC.getBeanManager().lookupBean(BindingTemplateTestApp.class).getInstance();
    bindingListIteratorRemoveAssertions(app.getCompositeTemplate().getListWidget());
    bindingListIteratorRemoveAssertions(app.getNonCompositeTemplate().getListWidget());
  }

  private void bindingListIteratorRemoveAssertions(BindingListWidget<?> listWidget) {
    List<TestModel> modelList = new ArrayList<TestModel>();
    modelList.add(new TestModel(0, "0"));
    modelList.add(new TestModel(1, "1"));
    modelList.add(new TestModel(5, "5"));
    modelList.add(new TestModel(2, "2"));
    modelList.add(new TestModel(3, "3"));

    listWidget.setItems(modelList);
    assertEquals("Expected three widgets", 5, listWidget.getComponentCount());

    ListIterator<TestModel> iter = listWidget.getValue().listIterator();
    for (int i = 0; i < 3; i++) {
      iter.next();
    }
    iter.remove();

    assertItemsRendered(listWidget);
  }

  private void assertItemsRendered(ListWidget<TestModel, ? extends BindingItem> listWidget) {
    assertEquals("Expected exactly four widgets", 4, listWidget.getComponentCount());

    BindingItem itemWidget0 = listWidget.getComponent(0);
    BindingItem itemWidget1 = listWidget.getComponent(1);
    BindingItem itemWidget2 = listWidget.getComponent(2);
    BindingItem itemWidget3 = listWidget.getComponent(3);
    assertEquals("First item widget was not rendered correctly!", "0", itemWidget0.getTextBox().getText());
    assertEquals("Second item widget was not rendered correctly!", "1", itemWidget1.getTextBox().getText());
    assertEquals("Third item widget was not rendered correctly!", "2", itemWidget2.getTextBox().getText());
    assertEquals("Fourth item widget was not rendered correctly!", "3", itemWidget3.getTextBox().getText());

    TestModel model0 = listWidget.getValue().get(0);
    TestModel model1 = listWidget.getValue().get(1);
    TestModel model2 = listWidget.getValue().get(2);
    TestModel model3 = listWidget.getValue().get(3);
    assertTrue("First item was not proxied!", model0 instanceof BindableProxy);
    assertTrue("Second item was not proxied!", model1 instanceof BindableProxy);
    assertTrue("Third item was not proxied!", model2 instanceof BindableProxy);
    assertTrue("Fourth item was not proxied!", model3 instanceof BindableProxy);

    assertSame("First item and widget model are not same proxy object!", itemWidget0.getModel(), model0);
    assertSame("Second item and widget model are not same proxy object!", itemWidget1.getModel(), model1);
    assertSame("Third item and widget model are not same proxy object!", itemWidget2.getModel(), model2);
    assertSame("Fourth item and widget model are not same proxy object!", itemWidget3.getModel(), model3);
  }

}
