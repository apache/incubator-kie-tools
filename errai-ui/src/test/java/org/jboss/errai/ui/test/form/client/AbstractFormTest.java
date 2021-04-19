/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.test.form.client;

import static org.jboss.errai.ioc.client.container.IOC.*;

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ui.client.widget.AbstractForm;
import org.jboss.errai.ui.test.form.client.res.TestFormWidget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class AbstractFormTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.ui.test.form.Test";
  }

  public void testFormAddsHiddenIFrame() throws Exception {
    createTestFormWidget();
    assertNotNull(Document.get().getElementById(AbstractForm.ERRAI_FORM_FRAME_ID));
  }

  public void testMultipleFormsOnlyAddOneHiddenIFrame() throws Exception {
    // Should create one iframe
    createTestFormWidget();
    
    NodeList<Element> iFrames = Document.get().getElementsByTagName("iframe");
    final int initialNumberOfIFrames = iFrames.getLength();

    // Should not create another iframe
    createTestFormWidget();

    assertEquals(initialNumberOfIFrames, iFrames.getLength());
  }

  private TestFormWidget createTestFormWidget() {
    return getBeanManager().lookupBean(TestFormWidget.class).getInstance();
  }
}
