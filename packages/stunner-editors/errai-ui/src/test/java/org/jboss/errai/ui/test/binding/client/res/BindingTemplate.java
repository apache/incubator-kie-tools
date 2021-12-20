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

package org.jboss.errai.ui.test.binding.client.res;

import org.jboss.errai.ui.test.common.client.TestModel;
import org.jboss.errai.ui.test.common.client.dom.Element;
import org.jboss.errai.ui.test.common.client.dom.TextInputElement;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface BindingTemplate<W extends BindingItem> {

  Element getRoot();

  DivElement getIdDiv();

  Label getIdLabel();

  TextBox getNameTextBox();

  TextBox getDateTextBox();

  TextBox getPhoneNumberBox();

  Element getTitleField();

  TextInputElement getAge();

  BindingListWidget<W> getListWidget();

  TestModel getModel();

}
