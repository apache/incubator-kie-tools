/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;

import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.Model;
import org.jboss.errai.ui.shared.api.annotations.ModelSetter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Used for testing declarative binding using {@link Model}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@EntryPoint
public class DeclarativeBindingModuleUsingModel extends DeclarativeBindingSuperType implements DeclarativeBindingModule {
  public static final Date TEST_DATE = DateTimeFormat.getFormat("yyyy/MM/dd").parse("1980/22/06");

  @Bound
  private final Label id = new Label("");

  @Bound(property = "child.name")
  private final TextBox name = new TextBox();

  // tests automatic initialization
  @Bound
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

  @ModelSetter
  public void setModel(TestModel model) {
    this.model = model;
  }

  public static class BindingDateConverter implements Converter<Date, String> {

    @Override
    public Date toModelValue(String widgetValue) {
      return TEST_DATE;
    }

    @Override
    public String toWidgetValue(Date modelValue) {
      return "testdate";
    }

    @Override
    public Class<Date> getModelType() {
      return Date.class;
    }

    @Override
    public Class<String> getComponentType() {
      return String.class;
    }
  }
}
