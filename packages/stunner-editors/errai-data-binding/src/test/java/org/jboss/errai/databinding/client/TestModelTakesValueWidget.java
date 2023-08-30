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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used to test bindings to widgets of custom TakesValue types.
 */
public class TestModelTakesValueWidget extends Widget implements TakesValue<TestModel> {

  private TestModel value;

  @Override
  public void fireEvent(GwtEvent<?> event) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public TestModel getValue() {
    return value;
  }

  @Override
  public void setValue(TestModel value) {
    this.value = value;
  }

}
