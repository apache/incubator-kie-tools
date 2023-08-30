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

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.mvp.Command;

public abstract class AbstractInlineTextEditorBoxView<T extends InlineEditorBoxView.Presenter> implements IsElement {

    protected final Command showCommand;
    protected final Command hideCommand;

    protected T presenter;

    protected TranslationService translationService;

    private static final String DISPLAY = "display";
    private static final String DISPLAY_NONE = "none";
    private static final String DISPLAY_BLOCK = "block";

    @Inject
    @DataField
    Div editNameBox;

    protected AbstractInlineTextEditorBoxView(Command showCommand, Command hideCommand) {
        this.showCommand = showCommand;
        this.hideCommand = hideCommand;
    }

    protected AbstractInlineTextEditorBoxView() {
        showCommand = () -> this.getElement().getStyle().setProperty(DISPLAY,
                                                                     DISPLAY_BLOCK);
        hideCommand = () -> this.getElement().getStyle().setProperty(DISPLAY,
                                                                     DISPLAY_NONE);
    }

    abstract void initialize();

    public void setVisible() {
        showCommand.execute();
    }

    public void hide() {
        hideCommand.execute();
    }

    public boolean isVisible() {
        return !(getElement().getStyle().getPropertyValue(DISPLAY)).equals(DISPLAY_NONE);
    }

    public void scheduleDeferredCommand(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }
}
