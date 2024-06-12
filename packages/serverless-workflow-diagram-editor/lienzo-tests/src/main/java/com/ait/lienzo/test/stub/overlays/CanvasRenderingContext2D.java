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
import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.ImageData;

@StubClass("elemental2.dom.CanvasRenderingContext2D")
public class CanvasRenderingContext2D extends BaseRenderingContext2D {

    public String font;
    public String setTextAlign;
    public String setTextBaseline;

    public void setFont(String font) {
        this.font = font;
    }

    public void setTextAlign(String setTextAlign) {
        this.setTextAlign = setTextAlign;
    }

    public void setTextBaseline(String setTextBaseline) {
        this.setTextBaseline = setTextBaseline;
    }

    public elemental2.dom.TextMetrics measureText(String text) {
        return new elemental2.dom.TextMetrics();
    }

    public interface FillStyleUnionType {

        static elemental2.dom.CanvasRenderingContext2D.FillStyleUnionType of(Object o) {
            return null;
        }
    }

    public ImageData getImageData(int sx, int sy, int sw, int sh) {
        return new ImageData(100, 100);
    }
}
