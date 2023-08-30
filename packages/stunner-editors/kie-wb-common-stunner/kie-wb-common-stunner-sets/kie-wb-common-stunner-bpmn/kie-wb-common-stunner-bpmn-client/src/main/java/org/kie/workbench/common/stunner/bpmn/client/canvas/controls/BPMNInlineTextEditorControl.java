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



package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.graph.Element;

@BPMN
public class BPMNInlineTextEditorControl extends CanvasInlineTextEditorControl {

    @Inject
    public BPMNInlineTextEditorControl(FloatingView<IsWidget> floatingView,
                                       @InlineTextEditorBox TextEditorBox<AbstractCanvasHandler, Element> textEditorBox) {
        super(floatingView, textEditorBox);
    }

    @Override
    public boolean isFiltered(Object bean) {
        if (bean instanceof BaseGateway || bean instanceof BaseEndEvent ||
                bean instanceof BaseStartEvent || bean instanceof BaseIntermediateEvent) {
            return false;
        }

        return true;
    }
}
