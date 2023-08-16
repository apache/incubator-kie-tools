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


package org.jboss.errai.databinding.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;

/**
 * For testing binding with IsElement that implements TakesValue.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class TakesValueCheckInputPresenter implements IsElement, TakesValue<Boolean> {

  private Input element = (Input) Document.get().createTextInputElement();

  @Override
  public HTMLElement getElement() {
    return element;
  }

  @Override
  public void setValue(final Boolean value) {
    element.setValue(value.toString());
  }

  @Override
  public Boolean getValue() {
    return Boolean.valueOf(element.getValue());
  }

}
