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

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>A utility for matching simple package patterns to package names.
 *
 * <p>{@link #apply(String)} returns true iff an input is matched by at least one filter.
 *
 * @author mbarkley <mbarkley@redhat.com>
 */
public class SimplePackageFilter extends BasePackageFilter {

  /**
   * @param filters
   *          A collection of fully qualified class names or package patterns
   *          (package names with subpackage globbing i.e. org.jboss.errai.* or
   *          org.jboss.errai*).
   */
  public SimplePackageFilter(final Collection<String> filters) {
    super(filters);
  }

  @Override
  protected Collection<String> processFilters(final Collection<String> filters) {
    return new ArrayList<String>(filters);
  }

  @Override
  public boolean apply(final String input) {
    return matches(input);
  }

}
