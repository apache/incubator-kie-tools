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

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used to test bindings to widgets to lists.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class TestModelWithListWidget extends Widget implements HasValue<List<String>> {

  private List<String> value;


  @Override
  public List<String> getValue() {
    return value;
  }

  @Override
  public void setValue(List<String> value) {
    this.value = value;
  }

  @Override
  public void setValue(List<String> value, boolean fireEvents) {
    List<String> oldValue = getValue();
    setValue(value);
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
   addDomHandler(new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
          ValueChangeEvent.fire(TestModelWithListWidget.this, getValue());
        }
      }, ChangeEvent.getType());
  
    return addHandler(handler, ValueChangeEvent.getType());
  }

}
