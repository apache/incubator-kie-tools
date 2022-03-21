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

import java.util.List;

import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Represents the type of a JavaBean property with additional metadata for Errai
 * data binding.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@SuppressWarnings("rawtypes")
public class PropertyType {

  private final Class type;
  private final boolean bindable;
  private final boolean list;

  public PropertyType(Class type) {
    this (type, false, false);
  }
  
  public PropertyType(Class type, boolean bindable, boolean list) {
    this.type = type;
    this.bindable = bindable;
    this.list = list;
  }

  /**
   * Returns the type of this bean property.
   * 
   * @return property type.
   */
  public Class getType() {
    return type;
  }

  /**
   * Indicates whether or not the property type is {@link Bindable}.
   * 
   * @return true if type is bindable, otherwise false.
   */
  public boolean isBindable() {
    return bindable;
  }

  /**
   * Indicates whether or not the property type is a {@link List}.
   * 
   * @return true if type is a list, otherwise false.
   */
  public boolean isList() {
    return list;
  }
}
