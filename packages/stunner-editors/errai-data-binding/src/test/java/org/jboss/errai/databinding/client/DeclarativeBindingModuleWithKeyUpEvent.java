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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.Model;

/**
 * @author Divya Dadlani <ddadlani@redhat.com>
 */
@EntryPoint
public class DeclarativeBindingModuleWithKeyUpEvent extends DeclarativeBindingSuperType implements DeclarativeBindingModule {

  @Bound
  private final Label id = new Label("");

  @Inject
  @Bound(property = "child.name", onKeyUp = true)
  private TextBox name;

  @Inject
  @Bound(onKeyUp = true)
  private TextBox age;

  @Inject
  @Model
  private TestModel model;

  @Override
  public Label getLabel() {
    return id;
  }

  @Override
  public TextBox getNameTextBox() {
    return name;
  }

  @Override
  public TextBox getAge() {
    return age;
  }

  @Override
  public TestModel getModel() {
    return model;
  }
}
