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

package org.jboss.errai.databinding.client;

import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DefaultConverter;

/**
 * Global default converter for testing purposes. Converters annotated with {@link DefaultConverter} should be auto
 * discovered and registered as part of the bootstrap process.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@DefaultConverter
public class AutoRegisteredDefaultConverter implements Converter<Boolean, String> {

  @Override
  public Boolean toModelValue(String widgetValue) {
    return true;
  }

  @Override
  public String toWidgetValue(Boolean modelValue) {
    return "AutoRegisteredDefaultConverter";
  }

  @Override
  public Class<Boolean> getModelType() {
    return Boolean.class;
  }

  @Override
  public Class<String> getComponentType() {
    return String.class;
  }

}
