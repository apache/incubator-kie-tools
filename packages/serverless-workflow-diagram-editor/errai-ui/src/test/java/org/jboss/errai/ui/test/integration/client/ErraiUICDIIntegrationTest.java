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


package org.jboss.errai.ui.test.integration.client;

import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.test.integration.client.res.AppScopedTemplatedBean;
import org.jboss.errai.ui.test.integration.client.res.BeanWithElementInjectionSites;
import org.jboss.errai.ui.test.integration.client.res.InjectsJsTypeDiv;
import org.jboss.errai.ui.test.integration.client.res.LazyTestHelper;
import org.jboss.errai.ui.test.integration.client.res.NestedAppScopedTemplatedBean;
import org.jboss.errai.ui.test.integration.client.res.QualifiedTemplatedBean;
import org.jboss.errai.ui.test.integration.client.res.TestAppBean;

/**
 * @author Mike Brock
 */
public class ErraiUICDIIntegrationTest extends AbstractErraiCDITest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ui.test.integration.Test";
  }

  public void testErraiUIBeanPropertyConstructedWithSelfProducedDependency() {
    final TestAppBean bean = IOC.getBeanManager().lookupBean(TestAppBean.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getW());
  }

  public void testAppScopedTemplatedBeanIsLoadedWhenAddedToWidget() throws Exception {
    final LazyTestHelper testHelper = IOC.getBeanManager().lookupBean(LazyTestHelper.class).getInstance();
    assertEquals("Precondition failed: the templated bean should not have been loaded yet.", 0, AppScopedTemplatedBean.postConstructCount);
    assertEquals("Precondition failed: the nested templated bean should not have been loaded yet.", 0, NestedAppScopedTemplatedBean.postConstructCount);

    RootPanel.get().add(testHelper.bean);
    assertEquals("The templated bean should have been loaded after being added to the RootPanel.", 1, AppScopedTemplatedBean.postConstructCount);
    assertEquals("The nested templated bean should have been loaded after being added to the RootPanel.", 1, NestedAppScopedTemplatedBean.postConstructCount);
  }

  public void testGWTUserElementInjection() throws Exception {
    final BeanWithElementInjectionSites bean = IOC.getBeanManager().lookupBean(BeanWithElementInjectionSites.class).getInstance();

    assertNotNull("AnchorElement was not injected.", bean.anchor);
    assertEquals("AnchorElement has wrong tag name.", "A", bean.anchor.getTagName());

    assertNotNull("Div was not injected.", bean.div);
    assertEquals("DivElement has wrong tag name.", "DIV", bean.div.getTagName());

    assertNotNull("ButtonElement was not injected.", bean.button);
    assertEquals("ButtonElement has wrong tag name.", "BUTTON", bean.button.getTagName());

    assertNotNull("TableCellElement was not injected.", bean.td);
    assertEquals("TableCellElement has the wrong tag name.", "TD", bean.td.getTagName());
  }

  public void testJsTypeInjection() throws Exception {
    final InjectsJsTypeDiv bean = IOC.getBeanManager().lookupBean(InjectsJsTypeDiv.class).getInstance();

    assertNotNull(bean.div);
    assertEquals("DIV", bean.div.getTagName());

    assertNotNull(bean.th);
    assertEquals("TH", bean.th.getTagName());
  }

  public void testQualifiedDataField() throws Exception {
    final QualifiedTemplatedBean bean = IOC.getBeanManager().lookupBean(QualifiedTemplatedBean.class, QualifierUtil.ANY_ANNOTATION).getInstance();

    assertNotNull("FlowPanel was not injected.", bean.content);
    assertTrue("FlowPanel element was not attached.", bean.content.getElement().hasParentElement());
    assertEquals("FlowPanel element was not attached to the right element.",
            TemplateWidgetMapper.get(bean).getElement(), bean.content.getElement().getParentElement());
  }

  public void testJsTypeElementQualifiedPropertyInjection() throws Exception {
    final BeanWithElementInjectionSites bean = IOC.getBeanManager().lookupBean(BeanWithElementInjectionSites.class).getInstance();

    assertNotNull("Text input was not injected.", bean.textInput);
    assertEquals("Text input does not have proper type.", "text", bean.textInput.getType());
    assertEquals("Text input does not have proper placeholder.", "fooblie", bean.textInput.getPlaceholder());

    assertNotNull("Number input was not injected.", bean.numberInput);
    assertEquals("Number input does not have proper type.", "number", bean.numberInput.getType());
    assertEquals("Number input does not have proper placeholder.", "1337", bean.numberInput.getPlaceholder());
  }

  public void testJsTypeElementQualifiedClassInjection() throws Exception {
    final BeanWithElementInjectionSites bean = IOC.getBeanManager().lookupBean(BeanWithElementInjectionSites.class).getInstance();

    assertNotNull("Input with class annotation was not injected.", bean.inputWithClassAnnotation);
    assertNotNull("Input with class and property annotations was not injected.", bean.inputWithClassAnnotationAndProperty);


    assertEquals("input does not have class value.", "my_class my_class1", bean.inputWithClassAnnotation.getClassName());
    assertEquals("input does not have class value.", "my_class", bean.inputWithClassAnnotationAndProperty.getClassName());
  }

  public void testElemental2TypeElementQualifiedClassInjection() throws Exception {
    final BeanWithElementInjectionSites bean = IOC.getBeanManager()
            .lookupBean(BeanWithElementInjectionSites.class)
            .getInstance();

    assertNotNull("Input with class annotation was not injected.",
            bean.elemental2InputWithClassAndPropertiesAnnotations);

    assertEquals("Input does not have class value.", "foo bar",
            bean.elemental2InputWithClassAndPropertiesAnnotations.className);

    assertEquals("Input does not have proper placeholder.", "bar",
            bean.elemental2InputWithClassAndPropertiesAnnotations.placeholder);

    assertEquals("Input does not have proper id.", "foo",
            bean.elemental2InputWithClassAndPropertiesAnnotations.id);

  }
}
