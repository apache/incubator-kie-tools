/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public abstract class RepositoryWizardPage implements WizardPage {

    protected CreateRepositoryWizardModel model;

    @Inject
    protected Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    protected boolean structurePageWasVisited = false;

    public void fireEvent() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent(this);
        wizardPageStatusChangeEvent.fire(event);
    }

    public CreateRepositoryWizardModel getModel() {
        return model;
    }

    public void setModel(CreateRepositoryWizardModel model) {
        this.model = model;
    }

    public void setStructurePageWasVisited(boolean structurePageWasVisited) {
        this.structurePageWasVisited = structurePageWasVisited;
    }
}
