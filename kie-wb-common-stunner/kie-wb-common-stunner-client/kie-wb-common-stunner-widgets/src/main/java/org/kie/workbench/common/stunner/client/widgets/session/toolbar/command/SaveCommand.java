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

package org.kie.workbench.common.stunner.client.widgets.session.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasSaveControl;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidatorCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import javax.enterprise.context.Dependent;

@Dependent
public class SaveCommand extends AbstractToolbarCommand<DefaultCanvasFullSession> {

    @Override
    public IconType getIcon() {
        return IconType.SAVE;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Save";
    }

    @Override
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        if ( null != session && null != session.getCanvasSaveControl() ) {
            final CanvasSaveControl<AbstractCanvasHandler> saveControl = session.getCanvasSaveControl();
            saveControl.save( new CanvasValidatorCallback() {
                @Override
                public void onSuccess() {
                    final Diagram diagram = session.getCanvasHandler().getDiagram();
                    // TODO: Review this...
                    callback.onCommandExecuted( ( T ) diagram );

                }

                @Override
                public void onFail( final Iterable<CanvasValidationViolation> violations ) {
                    // TODO: Review this...
                    callback.onCommandExecuted( ( T ) violations );

                }

            } );

        }

    }

    @Override
    protected boolean getState() {
        // Always active for now.
        return true;
    }

}
