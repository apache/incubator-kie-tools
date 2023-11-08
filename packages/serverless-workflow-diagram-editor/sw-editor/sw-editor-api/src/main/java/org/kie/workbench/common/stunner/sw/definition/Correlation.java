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
import org.treblereel.j2cl.processors.annotations.GWT3Export;

@JsType
@GWT3Export
public class Correlation {

    public String contextAttributeName;

    public String contextAttributeValue;

    public final String getContextAttributeValue() {
        return contextAttributeValue;
    }

    public final void setContextAttributeValue(String contextAttributeValue) {
        this.contextAttributeValue = contextAttributeValue;
    }

    public final String getContextAttributeName() {
        return contextAttributeName;
    }

    public final void setContextAttributeName(String contextAttributeName) {
        this.contextAttributeName = contextAttributeName;
    }
}
