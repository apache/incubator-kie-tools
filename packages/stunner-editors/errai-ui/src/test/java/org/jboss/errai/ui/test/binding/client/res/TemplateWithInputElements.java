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

package org.jboss.errai.ui.test.binding.client.res;

import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.converter.DateInputConverter;
import org.jboss.errai.databinding.client.api.converter.DateTimeInputConverter;
import org.jboss.errai.databinding.client.api.converter.TimeInputConverter;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated
public class TemplateWithInputElements {

  @Inject
  @AutoBound
  public DataBinder<InputElementsModel> binder;

  @Inject
  @DataField
  public DivElement root;

  @Inject
  @Bound @DataField
  public InputElement text;

  @Inject
  @Bound @DataField
  public InputElement password;

  @Inject
  @Bound @DataField
  public InputElement number;

  @Inject
  @Bound @DataField
  public InputElement range;

  @Inject
  @Bound @DataField
  public InputElement checkbox;

  @Inject
  @Bound @DataField
  public InputElement file;

  @Inject
  @Bound(converter = DateInputConverter.class) @DataField
  public InputElement date;

  @Inject
  @Bound(converter = DateTimeInputConverter.class) @DataField
  public InputElement datetime;

  @Inject
  @Bound(converter = TimeInputConverter.class) @DataField
  public InputElement time;

  @Inject
  @Bound @DataField
  public InputElement email;

  @Inject
  @Bound @DataField
  public InputElement color;

  @Inject
  @Bound @DataField
  public InputElement radio;

  @Inject
  @Bound @DataField
  public InputElement tel;

  @Inject
  @Bound @DataField
  public InputElement url;

}
