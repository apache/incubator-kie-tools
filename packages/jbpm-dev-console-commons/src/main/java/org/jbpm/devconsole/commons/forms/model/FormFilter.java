/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.devconsole.commons.forms.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormFilter {

    private static final String NAMES_SEPARATOR = ";";

    private final List<String> names;

    public FormFilter() {
        this.names = new ArrayList<>();
    }

    public FormFilter(List<String> names) {
        this.names = names;
    }

    /**
     * Parses a filter from a request parameter holding a {@value NAMES_SEPARATOR}-separated list of form names.
     */
    public static FormFilter fromNamesParam(String namesParam) {
        if (namesParam == null || namesParam.isBlank()) {
            return new FormFilter();
        }
        return new FormFilter(Arrays.stream(namesParam.split(NAMES_SEPARATOR))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList()));
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names.addAll(names);
    }

    @Override
    public String toString() {
        return "FormFilter{" +
                "names=" + names +
                '}';
    }
}
