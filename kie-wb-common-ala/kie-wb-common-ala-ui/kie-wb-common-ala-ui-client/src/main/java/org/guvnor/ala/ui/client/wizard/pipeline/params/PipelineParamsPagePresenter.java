/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard.pipeline.params;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class PipelineParamsPagePresenter
        implements WizardPage {

    public interface View
            extends UberElement<PipelineParamsPagePresenter> {

        void setForm(final IsElement element);
    }

    private final View view;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    private PipelineParamsForm pipelineParamsForm;

    @Inject
    public PipelineParamsPagePresenter(final View view,
                                       final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        this.view = view;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setPipelineParamsForm(final PipelineParamsForm pipelineParamsForm) {
        this.pipelineParamsForm = pipelineParamsForm;
        this.view.setForm(pipelineParamsForm.getView());
        pipelineParamsForm.addContentChangeHandler(this::onContentChanged);
    }

    @Override
    public void initialise() {
        pipelineParamsForm.initialise();
    }

    @Override
    public void prepareView() {
        pipelineParamsForm.prepareView();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        pipelineParamsForm.isComplete(callback);
    }

    @Override
    public String getTitle() {
        return pipelineParamsForm.getWizardTitle();
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    protected void onContentChanged() {
        wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(PipelineParamsPagePresenter.this));
    }
}
