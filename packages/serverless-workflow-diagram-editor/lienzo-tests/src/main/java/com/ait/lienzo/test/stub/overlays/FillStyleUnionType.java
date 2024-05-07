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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.CanvasGradient;
import elemental2.dom.CanvasPattern;
import jsinterop.base.Js;

@StubClass("elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType")
public class FillStyleUnionType {

    static elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType of(Object o) {
        return null;
    }

    public CanvasGradient asCanvasGradient() {
        return Js.cast(this);
    }

    public CanvasPattern asCanvasPattern() {
        return Js.cast(this);
    }

    public String asString() {
        return Js.asString(this);
    }

    public boolean isCanvasGradient() {
        return (Object) this instanceof CanvasGradient;
    }

    public boolean isCanvasPattern() {
        return (Object) this instanceof CanvasPattern;
    }

    public boolean isString() {
        return (Object) this instanceof String;
    }
}
