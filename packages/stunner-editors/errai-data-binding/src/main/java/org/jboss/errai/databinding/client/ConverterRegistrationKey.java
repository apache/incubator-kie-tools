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

import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.Converter;

/**
 * Instances of this class serve as registration keys for global default converters (see
 * {@link Convert#registerDefaultConverter(Class, Class, Converter)}).
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ConverterRegistrationKey {

  private final Class<?> modelType;
  private final Class<?> widgetType;

  public ConverterRegistrationKey(Class<?> modelType, Class<?> widgetType) {
    this.modelType = modelType;
    this.widgetType = widgetType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((modelType == null) ? 0 : modelType.hashCode());
    result = prime * result + ((widgetType == null) ? 0 : widgetType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConverterRegistrationKey other = (ConverterRegistrationKey) obj;
    if (modelType == null) {
      if (other.modelType != null)
        return false;
    }
    else if (!modelType.equals(other.modelType))
      return false;
    if (widgetType == null) {
      if (other.widgetType != null)
        return false;
    }
    else if (!widgetType.equals(other.widgetType))
      return false;
    return true;
  }

}
