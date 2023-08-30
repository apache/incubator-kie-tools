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


package org.jboss.errai.ioc.util;

import java.util.Collection;
import java.util.HashSet;

import org.jboss.errai.common.metadata.ScannerSingleton;

/**
 * A utility class for tasks regarding the properties defined in
 * ErraiApp.properties .
 * 
 * @author mbarkley <mbarkley@redhat.com>
 */
public class PropertiesUtil {

  /**
   * Get the values in ErraiApp.properties for the given property name.
   * 
   * @param propertyName
   *          The name of the property for which to get values.
   * @param split
   *          The regex used to split lists in individual ErraiApp.properties
   *          files (or {@literal null}).
   * @return A collection of values for the property name.
   */
  public static Collection<String> getPropertyValues(final String propertyName, final String split) {
    final Collection<String> retVal = new HashSet<String>();

    final Collection<String> valueCollection = ScannerSingleton.getOrCreateInstance().getErraiProperties()
            .get(propertyName);
    for (final String list : valueCollection) {
      final String[] values = list.split(split);
      for (final String value : values) {
        retVal.add(value.trim());
      }
    }

    return retVal;
  }

}
