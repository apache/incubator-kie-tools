/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import java.util.List;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public abstract class CreateAnnotationWizardPage implements WizardPage {

    public enum PageStatus {
        VALIDATED,
        NOT_VALIDATED
    }

    protected String title;

    protected PageStatus status = PageStatus.NOT_VALIDATED;

    protected Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    protected Caller<DataModelerService> modelerService;

    protected KieModule project;

    protected AnnotationDefinition annotationDefinition;

    protected ElementType target = ElementType.FIELD;

    public CreateAnnotationWizardPage(Caller<DataModelerService> modelerService,
                                      Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        this.modelerService = modelerService;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PageStatus getStatus() {
        return status;
    }

    public void setStatus(PageStatus status) {
        this.status = status;
        fireStatusChangeEvent();
    }

    public boolean isValid() {
        return PageStatus.VALIDATED.equals(status);
    }

    @Override
    public void isComplete(Callback<Boolean> callback) {
        callback.callback(isValid());
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    public void fireStatusChangeEvent() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(this);
        wizardPageStatusChangeEvent.fire(event);
    }

    public String buildErrorList(List<DriverError> errors) {
        String message = "";
        for (DriverError error : errors) {
            message += error.getMessage();
        }
        return message;
    }
}
