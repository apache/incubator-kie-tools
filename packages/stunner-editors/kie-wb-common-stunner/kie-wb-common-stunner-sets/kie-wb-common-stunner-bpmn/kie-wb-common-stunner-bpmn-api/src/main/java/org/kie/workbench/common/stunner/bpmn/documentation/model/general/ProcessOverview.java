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


package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ProcessOverview {

    private General general;
    private Imports imports;
    private ProcessVariablesTotal dataTotal;

    private ProcessOverview() {
    }

    @JsOverlay
    public static final ProcessOverview create(final General general, final Imports imports, final ProcessVariablesTotal dataTotal) {
        final ProcessOverview instance = new ProcessOverview();
        instance.general = general;
        instance.imports = imports;
        instance.dataTotal = dataTotal;
        return instance;
    }

    @JsOverlay
    public final General getGeneral() {
        return general;
    }

    @JsOverlay
    public final Imports getImports() {
        return imports;
    }

    @JsOverlay
    public final ProcessVariablesTotal getDataTotal() {
        return dataTotal;
    }
}
