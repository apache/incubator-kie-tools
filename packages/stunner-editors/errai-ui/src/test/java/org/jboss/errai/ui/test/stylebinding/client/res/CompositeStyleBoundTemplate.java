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


package org.jboss.errai.ui.test.stylebinding.client.res;

import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import elemental.client.Browser;
import elemental.html.SpanElement;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.test.common.client.dom.TextInputElement;

import static org.jboss.errai.ui.test.common.client.dom.Document.getDocument;

/**
 * @author Mike Brock
 */
@Templated("StyleBoundTemplate.html")
public class CompositeStyleBoundTemplate extends Composite implements StyleBoundTemplate {

  @Inject @Bound @AdminBinding @DataField private Label testA;
  @Inject @Bound @TestBinding @DataField private Label testB;

  @Inject @Bound @ComponentBinding @DataField private CustomComponent testC;
  @Inject @AdminBinding @DataField private CustomNonCompositeComponent testD;

  @AdminBinding @DataField private SpanElement elemental = Browser.getDocument().createSpanElement();
  @AdminBinding @DataField("user-element") private com.google.gwt.dom.client.SpanElement userElement = Document.get().createSpanElement();
  @AdminBinding @DataField private TextInputElement jstype = getDocument().createTextInputElement();

  @Override
  public Label getTestA() {
    return testA;
  }

  @Override
  public Label getTestB() {
    return testB;
  }

  @Override
  public CustomComponent getTestC() {
    return testC;
  }

  @Override
  public CustomNonCompositeComponent getTestD() {
    return testD;
  }

  @Override
  public TextInputElement getJstype() {
    return jstype;
  }

  @Override
  public SpanElement getElementalElement() {
    return elemental;
  }

  @Override
  public com.google.gwt.dom.client.SpanElement getUserSpanElement() {
    return userElement;
  }

  @Override
  public TestModel getTestModel() {
    return dataBinder.getModel();
  }

  @Inject @AutoBound DataBinder<TestModel> dataBinder;

  @TestBinding
  private void testBindingStyleUpdate(Style style) {
     if ("0".equals(getTestModel().getTestB())) {
       style.setVisibility(Style.Visibility.HIDDEN);
     }
     else {
       style.clearVisibility();
     }
  }

  @ComponentBinding
  private void testCustomComponentBindingStyleUpdate(Style style) {
    if ("0".equals(getTestModel().getTestC())) {
      style.setVisibility(Style.Visibility.HIDDEN);
    }
    else {
      style.clearVisibility();
    }
  }
}
