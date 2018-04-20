/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.listener.ListenerListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.workitemhandler.WorkItemHandlerListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemView;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;

public class KnowledgeSessionListItemPresenter extends ListItemPresenter<KSessionModel, KnowledgeSessionsModal, KnowledgeSessionListItemPresenter.View> {

    private final Event<DefaultKnowledgeSessionChange> defaultKnowledgeSessionChangeEvent;
    private final WorkItemHandlersListPresenter workItemHandlersListPresenter;
    private final ListenersListPresenter listenersListPresenter;
    private final KieEnumSelectElement<ClockTypeOption> clockSelect;

    KSessionModel kSessionModel;
    KnowledgeSessionsModal parentPresenter;

    @Inject
    public KnowledgeSessionListItemPresenter(final View view,
                                             final Event<DefaultKnowledgeSessionChange> defaultKnowledgeSessionChangeEvent,
                                             final WorkItemHandlersListPresenter workItemHandlersListPresenter,
                                             final ListenersListPresenter listenersListPresenter,
                                             final KieEnumSelectElement<ClockTypeOption> clockSelect) {
        super(view);
        this.defaultKnowledgeSessionChangeEvent = defaultKnowledgeSessionChangeEvent;
        this.workItemHandlersListPresenter = workItemHandlersListPresenter;
        this.listenersListPresenter = listenersListPresenter;
        this.clockSelect = clockSelect;
    }

    @Override
    public KnowledgeSessionListItemPresenter setup(final KSessionModel kSessionModel,
                                                   final KnowledgeSessionsModal parentPresenter) {
        this.kSessionModel = kSessionModel;
        this.parentPresenter = parentPresenter;

        view.init(this);

        view.setIsDefault(kSessionModel.isDefault());
        view.setName(kSessionModel.getName());
        view.setType(kSessionModel.getType());
        view.setListenersCount(kSessionModel.getListeners().size());
        view.setWorkItemHandlersCount(kSessionModel.getWorkItemHandelerModels().size());

        listenersListPresenter.setup(
                view.getListenersContainer(),
                kSessionModel.getListeners(),
                (listener, presenter) -> presenter.setup(listener, this));

        workItemHandlersListPresenter.setup(
                view.getWorkItemHandlersContainer(),
                kSessionModel.getWorkItemHandelerModels(),
                (workItemHandler, presenter) -> presenter.setup(workItemHandler, this));

        clockSelect.setup(
                view.getClockSelectContainer(),
                ClockTypeOption.values(),
                kSessionModel.getClockType(),
                clockTypeOption -> {
                    kSessionModel.setClockType(clockTypeOption);
                    parentPresenter.fireChangeEvent();
                });

        view.initListViewCompoundExpandableItems();

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.signalKnowledgeBaseAddedOrRemoved();
    }

    @Override
    public KSessionModel getObject() {
        return kSessionModel;
    }

    public void setName(final String name) {
        kSessionModel.setName(name);
        parentPresenter.fireChangeEvent();
    }

    public void setType(final String type) {
        kSessionModel.setType(type);
        parentPresenter.fireChangeEvent();
    }

    public void addListener() {
        listenersListPresenter.add(new ListenerModel());
        signalListenerAddedOrRemoved();
    }

    public void addWorkItemHandler() {
        workItemHandlersListPresenter.add(new WorkItemHandlerModel());
        signalWorkItemHandlerAddedOrRemoved();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    public void closeAllExpandableListItems() {
        view.closeAllExpandableListItems();
    }

    public void signalWorkItemHandlerAddedOrRemoved() {
        view.setWorkItemHandlersCount(kSessionModel.getWorkItemHandelerModels().size());
        fireChangeEvent();
    }

    public void signalListenerAddedOrRemoved() {
        view.setListenersCount(kSessionModel.getListeners().size());
        fireChangeEvent();
    }

    public void setDefault(final boolean isDefault) {
        kSessionModel.setDefault(isDefault);
        defaultKnowledgeSessionChangeEvent.fire(new DefaultKnowledgeSessionChange(parentPresenter.getObject(), kSessionModel));
        parentPresenter.fireChangeEvent();
    }

    public void onDefaultKnowledgeSessionChanged(@Observes final DefaultKnowledgeSessionChange event) {
        if (event.getKBaseModel().equals(parentPresenter.getObject()) && !event.getNewDefault().equals(kSessionModel)) {
            kSessionModel.setDefault(false);
        }
    }

    @Dependent
    public static class ListenersListPresenter extends ListPresenter<ListenerModel, ListenerListItemPresenter> {

        @Inject
        public ListenersListPresenter(final ManagedInstance<ListenerListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class WorkItemHandlersListPresenter extends ListPresenter<WorkItemHandlerModel, WorkItemHandlerListItemPresenter> {

        @Inject
        public WorkItemHandlersListPresenter(final ManagedInstance<WorkItemHandlerListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    public interface View extends ListItemView<KnowledgeSessionListItemPresenter>,
                                  IsElement {

        void initListViewCompoundExpandableItems();

        void setIsDefault(final boolean isDefault);

        void setName(final String name);

        void setType(final String type);

        HTMLElement getClockSelectContainer();

        HTMLElement getListenersContainer();

        HTMLElement getWorkItemHandlersContainer();

        void setListenersCount(final int listenersCount);

        void setWorkItemHandlersCount(final int workItemHandlersCount);

        void closeAllExpandableListItems();
    }
}
