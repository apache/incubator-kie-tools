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

package org.jboss.errai.ui.test.basic.client.res;

import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated
public class NonCompositeComponent {

  @DataField
  private DivElement root = Document.get().createDivElement();

  @Inject
  @DataField
  private TextBox text;

  @Inject
  @DataField
  private Button button;

  public TextBox getTextBox() {
    return text;
  }

  public Button getButton() {
    return button;
  }

  public DivElement getRoot() {
    return root;
  }

}
