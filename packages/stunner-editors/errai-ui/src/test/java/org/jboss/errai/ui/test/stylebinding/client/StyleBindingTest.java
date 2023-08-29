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


package org.jboss.errai.ui.test.stylebinding.client;

import com.google.gwt.dom.client.Element;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.jboss.errai.ui.test.stylebinding.client.res.CompositeStyleBoundTemplate;
import org.jboss.errai.ui.test.stylebinding.client.res.NonCompositeStyleBoundTemplate;
import org.jboss.errai.ui.test.stylebinding.client.res.StyleBoundTemplate;
import org.jboss.errai.ui.test.stylebinding.client.res.StyleControl;
import org.jboss.errai.ui.test.stylebinding.client.res.TestModel;

/**
 * @author Mike Brock
 */
public class StyleBindingTest extends AbstractErraiCDITest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ui.test.stylebinding.Test";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    setRemoteCommunicationEnabled(false);
    super.gwtSetUp();
    StyleBindingsRegistry.get().updateStyles();
  }

  public void testStyleBindingWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    styleBindingAssertions(instance);
  }

  public void testStyleBindingWithNonCompositeNonComposite() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    styleBindingAssertions(instance);
  }

  private void styleBindingAssertions(final StyleBoundTemplate instance) {
    assertEquals("hidden", instance.getTestA().getElement().getStyle().getVisibility());

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("visible", instance.getTestA().getElement().getStyle().getVisibility());
  }

  public void testStyleBindingWithElementalElementWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    elementalStyleBindingAssertions(instance);
  }

  public void testStyleBindingWithElementalElementWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    elementalStyleBindingAssertions(instance);
  }

  private void elementalStyleBindingAssertions(final StyleBoundTemplate instance) {
    assertEquals("hidden", instance.getElementalElement().getStyle().getVisibility());

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("visible", instance.getElementalElement().getStyle().getVisibility());
  }

  public void testStyleBindingWithGwtUserElementWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    gwtUserElementBindingAssertions(instance);
  }

  public void testStyleBindingWithGwtUserElementWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    gwtUserElementBindingAssertions(instance);
  }

  private void gwtUserElementBindingAssertions(final StyleBoundTemplate instance) {
    assertEquals("hidden", instance.getUserSpanElement().getStyle().getVisibility());

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("visible", instance.getUserSpanElement().getStyle().getVisibility());
  }

  public void testStyleBindingWithJsTypeElement() throws Exception {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    jsTypeElementBindingAssertions(instance);
  }

  public void testStyleBindingNonWithJsTypeElement() throws Exception {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    jsTypeElementBindingAssertions(instance);
  }

  private void jsTypeElementBindingAssertions(final StyleBoundTemplate instance) {
    final Element element = TemplateUtil.asElement(instance.getJstype());

    assertEquals("hidden", element.getStyle().getVisibility());

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("visible", element.getStyle().getVisibility());
  }

  public void testStyleBindingWithNonCompositeComponentInCompositeTemplate() throws Exception {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    nonCompositeComponentAssertions(instance);
  }

  public void testStyleBindingWithNonCompositeComponentInNonCompositeTemplate() throws Exception {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    nonCompositeComponentAssertions(instance);
  }

  private void nonCompositeComponentAssertions(final StyleBoundTemplate instance) {
    final Element element = TemplateWidgetMapper.get(instance.getTestD()).getElement().cast();

    assertEquals("hidden", element.getStyle().getVisibility());

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("visible", element.getStyle().getVisibility());
  }

  public void testDataBindingChangesUpdatesStyleWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    bindingChangesUpdateStyleAssertions(instance);
  }

  public void testDataBindingChangesUpdatesStyleWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    bindingChangesUpdateStyleAssertions(instance);
  }

  private void bindingChangesUpdateStyleAssertions(final StyleBoundTemplate instance) {
    assertEquals("", instance.getTestB().getElement().getStyle().getVisibility());

    instance.getTestModel().setTestB("0");

    assertEquals("0", instance.getTestB().getText());
    assertEquals("hidden", instance.getTestB().getElement().getStyle().getVisibility());
  }

  public void testCustomComponentDataBindingChangesUpdatesStyleWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    customComponenetBindingChangesUpdateStyleAssertions(instance);
  }

  public void testCustomComponentDataBindingChangesUpdatesStyleWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    customComponenetBindingChangesUpdateStyleAssertions(instance);
  }

  private void customComponenetBindingChangesUpdateStyleAssertions(final StyleBoundTemplate instance) {
    assertEquals("", instance.getTestC().getElement().getStyle().getVisibility());

    instance.getTestModel().setTestC("0");

    assertEquals("0", instance.getTestC().getValue());
    assertEquals("hidden", instance.getTestC().getElement().getStyle().getVisibility());
  }

  public void testDestroyingBeanCleansUpStyleBindingsWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    destructionCleanupAssertions(instance);
  }

  public void testDestroyingBeanCleansUpStyleBindingsWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    destructionCleanupAssertions(instance);
  }

  private void destructionCleanupAssertions(final StyleBoundTemplate instance) {
    assertEquals("hidden", instance.getTestA().getElement().getStyle().getVisibility());

    IOC.getBeanManager().destroyBean(instance);

    final SyncBeanDef<StyleControl> styleControl = IOC.getBeanManager().lookupBean(StyleControl.class);
    styleControl.getInstance().setAdmin(true);

    StyleBindingsRegistry.get().updateStyles();

    assertEquals("hidden", instance.getTestA().getElement().getStyle().getVisibility());
  }

  public void testDestroyingBeanCleansUpPropertyChangeHandlerWithCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(CompositeStyleBoundTemplate.class).getInstance();
    propertyChangeHandlerCleanupAssertions(instance);
  }

  public void testDestroyingBeanCleansUpPropertyChangeHandlerWithNonCompositeTemplate() {
    final StyleBoundTemplate instance = IOC.getBeanManager().lookupBean(NonCompositeStyleBoundTemplate.class).getInstance();
    propertyChangeHandlerCleanupAssertions(instance);
  }

  private void propertyChangeHandlerCleanupAssertions(final StyleBoundTemplate instance) {
    final StyleBindingsRegistry oldReg = StyleBindingsRegistry.get();
    try {
      StyleBindingsRegistry registry = new StyleBindingsRegistry() {
        @Override
        public void updateStyles(Object beanInst) {
          fail("updateStyles should not be called after bean was destroyed");
        }
      };

      TestModel model = instance.getTestModel();
      model.setTestB("");

      assertEquals("", instance.getTestB().getText());
      assertEquals("", instance.getTestB().getElement().getStyle().getVisibility());

      IOC.getBeanManager().destroyBean(instance);
      StyleBindingsRegistry.set(registry);

      model.setTestB("0");
      assertEquals("", instance.getTestB().getText());
      assertEquals("", instance.getTestB().getElement().getStyle().getVisibility());
    }
    finally {
      StyleBindingsRegistry.set(oldReg);
    }
  }

}
