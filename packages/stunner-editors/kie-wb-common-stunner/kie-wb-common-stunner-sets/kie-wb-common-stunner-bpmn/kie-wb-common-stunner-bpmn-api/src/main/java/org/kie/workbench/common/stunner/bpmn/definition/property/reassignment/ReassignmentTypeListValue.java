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


package org.kie.workbench.common.stunner.bpmn.definition.property.reassignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@Portable
@Bindable
public class ReassignmentTypeListValue {

    private List<ReassignmentValue> values = new ArrayList<>();

    public ReassignmentTypeListValue() {

    }

    public ReassignmentTypeListValue(@MapsTo("values") final List<ReassignmentValue> values) {
        this.values = values;
    }

    public List<ReassignmentValue> getValues() {
        return values;
    }

    public void setValues(List<ReassignmentValue> values) {
        this.values = values;
    }

    public ReassignmentTypeListValue addValue(ReassignmentValue value) {
        this.values.add(value);
        return this;
    }

    public boolean isEmpty() {
        return values == null || values.isEmpty();
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(values));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReassignmentTypeListValue) {
            ReassignmentTypeListValue other = (ReassignmentTypeListValue) o;
            return Objects.equals(values,
                                  other.values);
        }
        return false;
    }

    @Override
    public String toString() {
        return values.stream()
                .map(String::valueOf)
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.joining(", "));
    }
}