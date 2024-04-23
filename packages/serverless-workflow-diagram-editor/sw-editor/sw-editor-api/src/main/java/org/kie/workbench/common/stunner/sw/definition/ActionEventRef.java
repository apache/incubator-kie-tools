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
import org.kie.j2cl.tools.processors.annotations.GWT3Export;

@JsType
@GWT3Export
public class ActionEventRef {

    public String produceEventRef;
    public String consumeEventRef;
    public String consumeEventTimeout;
    public String data;
    public FunctionRefType invoke;

    public final String getProduceEventRef() {
        return produceEventRef;
    }

    public final void setProduceEventRef(String produceEventRef) {
        this.produceEventRef = produceEventRef;
    }

    public final String getConsumeEventRef() {
        return consumeEventRef;
    }

    public final void setConsumeEventRef(String consumeEventRef) {
        this.consumeEventRef = consumeEventRef;
    }

    public final String getConsumeEventTimeout() {
        return consumeEventTimeout;
    }

    public final void setConsumeEventTimeout(String consumeEventTimeout) {
        this.consumeEventTimeout = consumeEventTimeout;
    }

    public final String getData() {
        return data;
    }

    public final void setData(String data) {
        this.data = data;
    }

    public final FunctionRefType getInvoke() {
        return invoke;
    }

    public final void setInvoke(FunctionRefType invoke) {
        this.invoke = invoke;
    }
}
