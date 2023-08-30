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
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class FunctionDef {

    private String name;

    private List<ParamDef> params = new ArrayList<>();

    public FunctionDef(String name) {
        this.name = name;
    }

    public FunctionDef(final @MapsTo("name") String name,
                       final @MapsTo("params") List<ParamDef> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamDef> getParams() {
        return params;
    }

    public void setParams(List<ParamDef> params) {
        this.params = params;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(name),
                                         Objects.hashCode(params));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof FunctionDef) {
            FunctionDef other = (FunctionDef) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(params, other.params);
        }
        return false;
    }

    @NonPortable
    public static class FunctionDefBuilder {

        private String functionName;

        private List<ParamDef> params = new ArrayList<>();

        private FunctionDefBuilder(String functionName) {
            this.functionName = functionName;
        }

        public static FunctionDefBuilder newFunction(String functionName) {
            return new FunctionDefBuilder(functionName);
        }

        public FunctionDefBuilder withParam(String paramName, String type) {
            params.add(new ParamDef(paramName, type));
            return this;
        }

        public FunctionDef build() {
            return new FunctionDef(functionName, params);
        }
    }
}
