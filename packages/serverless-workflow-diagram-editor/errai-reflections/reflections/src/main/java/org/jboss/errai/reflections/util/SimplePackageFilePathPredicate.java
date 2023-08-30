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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>A utility for matching package patterns to file paths.
 *
 * <p>{@link #apply(String)} returns false iff an input is matched by at least one filter.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class SimplePackageFilePathPredicate extends BasePackageFilter {

  public SimplePackageFilePathPredicate(final Collection<String> filters) {
    super(filters);
  }

  @Override
  protected Collection<String> processFilters(Collection<String> filters) {
    final Collection<String> processedFilters = new ArrayList<String>(filters.size());

    for (final String filter : filters) {
      String filterPath = filter.replace('.', File.separatorChar);
      if (!filterPath.endsWith("*"))
        filterPath += ".class";
      processedFilters.add(filterPath);
    }

    return processedFilters;
  }

  @Override
  public boolean apply(String input) {
    return !matches(input);
  }
}
