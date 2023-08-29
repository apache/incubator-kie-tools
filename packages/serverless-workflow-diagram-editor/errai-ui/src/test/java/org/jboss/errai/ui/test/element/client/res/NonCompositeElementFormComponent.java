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


package org.jboss.errai.ui.test.element.client.res;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import elemental.client.Browser;
import elemental.html.ButtonElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.test.common.client.dom.Document;

@Templated("ElementFormComponent.html")
public class NonCompositeElementFormComponent implements ElementFormComponent {

  private int numberOfTimesPressed = 0;

  @DataField
  private Element root = DOM.createDiv();

  @DataField
  private Element form = DOM.createForm();

  @DataField
  private org.jboss.errai.ui.test.common.client.dom.Element username = Document.getDocument().createElement("input");

  @Inject
  @DataField
  private PasswordTextBox password;

  @Inject
  @DataField("remember")
  private CheckBox rememberMe;

  @Inject
  @DataField
  private Button submit;

  @DataField
  private ButtonElement cancel = Browser.getDocument().createButtonElement();

  @Inject
  @DataField
  private ElementPresenter presenter;

  @Override
  public Element getForm() {
    return form;
  }

  @Override
  public org.jboss.errai.ui.test.common.client.dom.Element getUsername() {
    return username;
  }

  @Override
  public PasswordTextBox getPassword() {
    return password;
  }

  @Override
  public CheckBox getRememberMe() {
    return rememberMe;
  }

  @Override
  public Button getSubmit() {
    return submit;
  }

  @Override
  public ButtonElement getCancel() {
    return cancel;
  }

  @Override
  public ElementPresenter getElementPresenter() {
    return presenter;
  }

  @EventHandler("cancel")
  private void onClick(ClickEvent event) {
    numberOfTimesPressed++;
    /*
     * DO NOT REMOVE
     * HTMLUnit crashes when firing a click event without this.
     */
    event.preventDefault();
  }

  @Override
  public int getNumberOfTimesPressed() {
    return numberOfTimesPressed;
  }

  @Override
  public Element getElement() {
    return root;
  }
}
