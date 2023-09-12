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


package org.kie.workbench.common.stunner.client.widgets.inlineeditor;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.uberfire.client.mvp.UberElement;

public interface InlineEditorBoxView extends UberElement<InlineEditorBoxView.Presenter> {

    void show(final String name, final double width, double height);

    void hide();

    boolean isVisible();

    void setTextBoxInternalAlignment(final String alignment);

    void setMultiline(final boolean isMultiline);

    void setPlaceholder(final String placeholder);

    void setFontSize(final double size);

    void setFontFamily(final String fontFamily);

    void rollback();

    interface Presenter extends TextEditorBox<AbstractCanvasHandler, Element> {

        void onSave();

        void onClose();

        void onChangeName(final String value);

        String getNameValue();
    }
}
