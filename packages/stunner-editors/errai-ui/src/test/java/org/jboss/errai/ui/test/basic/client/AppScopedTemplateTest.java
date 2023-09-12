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

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.IOCUtil;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ui.shared.TemplateWidget;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.test.basic.client.res.AppScopedParent1;
import org.jboss.errai.ui.test.basic.client.res.AppScopedParent2;
import org.jboss.errai.ui.test.basic.client.res.NestedAppScopedTemplate;

public class AppScopedTemplateTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return getClass().getName().replaceAll("client.*$", "Test");
  }

  public void testCreateTwoTemplatedBeansWithAppScopedDataField() throws Exception {
    errorAsFailure("AppScopedParent1 problem", () -> {
      final AppScopedParent1 bean = IOCUtil.getInstance(AppScopedParent1.class);
      assertTrue("No TemplateWidget found for @Templated bean.", TemplateWidgetMapper.containsKey(bean));
      final TemplateWidget templateWidget = TemplateWidgetMapper.get(bean);
      assertTrue("TemplateWidget should be attached.", templateWidget.isAttached());
      final NestedAppScopedTemplate nested = bean.getNested();
      assertTrue(nested.isInitialized());
      assertTrue(TemplateWidgetMapper.containsKey(nested));
      final TemplateWidget nestedTemplateWidget = TemplateWidgetMapper.get(nested);
      assertTrue(nestedTemplateWidget.isAttached());
      assertEquals(TemplateWidget.class, nestedTemplateWidget.getParent().getClass());
      final TemplateWidget nestedTemplateWidgetParent = (TemplateWidget) nestedTemplateWidget.getParent();
      assertSame(templateWidget, nestedTemplateWidgetParent);
    });
    errorAsFailure("AppScopedParent2 problem", () -> {
      final AppScopedParent2 bean = IOCUtil.getInstance(AppScopedParent2.class);
      assertTrue("No TemplateWidget found for @Templated bean.", TemplateWidgetMapper.containsKey(bean));
      final TemplateWidget templateWidget = TemplateWidgetMapper.get(bean);
      assertTrue("TemplateWidget should be attached.", templateWidget.isAttached());
      final NestedAppScopedTemplate nested = bean.getNested();
      assertTrue(nested.isInitialized());
      assertTrue(TemplateWidgetMapper.containsKey(nested));
      final TemplateWidget nestedTemplateWidget = TemplateWidgetMapper.get(nested);
      assertTrue(nestedTemplateWidget.isAttached());
      assertEquals(TemplateWidget.class, nestedTemplateWidget.getParent().getClass());
      final TemplateWidget nestedTemplateWidgetParent = (TemplateWidget) nestedTemplateWidget.getParent();
      final Object nestedTemplateWidgetParentBean = TemplateWidgetMapper.reverseGet(nestedTemplateWidgetParent);
      assertSame("Expected AppScopedParent2 instance but found " + nestedTemplateWidgetParentBean.getClass().getName()
              + " instance.", Factory.maybeUnwrapProxy(bean), nestedTemplateWidgetParentBean);
      assertSame(templateWidget, nestedTemplateWidgetParent);
    });
  }

  private void errorAsFailure(final String errorMessage, final Runnable assertions) {
    try {
      assertions.run();
    } catch (final Throwable t) {
      throw new AssertionError(errorMessage, t);
    }
  }

}
