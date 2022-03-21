/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client.api;

import java.awt.Checkbox;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Contract for converters that perform model value to component value transformations and vice versa.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 *
 * @param <M>
 *          The type of the model value (field type of the model)
 * @param <C>
 *          The type of the component value (e.g. String for a {@link TextBox} (=HasValue&lt;String&gt;) or Boolean for a
 *          {@link Checkbox} (=HasValue&lt;Boolean&gt;)))
 */
public interface Converter<M, C> {

  /**
   * @return The type of the model value.
   */
  public Class<M> getModelType();

  /**
   * @return The type of the component value.
   */
  public Class<C> getComponentType();

  /**
   * Converts the provided widget value to a model value of type &lt;M&gt;.
   *
   * @param componentValue
   *          the component value to convert, may be null.
   * @return converted value for the model.
   */
  public M toModelValue(C componentValue);

  /**
   * Converts the provided model value to a value usable by widgets of type {@link HasValue HasValue&lt;W&gt;} or
   * {@link TakesValue TakesValue&lt;W&gt;}.
   *
   * @param modelValue
   *          the model value to convert, may be null.
   * @return converted value for the component.
   */
  public C toWidgetValue(M modelValue);
}
