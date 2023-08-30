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


package org.jboss.errai.ui.test.path.client.res;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("../PathRelativeParentComponent.html")
public class PathRelativeParentComponent extends Composite {

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

  @PostConstruct
  public void init() {
    content.getElement().setAttribute("id", "c1");
    content.setText("Added by component");
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
}
