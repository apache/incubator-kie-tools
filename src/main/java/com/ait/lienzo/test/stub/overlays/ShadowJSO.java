/*
 * Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
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
package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Stub for class <code>com.ait.lienzo.client.core.types.Shadow$ShadowJSO</code>.
 * @author Roger Martinez
 */
@StubClass("com.ait.lienzo.client.core.types.Shadow$ShadowJSO")
public class ShadowJSO extends JavaScriptObject {

    private String color;
    private int blur;
    private boolean onFill;
    private Point2D.Point2DJSO offset;

    public static ShadowJSO make(final String color,
                                 final int blur,
                                 final boolean onfill,
                                 final Point2D.Point2DJSO offset) {
        ShadowJSO instance = new ShadowJSO();
        instance.color = color;
        instance.blur = blur;
        instance.onFill = onfill;
        instance.offset = offset;
        return instance;
    }

    protected ShadowJSO() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBlur() {
        return blur;
    }

    public void setBlur(int blur) {
        this.blur = blur;
    }

    public boolean getOnFill() {
        return onFill;
    }

    public void setOnFill(boolean onfill) {
        this.onFill = onfill;
    }

    public Point2D.Point2DJSO getOffset() {
        return offset;
    }

    public void setOffset(Point2D.Point2DJSO offset) {
        this.offset = offset;
    }
}