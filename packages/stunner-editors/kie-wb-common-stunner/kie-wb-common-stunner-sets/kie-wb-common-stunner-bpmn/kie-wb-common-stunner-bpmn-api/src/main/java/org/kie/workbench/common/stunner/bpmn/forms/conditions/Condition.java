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


package org.kie.workbench.common.stunner.bpmn.forms.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class Condition {

    private String function;

    private List<String> params = new ArrayList<>();

    public Condition() {
    }

    public Condition(String function) {
        this.function = function;
    }

    public Condition(final @MapsTo("function") String function,
                     final @MapsTo("params") List<String> params) {
        this.function = function;
        this.params = params;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void addParam(String param) {
        params.add(param);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(function),
                                         Objects.hashCode(params));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Condition) {
            Condition other = (Condition) o;
            return Objects.equals(function, other.function) &&
                    Objects.equals(params, other.params);
        }
        return false;
    }
}
