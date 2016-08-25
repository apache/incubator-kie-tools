/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.Method;

public class MethodImpl implements Method {

    private String name;

    private List<String> parameters = new ArrayList<>( );

    private String body;

    private String returnType;

    public MethodImpl() {
    }

    public MethodImpl( String name, List<String> parameters, String body, String returnType ) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

}
