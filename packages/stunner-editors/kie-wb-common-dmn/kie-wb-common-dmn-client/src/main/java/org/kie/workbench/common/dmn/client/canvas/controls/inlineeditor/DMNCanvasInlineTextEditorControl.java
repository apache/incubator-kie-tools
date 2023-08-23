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

package org.kie.workbench.common.dmn.client.canvas.controls.inlineeditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.graph.Element;

@DMNEditor
@Dependent
@Observer
public class DMNCanvasInlineTextEditorControl extends org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl {

    @Inject
    public DMNCanvasInlineTextEditorControl(FloatingView<IsWidget> floatingView,
                                            @InlineTextEditorBox TextEditorBox<AbstractCanvasHandler, Element> textEditorBox) {
        super(floatingView, textEditorBox);
    }

    @PostConstruct
    @Override
    protected void initParameters() {
        isMultiline = false;
        borderOffsetX = 2d;
        borderOffsetY = 10d;
        underBoxOffset = 4d;
        topBorderOffset = 18.5d;
        fontSizeCorrection = 3d;
        maxInnerLeftBoxWidth = 190d;
        maxInnerLeftBoxHeight = 190d;
        scrollBarOffset = 13d;
        paletteOffsetX = 0d;
        maxInnerTopBoxWidth = 190d;
        maxInnerTopBoxHeight = 120d;
        innerBoxOffsetY = 0d;
    }

    @Override
    public boolean isFiltered(Object bean) {
        return !((DRGElement) bean).isAllowOnlyVisualChange();
    }

    @Override
    protected boolean isEditableForDoubleClick(Element element) {
        return super.isEditable(element);
    }
}
