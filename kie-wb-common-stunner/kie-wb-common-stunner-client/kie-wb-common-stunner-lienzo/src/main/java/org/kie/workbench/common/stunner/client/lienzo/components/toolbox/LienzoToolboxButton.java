/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButton;
import org.kie.workbench.common.stunner.core.client.components.toolbox.event.ToolboxButtonEventHandler;

public class LienzoToolboxButton implements ToolboxButton<IPrimitive<?>> {

    private final IPrimitive<?> icon;

    private ToolboxButtonEventHandler clickHandler;
    private ToolboxButtonEventHandler mouseDownHandler;
    private ToolboxButtonEventHandler mouseEnterHandler;
    private ToolboxButtonEventHandler mouseExitHandler;
    private HoverAnimation animation;

    public LienzoToolboxButton( final IPrimitive<?> icon ) {
        this.icon = icon;
    }

    @Override
    public IPrimitive<?> getIcon() {
        return icon;
    }

    @Override
    public HoverAnimation getAnimation() {
        return animation;
    }

    @Override
    public ToolboxButtonEventHandler getClickHandler() {
        return clickHandler;
    }

    @Override
    public ToolboxButtonEventHandler getMouseDownHandler() {
        return mouseDownHandler;
    }

    @Override
    public ToolboxButtonEventHandler getMouseEnterHandler() {
        return mouseEnterHandler;
    }

    @Override
    public ToolboxButtonEventHandler getMouseExitHandler() {
        return mouseExitHandler;
    }

    public LienzoToolboxButton setAnimation( final HoverAnimation animation ) {
        this.animation = animation;
        return this;
    }

    public LienzoToolboxButton setClickHandler( final ToolboxButtonEventHandler clickHandler ) {
        this.clickHandler = clickHandler;
        return this;
    }

    public LienzoToolboxButton setMouseDownHandler( final ToolboxButtonEventHandler mouseDownHandler ) {
        this.mouseDownHandler = mouseDownHandler;
        return this;
    }

    public LienzoToolboxButton setMouseEnterHandler( final ToolboxButtonEventHandler mouseEnterHandler ) {
        this.mouseEnterHandler = mouseEnterHandler;
        return this;
    }

    public LienzoToolboxButton setMouseExitHandler( final ToolboxButtonEventHandler mouseExitHandler ) {
        this.mouseExitHandler = mouseExitHandler;
        return this;
    }

}
