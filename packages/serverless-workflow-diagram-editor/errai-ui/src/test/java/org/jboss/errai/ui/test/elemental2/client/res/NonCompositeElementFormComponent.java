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


package org.jboss.errai.ui.test.elemental2.client.res;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.test.common.client.dom.Document;

import static elemental2.dom.DomGlobal.document;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
@Templated("ElementFormComponent.html")
public class NonCompositeElementFormComponent implements ElementFormComponent {

  private int numberOfTimesPressed = 0;

  @DataField
  private elemental2.dom.Element form = (elemental2.dom.Element) document.createElement("form");

  @Inject
  @DataField("help-email")
  private Elemental2EmailAnchor helpEmail;

  @DataField
  private org.jboss.errai.ui.test.common.client.dom.Element username = Document.getDocument().createElement("input");

  @Inject
  @DataField
  private HTMLInputElement password;

  @Inject
  @DataField("remember")
  private HTMLInputElement rememberMe;

  @Inject
  @DataField
  private HTMLButtonElement submit;

  @DataField
  private HTMLButtonElement cancel = (HTMLButtonElement) document.createElement("button");

  @Inject
  @DataField
  private ElementPresenter presenter;

  @Override
  public elemental2.dom.Element getForm() {
    return form;
  }

  @Override
  public org.jboss.errai.ui.test.common.client.dom.Element getUsername() {
    return username;
  }

  @Override
  public HTMLInputElement getPassword() {
    return password;
  }

  @Override
  public HTMLInputElement getRememberMe() {
    return rememberMe;
  }

  @Override
  public HTMLButtonElement getSubmit() {
    return submit;
  }

  @Override
  public Elemental2EmailAnchor getHelpEmail() {
    return helpEmail;
  }

  @Override
  public HTMLButtonElement getCancel() {
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
}
