/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.definition;

//TODO is it the same as EventRef
public class ActionEventRef {

    private String produceEventRef;
    private String consumeEventRef;
    private String consumeEventTimeout;
    private String data;
    private FunctionRefType invoke;

    public String getProduceEventRef() {
        return produceEventRef;
    }

    public void setProduceEventRef(String produceEventRef) {
        this.produceEventRef = produceEventRef;
    }

    public String getConsumeEventRef() {
        return consumeEventRef;
    }

    public void setConsumeEventRef(String consumeEventRef) {
        this.consumeEventRef = consumeEventRef;
    }

    public String getConsumeEventTimeout() {
        return consumeEventTimeout;
    }

    public void setConsumeEventTimeout(String consumeEventTimeout) {
        this.consumeEventTimeout = consumeEventTimeout;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FunctionRefType getInvoke() {
        return invoke;
    }

    public void setInvoke(FunctionRefType invoke) {
        this.invoke = invoke;
    }
}
