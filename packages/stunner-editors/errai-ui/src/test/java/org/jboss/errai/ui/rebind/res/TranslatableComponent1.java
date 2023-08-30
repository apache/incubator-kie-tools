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


package org.jboss.errai.ui.rebind.res;

import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class TranslatableComponent1 extends Composite {

  @DataField("welcome-p")
  private final ParagraphElement welcome_p = Document.get().createPElement();
  @Inject @DataField
  private InlineLabel label1;
  @Inject @DataField
  private InlineLabel val1;
  @Inject @DataField
  private InlineLabel label2;
  @Inject @DataField
  private InlineLabel val2;

  @Inject @DataField
  private InlineLabel emailLabel;
  @Inject @DataField
  private TextBox email;
  @Inject @DataField
  private InlineLabel passwordLabel;
  @Inject @DataField
  private TextBox password;

  /**
   * Constructor.
   */
  public TranslatableComponent1() {
  }

  /**
   * @return the welcome_p
   */
  public ParagraphElement getWelcome_p() {
    return welcome_p;
  }

  /**
   * @return the label1
   */
  public InlineLabel getLabel1() {
    return label1;
  }

  /**
   * @return the val1
   */
  public InlineLabel getVal1() {
    return val1;
  }

  /**
   * @return the label2
   */
  public InlineLabel getLabel2() {
    return label2;
  }

  /**
   * @return the val2
   */
  public InlineLabel getVal2() {
    return val2;
  }

  /**
   * @return the emailLabel
   */
  public InlineLabel getEmailLabel() {
    return emailLabel;
  }

  /**
   * @return the email
   */
  public TextBox getEmail() {
    return email;
  }

  /**
   * @return the passwordLabel
   */
  public InlineLabel getPasswordLabel() {
    return passwordLabel;
  }

  /**
   * @return the password
   */
  public TextBox getPassword() {
    return password;
  }

}
