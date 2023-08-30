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

import org.jboss.errai.databinding.client.api.Converter;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class IdentityConverter<T> implements Converter<T, T> {

  private final Class<T> type;

  public IdentityConverter(final Class<T> type) {
    this.type = type;
  }

  @Override
  public Class<T> getModelType() {
    return type;
  }

  @Override
  public Class<T> getComponentType() {
    return type;
  }

  @Override
  public T toModelValue(T widgetValue) {
    return widgetValue;
  }

  @Override
  public T toWidgetValue(T modelValue) {
    return modelValue;
  }

}
