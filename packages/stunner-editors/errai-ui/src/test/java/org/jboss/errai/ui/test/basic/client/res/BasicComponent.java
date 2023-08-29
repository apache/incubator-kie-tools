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


package org.jboss.errai.ui.test.basic.client.res;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.test.common.client.dom.Element;

import static org.jboss.errai.ui.test.common.client.dom.Document.getDocument;

public class BasicComponent extends Composite {

  @Inject
  @DataField("c1")
  private Label content;

  @Inject
  @DataField
  private Button c2;

  @Inject
  @DataField
  private TextBox c3;

  @Inject
  @DataField
  private Anchor c4;

  @Inject
  @DataField
  private Image c6;

  @Inject
  @DataField
  private Anchor c5;

  @DataField("nc1")
  private Element nativeContent = getDocument().createElement("label");

  @DataField
  private Element nc2 = getDocument().createElement("button");

  @DataField
  private Element nc3 = getDocument().createElement("input");

  @DataField
  private Element nc4 = getDocument().createElement("a");

  @DataField
  private Element nc6 = getDocument().createElement("img");

  @DataField
  private Element nc5 = getDocument().createElement("a");

  @PostConstruct
  public void init() {
    content.getElement().setAttribute("id", "c1");
    content.setText("Added by component");

    com.google.gwt.dom.client.Element nativeInputElement = TemplateUtil.asElement(nativeContent);
    nativeInputElement.setAttribute("id", "nc1");
    nativeInputElement.setInnerText("Added by component");
  }

  public Label getLabel() {
    return content;
  }

  public Button getContent2() {
    return c2;
  }

  public TextBox getTextBox() {
    return c3;
  }

  public void setTextBox(TextBox box) {
    this.c3 = box;
  }

  public Anchor getC4() {
    return c4;
  }

  public Anchor getC5() {
    return c5;
  }

  public Image getC6() {
    return c6;
  }

  public Element getNativeLabel() {
    return nativeContent;
  }

  public Element getNativeTextBox() {
    return nc3;
  }

  public Element getNc4() {
    return nc4;
  }

  public Element getNc5() {
    return nc5;
  }

  public Element getNc6() {
    return nc6;
  }
}
