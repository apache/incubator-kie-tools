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
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasFullSession;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationViolation;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidatorCallback;

import javax.enterprise.context.Dependent;

@Dependent
public class ValidateCommand extends AbstractToolbarCommand<DefaultCanvasFullSession> {

    @Override
    protected boolean getState() {
        // Always active.
        return true;
    }

    @Override
    public IconType getIcon() {
        return IconType.CHECK;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Validate";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        session.getCanvasValidationControl().validate( new CanvasValidatorCallback() {

            @Override
            public void onSuccess() {
                // TODO: Review this...
                callback.onCommandExecuted( null );
            }

            @Override
            public void onFail( final Iterable<CanvasValidationViolation> violations ) {
                // TODO: Review this...
                callback.onCommandExecuted( ( T ) violations );

            }

        } );

    }

}
