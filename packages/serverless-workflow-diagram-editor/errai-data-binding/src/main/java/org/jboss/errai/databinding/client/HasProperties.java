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

import java.util.Map;

/**
 * Implementations of this interface allow for dynamic access of their JavaBean
 * properties by name. Since this is a GWT client-side interface, all
 * implementations must be code generated in the absence of Java reflection.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public interface HasProperties {

  /**
   * Returns the value of the JavaBean property with the given name.
   * 
   * @param propertyName
   *          the name of the JavaBean property, must not be null.
   * @return the current value of the corresponding property.
   * @throws NonExistingPropertyException
   *           if the implementing bean does not have a property with the given
   *           name.
   */
  public Object get(String propertyName);

  /**
   * Sets the value of the JavaBean property with the given name.
   * 
   * @param propertyName
   *          the name of the JavaBean property, must not be null.
   * @param value
   *          the value to set.
   * @throws NonExistingPropertyException
   *           if the implementing bean does not have a property with the given
   *           name.
   */
  public void set(String propertyName, Object value);

  /**
   * Returns a map of JavaBean property names to their {@link PropertyType}.
   * 
   * @return an immutable map of property names to their types. Never null.
   */
  public Map<String, PropertyType> getBeanProperties();

}
