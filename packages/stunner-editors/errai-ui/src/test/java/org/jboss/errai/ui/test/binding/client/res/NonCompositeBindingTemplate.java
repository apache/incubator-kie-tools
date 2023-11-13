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


package org.jboss.errai.ui.test.binding.client.res;

import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.test.common.client.TestModel;
import org.jboss.errai.ui.test.common.client.dom.Element;
import org.jboss.errai.ui.test.common.client.dom.TextInputElement;

import static org.jboss.errai.ui.test.common.client.dom.Document.getDocument;

@Templated("BindingTemplate.html")
public class NonCompositeBindingTemplate implements BindingTemplate<NonCompositeBindingItem> {

  @DataField
  private Element root = getDocument().createElement("div");

  @Bound(property = "id")
  @DataField
  private final DivElement idDiv = DOM.createElement("div").cast();

  @Inject
  @Bound
  @DataField
  private Label id;

  @Inject
  @Bound(property = "child.name")
  @DataField
  private TextBox name;

  @Bound(property = "title")
  @DataField
  private Element titleField = getDocument().createElement("div");

  @Bound
  @DataField
  private TextInputElement age = getDocument().createTextInputElement();

  @Inject
  @Bound(property = "lastChanged", converter = BindingDateConverter.class)
  @DataField("dateField")
  private TextBox date;

  @Inject
  @Bound
  @DataField("phone")
  private TextBox phoneNumber;

  @Inject
  @Bound
  @DataField
  private NonCompositeBindingListWidget children;

  private final TestModel model;

  @Inject
  public NonCompositeBindingTemplate(@AutoBound DataBinder<TestModel> binder) {
    model = binder.getModel();
  }

  @Override
  public Element getRoot() {
    return root;
  }

  @Override
  public DivElement getIdDiv() {
    return idDiv;
  }

  @Override
  public Label getIdLabel() {
    return id;
  }

  @Override
  public TextBox getNameTextBox() {
    return name;
  }

  @Override
  public TextBox getDateTextBox() {
    return date;
  }

  @Override
  public TextBox getPhoneNumberBox() {
    return phoneNumber;
  }

  @Override
  public Element getTitleField() {
    return titleField;
  }

  @Override
  public TextInputElement getAge() {
    return age;
  }

  @Override
  public BindingListWidget<NonCompositeBindingItem> getListWidget() {
    return children;
  }

  @Override
  public TestModel getModel() {
    return model;
  }
}
