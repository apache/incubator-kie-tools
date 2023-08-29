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


package org.jboss.errai.ui.test.basic.client;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ui.test.basic.client.res.BeanStrategy;
import org.jboss.errai.ui.test.basic.client.res.MixedStrategy;
import org.jboss.errai.ui.test.basic.client.res.TemplateStrategy;

import static org.jboss.errai.ioc.client.IOCUtil.getInstance;

public class AttributeMergingTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  public void testAllTemplateStrategy() throws Exception {
    final TemplateStrategy bean = getInstance(TemplateStrategy.class);

    assertEquals("template", bean.div1.getTitle());
    assertEquals("template", bean.div1.getLang());
    assertHasCssProperty(bean.div1, "display", "block");
    assertHasCssProperty(bean.div1, "height", "10px");
    assertHasCssProperty(bean.div1, "width", "100px");
    assertHasClassName(bean.div1, "template");
    assertHasClassName(bean.div1, "other");
    assertHasClassName(bean.div1, "bean");


    assertEquals("template", bean.div2.getTitle());
    assertEquals("template", bean.div2.getLang());
    assertHasCssProperty(bean.div2, "display", "block");
    assertHasCssProperty(bean.div2, "height", "10px");
    assertHasCssProperty(bean.div2, "width", "100px");
    assertHasClassName(bean.div2, "template");
    assertHasClassName(bean.div2, "other");
    assertHasClassName(bean.div2, "bean");
  }

  public void testAllBeanStrategy() throws Exception {
    final BeanStrategy bean = getInstance(BeanStrategy.class);

    assertEquals("bean", bean.div1.getTitle());
    assertEquals("bean", bean.div1.getLang());
    assertHasCssProperty(bean.div1, "display", "block");
    assertHasCssProperty(bean.div1, "height", "100px");
    assertHasCssProperty(bean.div1, "width", "100px");
    assertHasClassName(bean.div1, "template");
    assertHasClassName(bean.div1, "other");
    assertHasClassName(bean.div1, "bean");


    assertEquals("bean", bean.div2.getTitle());
    assertEquals("bean", bean.div2.getLang());
    assertHasCssProperty(bean.div2, "display", "block");
    assertHasCssProperty(bean.div2, "height", "100px");
    assertHasCssProperty(bean.div2, "width", "100px");
    assertHasClassName(bean.div2, "template");
    assertHasClassName(bean.div2, "other");
    assertHasClassName(bean.div2, "bean");
  }

  public void testMixedStrategies() throws Exception {
    final MixedStrategy bean = getInstance(MixedStrategy.class);

    assertEquals("bean", bean.div1.getTitle());
    assertEquals("bean", bean.div1.getLang());
    assertHasCssProperty(bean.div1, "display", "block");
    assertHasCssProperty(bean.div1, "height", "10px");
    assertHasCssProperty(bean.div1, "width", "100px");
    assertHasClassName(bean.div1, "template");
    assertHasClassName(bean.div1, "other");
    assertHasClassName(bean.div1, "bean");


    assertEquals("bean", bean.div2.getTitle());
    assertEquals("bean", bean.div2.getLang());
    assertHasCssProperty(bean.div2, "display", "block");
    assertHasCssProperty(bean.div2, "height", "10px");
    assertHasCssProperty(bean.div2, "width", "100px");
    assertHasClassName(bean.div2, "template");
    assertHasClassName(bean.div2, "other");
    assertHasClassName(bean.div2, "bean");
  }

  private static void assertHasClassName(final HTMLElement div1, final String className) {
    assertTrue("Element does not have class [" + className + "]. Classes: " + div1.getClassName(),
            div1.getClassList().contains(className));
  }

  private static void assertHasCssProperty(final HTMLElement element, final String propertyName, final String expectedPropertyValue) {
    assertEquals("The property [" + propertyName + "] has the wrong value. Element styles: " + element.getStyle().getCssText(),
            expectedPropertyValue, element.getStyle().getPropertyValue(propertyName));
  }

}
