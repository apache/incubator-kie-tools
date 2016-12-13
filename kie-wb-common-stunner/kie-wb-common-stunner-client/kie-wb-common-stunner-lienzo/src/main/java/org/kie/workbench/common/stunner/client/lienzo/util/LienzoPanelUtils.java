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

package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.validation.client.impl.Group;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

public class LienzoPanelUtils {

    public static LienzoPanel newPanel( final Glyph<Group> glyph, final int width, final int height ) {
        final com.ait.lienzo.client.widget.LienzoPanel panel = new LienzoPanel( width, height );
        final Layer layer = new Layer();
        panel.add( layer.setTransformable( true ) );
        layer.add( ( IPrimitive<?> ) glyph.getGroup() );
        return panel;
    }

}
