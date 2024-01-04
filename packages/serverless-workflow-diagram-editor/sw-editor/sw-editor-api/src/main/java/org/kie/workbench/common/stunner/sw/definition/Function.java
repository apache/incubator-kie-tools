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


package org.kie.workbench.common.stunner.sw.definition;

import jsinterop.annotations.JsType;
import org.treblereel.gwt.json.mapper.annotation.JSONMapper;
import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.j2cl.processors.annotations.GWT3Export;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
public class Function {

    public String name;

    public String operation;

    public FunctionType type;

    public String authRef;

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getOperation() {
        return operation;
    }

    public final void setOperation(String operation) {
        this.operation = operation;
    }

    public final FunctionType getType() {
        return type;
    }

    public final void setType(FunctionType type) {
        this.type = type;
    }

    public final String getAuthRef() {
        return authRef;
    }

    public final void setAuthRef(String authRef) {
        this.authRef = authRef;
    }
}
