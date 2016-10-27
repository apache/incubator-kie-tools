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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.validation.canvas.*;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class CanvasValidationControlImpl
        extends AbstractCanvasHandlerControl
        implements CanvasValidationControl<AbstractCanvasHandler> {

    CanvasValidator canvasValidator;
    Event<CanvasValidationSuccessEvent> validationSuccessEvent;
    Event<CanvasValidationFailEvent> validationFailEvent;

    @Inject
    public CanvasValidationControlImpl( final CanvasValidator canvasValidator,
                                        final Event<CanvasValidationSuccessEvent> validationSuccessEvent,
                                        final Event<CanvasValidationFailEvent> validationFailEvent ) {
        this.canvasValidator = canvasValidator;
        this.validationSuccessEvent = validationSuccessEvent;
        this.validationFailEvent = validationFailEvent;
    }

    @Override
    protected void doDisable() {
        this.canvasValidator = null;
        this.validationSuccessEvent = null;
        this.validationFailEvent = null;

    }

    @Override
    public void validate() {
        this.validate( null );
    }

    @Override
    public void validate( final CanvasValidatorCallback validatorCallback ) {
        if ( null != canvasHandler ) {
            final GraphRulesManager rulesManager = canvasHandler.getRuleManager();
            canvasValidator
                    .withRulesManager( rulesManager )
                    .validate( canvasHandler, new CanvasValidatorCallback() {

                        @Override
                        public void onSuccess() {
                            if ( null != validatorCallback ) {
                                validatorCallback.onSuccess();
                            }
                            validationSuccessEvent.fire( new CanvasValidationSuccessEvent( canvasHandler ) );
                        }

                        @Override
                        public void onFail( final Iterable<CanvasValidationViolation> violations ) {
                            if ( null != validatorCallback ) {
                                validatorCallback.onFail( violations );
                            }
                            validationFailEvent.fire( new CanvasValidationFailEvent( canvasHandler, violations ) );
                        }

                    } );

        }

    }

}
