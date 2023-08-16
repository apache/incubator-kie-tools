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


package com.ait.lienzo.client.core.event;

import elemental2.dom.MouseEvent;

public class OnEventHandlers {

    private OnMouseEventHandler m_onMouseDownEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseUpEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseMoveEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseClickEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    private OnMouseEventHandler m_onMouseDoubleClickEventHandle = DefaultOnMouseEventHandler.INSTANCE;

    public OnMouseEventHandler getOnMouseDownEventHandle() {
        return m_onMouseDownEventHandle;
    }

    public void setOnMouseDownEventHandle(OnMouseEventHandler onMouseDownEventHandle) {
        m_onMouseDownEventHandle = onMouseDownEventHandle;
    }

    public OnMouseEventHandler getOnMouseUpEventHandle() {
        return m_onMouseUpEventHandle;
    }

    public void setOnMouseUpEventHandle(OnMouseEventHandler onMouseUpEventHandle) {
        m_onMouseUpEventHandle = onMouseUpEventHandle;
    }

    public OnMouseEventHandler getOnMouseMoveEventHandle() {
        return m_onMouseMoveEventHandle;
    }

    public void setOnMouseMoveEventHandle(OnMouseEventHandler onMouseMoveEventHandle) {
        m_onMouseMoveEventHandle = onMouseMoveEventHandle;
    }

    public OnMouseEventHandler getOnMouseClickEventHandle() {
        return m_onMouseClickEventHandle;
    }

    public void setOnMouseClickEventHandle(OnMouseEventHandler onMouseClickEventHandle) {
        m_onMouseClickEventHandle = onMouseClickEventHandle;
    }

    public OnMouseEventHandler getOnMouseDoubleClickEventHandle() {
        return m_onMouseDoubleClickEventHandle;
    }

    public void setOnMouseDoubleClickEventHandle(OnMouseEventHandler onMouseDoubleClickEventHandle) {
        m_onMouseDoubleClickEventHandle = onMouseDoubleClickEventHandle;
    }

    public static class DefaultOnMouseEventHandler implements OnMouseEventHandler {

        static DefaultOnMouseEventHandler INSTANCE = new DefaultOnMouseEventHandler();

        @Override
        public boolean onMouseEventBefore(MouseEvent listener) {
            return true;
        }

        @Override
        public void onMouseEventAfter(MouseEvent listener) {

        }
    }

    public void destroy() {
        m_onMouseDownEventHandle = null;
        m_onMouseUpEventHandle = null;
        m_onMouseMoveEventHandle = null;
        m_onMouseClickEventHandle = null;
        m_onMouseDoubleClickEventHandle = null;
    }
}
