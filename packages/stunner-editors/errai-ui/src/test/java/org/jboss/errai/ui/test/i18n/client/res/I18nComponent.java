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

package org.jboss.errai.ui.test.i18n.client.res;

import org.jboss.errai.ui.client.widget.LocaleListBox;

import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface I18nComponent {

  /**
   * @return the welcome_p
   */
  ParagraphElement getWelcome_p();

  /**
   * @return the label1
   */
  InlineLabel getLabel1();

  /**
   * @return the val1
   */
  InlineLabel getVal1();

  /**
   * @return the label2
   */
  InlineLabel getLabel2();

  /**
   * @return the val2
   */
  InlineLabel getVal2();

  Label getLongTextLabel();

  /**
   * @return the emailLabel
   */
  InlineLabel getEmailLabel();

  /**
   * @return the email
   */
  TextBox getEmail();

  /**
   * @return the passwordLabel
   */
  InlineLabel getPasswordLabel();

  /**
   * @return the password
   */
  TextBox getPassword();

  LocaleListBox getListBox();

  InlineLabel getNestedLabel();

  InlineLabel getVal1_1();

}
