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


package org.jboss.errai.reflections.util;

import java.util.Collection;

import com.google.common.base.Predicate;

/**
 * Base functionality for filtering input by given package patterns.
 *
 * @author Max Barkley <mbarkley@redhat.com.
 */
public abstract class BasePackageFilter implements Predicate<String> {

  protected final Collection<String> filters;

  public BasePackageFilter(final Collection<String> filters) {
    this.filters = processFilters(filters);
  }

  /**
   * In {@link #apply(String)}, filters ending with '*' are compared to inputs with {@link String#startsWith(String)}
   * and other patterns are matched with {@link String#equals(Object)}. This method gives subclasses a chance to modify
   * filters before any matching is done (for example to convert java packages to paths).
   */
  protected abstract Collection<String> processFilters(Collection<String> filters);

  public abstract boolean apply(String input);

  public boolean matches(final String input) {
    boolean matches = false;

    for (final String pattern : filters) {
      if (pattern.endsWith("*")) {
        matches = input.startsWith(pattern.substring(0, pattern.length()-1));
      }
      else {
        matches = input.equals(pattern);
      }
      if (matches) break;
    }

    return matches;
  }

}
