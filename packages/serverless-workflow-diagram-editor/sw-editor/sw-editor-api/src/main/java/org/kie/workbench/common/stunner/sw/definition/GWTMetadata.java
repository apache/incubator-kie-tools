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

import elemental2.core.JsString;
import elemental2.core.Reflect;
import jsinterop.annotations.JsType;

@JsType
public class GWTMetadata {

    public void setName(String name) {
        Reflect.set(this, "name", name);
    }

    public String getName() {
        Object o = Reflect.get(this, "name");
        return new JsString(o).toString();
    }

    public String getType() {
        Object o = Reflect.get(this, "type");
        return new JsString(o).toString();
    }

    public void setType(String type) {
        Reflect.set(this, "type", type);
    }


    public void setIcon(String icon) {
        Reflect.set(this, "icon", icon);
    }

    public String getIcon() {
        Object o = Reflect.get(this, "icon");
        return new JsString(o).toString();
    }
}
