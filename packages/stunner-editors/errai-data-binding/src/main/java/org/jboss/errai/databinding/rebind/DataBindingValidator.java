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


package org.jboss.errai.databinding.rebind;

import java.util.List;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Validation utilities for data binding.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class DataBindingValidator {

  private DataBindingValidator() {}

  /**
   * Returns true if and only if the given property chain is a valid property expression rooted in the given bindable
   * type.
   *
   * @param bindableType
   *          The root type the given property chain is resolved against. Not null.
   * @param propertyChain
   *          The data binding property chain to validate. Not null.
   * @return True if the given property chain is resolvable from the given bindable type.
   */
  public static boolean isValidPropertyChain(final MetaClass bindableType, final String propertyChain) {
    if (!bindableType.isAnnotationPresent(Bindable.class) &&
        !DataBindingUtil.getConfiguredBindableTypes().contains(bindableType) &&
        !bindableType.getFullyQualifiedName().equals(List.class.getName())) {
      return false;
    }

    if ("this".equals(propertyChain)) {
      return true;
    }

    int dotPos = propertyChain.indexOf(".");
    if (dotPos <= 0) {
      return bindableType.getBeanDescriptor().getProperties().contains(propertyChain);
    }
    else {
      String thisProperty = propertyChain.substring(0, dotPos);
      String moreProperties = propertyChain.substring(dotPos + 1);
      if (!bindableType.getBeanDescriptor().getProperties().contains(thisProperty)) {
        return false;
      }
      MetaClass propertyType = bindableType.getBeanDescriptor().getPropertyType(thisProperty);
      return  isValidPropertyChain(propertyType, moreProperties);
    }
  }

  /**
   * @param bindableType
   *          The root type the given property chain is resolved against. Not null.
   * @param propertyChain
   *          The data binding property chain to validate. Not null.
   * @return The type of the given property within the given bindable type.
   */
  public static MetaClass getPropertyType(final MetaClass bindableType, final String propertyChain) {
    if ("this".equals(propertyChain)) {
      return bindableType;
    }

    final int dotPos = propertyChain.indexOf('.');
    if (dotPos >= 0) {
      final String firstProp = propertyChain.substring(0, dotPos);
      final String subChain = propertyChain.substring(dotPos+1);

      return getPropertyType(bindableType.getBeanDescriptor().getPropertyType(firstProp), subChain);
    }
    else {
      return bindableType.getBeanDescriptor().getPropertyType(propertyChain);
    }
  }
}
