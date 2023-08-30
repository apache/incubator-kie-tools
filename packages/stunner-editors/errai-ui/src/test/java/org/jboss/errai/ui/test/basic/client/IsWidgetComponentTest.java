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

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.test.basic.client.res.IsWidgetComponentView;
import org.jboss.errai.ui.test.basic.client.res.IsWidgetParentComponent;
import org.junit.Test;

public class IsWidgetComponentTest extends AbstractErraiCDITest {

    @Override
    public String getModuleName() {
        return getClass().getName().replaceAll("client.*$", "Test");
    }

    @Test
    public void testContainsElements() {
        IsWidgetComponentTestApp app = IOC.getBeanManager().lookupBean(IsWidgetComponentTestApp.class).getInstance();
        IsWidgetParentComponent instance = app.getComponent();

        String innerHtml = instance.getElement().getInnerHTML();

        assertTrue( RegExp.compile("<div(.)*>Parent test div</div>").test(innerHtml));
        assertTrue( RegExp.compile("<div(.)*>Test div</div>").test(innerHtml));
    }

    @Test
    public void testCompositeTemplateCleanup() throws Exception {
        IsWidgetParentComponent parentInstance = IOC.getBeanManager().lookupBean(IsWidgetParentComponent.class).getInstance();
        IsWidgetComponentView instance = (IsWidgetComponentView) parentInstance.getIsWidgetComponent().asWidget();

        assertFalse("Composite templated beans should not have TemplateWidget mappings after initialization.", TemplateWidgetMapper.containsKey(instance));
        assertTrue("Composite templated bean should be attached after initialization.", instance.isAttached());
        assertTrue("Composite templated bean should be in the detach list after initialization.", RootPanel.isInDetachList(parentInstance));

        IOC.getBeanManager().destroyBean(parentInstance);

        assertFalse("Composite templated beans should not have TemplateWidget mappings after destructions.", TemplateWidgetMapper.containsKey(instance));
        assertFalse("Composite templated bean should not be attached after destruction.", instance.isAttached());
        assertFalse("Composite templated bean should not be in the detach list after destruction.", RootPanel.isInDetachList(parentInstance));

    }

}
