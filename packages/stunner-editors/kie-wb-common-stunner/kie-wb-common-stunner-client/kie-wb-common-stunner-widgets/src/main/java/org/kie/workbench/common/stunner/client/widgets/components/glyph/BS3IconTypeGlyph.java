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


package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A glyph that is rendered using a GWTBootstrap3 Icon.
 * Notice actually this glyph only is being to render as DOMElement/Widget, there
 * is no canvas renderer for it.
 */
public final class BS3IconTypeGlyph implements Glyph {

    private final IconType iconType;

    public static BS3IconTypeGlyph create(final IconType iconType) {
        return new BS3IconTypeGlyph(iconType);
    }

    private BS3IconTypeGlyph(final IconType iconType) {
        this.iconType = iconType;
    }

    public IconType getIconType() {
        return iconType;
    }
}
