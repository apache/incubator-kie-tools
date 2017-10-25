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

package org.guvnor.ala.ui.client.wizard.pipeline.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.ala.ui.client.wizard.pipeline.select.item.PipelineItemPresenter;
import org.guvnor.ala.ui.model.PipelineKey;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public class SelectPipelinePagePresenter
        implements WizardPage {

    public interface View extends UberElement<SelectPipelinePagePresenter> {

        void clear();

        void addPipelineItem(final IsElement element);

        String getTitle();
    }

    private final View view;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;
    private final ManagedInstance<PipelineItemPresenter> itemPresenterInstance;

    private List<PipelineItemPresenter> itemPresenters = new ArrayList<>();

    @Inject
    public SelectPipelinePagePresenter(final View view,
                                       final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent,
                                       final ManagedInstance<PipelineItemPresenter> itemPresenterInstance) {
        this.view = view;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
        this.itemPresenterInstance = itemPresenterInstance;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final Collection<PipelineKey> pipelines) {
        clear();
        pipelines.forEach(pipeline -> {
            final PipelineItemPresenter presenter = newItemPresenter();
            presenter.setup(pipeline);
            presenter.addContentChangeHandler(this::onContentChange);
            itemPresenters.add(presenter);
            view.addPipelineItem(presenter.getView());
        });

        itemPresenters.forEach(item -> item.addOthers(itemPresenters));
    }

    @Override
    public void initialise() {
    }

    @Override
    public void prepareView() {
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        for (PipelineItemPresenter item : itemPresenters) {
            if (item.isSelected()) {
                callback.callback(true);
                return;
            }
        }
        callback.callback(false);
    }

    public void clear() {
        view.clear();
        clearItemPresenters();
    }

    @Override
    public String getTitle() {
        return view.getTitle();
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public PipelineKey getPipeline() {
        return itemPresenters.stream()
                .filter(PipelineItemPresenter::isSelected)
                .map(PipelineItemPresenter::getPipeline)
                .findFirst()
                .orElse(null);
    }

    private void onContentChange() {
        wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(SelectPipelinePagePresenter.this));
    }

    protected PipelineItemPresenter newItemPresenter() {
        return itemPresenterInstance.get();
    }

    /**
     * suited for testing purposes.
     */
    protected List<PipelineItemPresenter> getItemPresenters() {
        return itemPresenters;
    }

    private void clearItemPresenters() {
        itemPresenters.forEach(itemPresenterInstance::destroy);
        itemPresenters.clear();
    }
}
