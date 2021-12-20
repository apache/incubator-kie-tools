/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;

import com.google.gwt.dom.client.Document;

/**
 * For testing binding with IsElement that does not implement TakesValue or HasText.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class SimpleTextInputPresenter implements IsElement {

  private Input element = (Input) Document.get().createTextInputElement();

  @Override
  public HTMLElement getElement() {
    return element;
  }

  public void setValue(String value) {
    element.setValue(value);
  }

  public String getValue() {
    return element.getValue();
  }

}
