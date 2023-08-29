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


package com.ait.lienzo.client.widget.panel;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style.Cursor;
import elemental2.dom.HTMLDivElement;

public abstract class LienzoPanel<P extends LienzoPanel> {

    public abstract P add(Layer layer);

    public abstract P setBackgroundLayer(Layer layer);

    public abstract P setCursor(Cursor cursor);

    public abstract int getWidePx();

    public abstract int getHighPx();

    public abstract Viewport getViewport();

    public abstract HTMLDivElement getElement();

    public abstract void destroy();
}
