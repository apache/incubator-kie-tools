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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

@Templated
public class KnowledgeSessionListItemView implements KnowledgeSessionListItemPresenter.View {

    @Inject
    @DataField("is-default")
    private HTMLInputElement isDefault;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @DataField("type")
    private HTMLInputElement type;

    @Inject
    @DataField("clock-select-container")
    private KieEnumSelectElement<ClockTypeOption> clockSelect;

    @Inject
    @DataField("listeners-button")
    private HTMLDivElement listenersButton;

    @Inject
    @Named("strong")
    @DataField("listeners-count")
    private HTMLElement listenersCount;

    @Inject
    @Named("tbody")
    @DataField("listeners-container")
    private HTMLTableSectionElement listenersContainer;

    @Inject
    @DataField("add-listener-button")
    private HTMLButtonElement addListenerButton;

    @Inject
    @DataField("work-item-handlers-button")
    private HTMLDivElement workItemHandlersButton;

    @Inject
    @Named("strong")
    @DataField("work-item-handlers-count")
    private HTMLElement workItemHandlersCount;

    @Inject
    @Named("tbody")
    @DataField("work-item-handlers-container")
    private HTMLTableSectionElement workItemHandlersContainer;

    @Inject
    @DataField("add-work-item-handler-button")
    private HTMLButtonElement addWorkItemHandlerButton;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    private KnowledgeSessionListItemPresenter presenter;

    @Override
    public void init(final KnowledgeSessionListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("name")
    public void onNameChanged(final ChangeEvent ignore) {
        this.presenter.setName(name.value);
    }

    @EventHandler("type")
    public void onTypeChanged(final ChangeEvent ignore) {
        this.presenter.setType(type.value);
    }

    @EventHandler("is-default")
    public void onDefaultChanged(final ChangeEvent ignore) {
        presenter.setDefault(isDefault.checked);
    }

    @EventHandler("add-listener-button")
    public void onAddListenerButtonClicked(final ClickEvent ignore) {
        this.presenter.addListener();
    }

    @EventHandler("add-work-item-handler-button")
    public void onAddWorkItemHandlerButtonClicked(final ClickEvent ignore) {
        this.presenter.addWorkItemHandler();
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        this.presenter.remove();
    }

    @Override
    public void initListViewCompoundExpandableItems() {
        initListViewCompoundExpandableItem(listenersButton);
        initListViewCompoundExpandableItem(workItemHandlersButton);
    }

    public native void initListViewCompoundExpandableItem(final HTMLElement element) /*-{
        $wnd.jQuery(element).on("click", function () {
            var $this = $wnd.jQuery(this);
            var $heading = $this.parents(".list-group-item");
            var $subPanels = $heading.find(".list-group-item-container");
            var index = $heading.find(".list-view-pf-expand").index(this);

            //Remove all active status
            $heading.find(".list-view-pf-expand.active").find(".fa-angle-right").removeClass("fa-angle-down")
                    .end().removeClass("active")
                    .end().removeClass("list-view-pf-expand-active");

            // Add active to the clicked item
            $this.addClass("active")
                    .parents(".list-group-item").addClass("list-view-pf-expand-active")
                    .end().find(".fa-angle-right").addClass("fa-angle-down");

            // check if it needs to hide
            if ($subPanels.eq(index).hasClass("hidden")) {
                $heading.find(".list-group-item-container:visible").addClass("hidden");
                $subPanels.eq(index).removeClass("hidden");
            } else {
                $subPanels.eq(index).addClass("hidden");
                $heading.find(".list-view-pf-expand.active").find(".fa-angle-right").removeClass("fa-angle-down")
                        .end().removeClass("active")
                        .end().removeClass("list-view-pf-expand-active");
            }

            // close icon
            $subPanels.find(".close").on("click", function () {
                var $this = $wnd.jQuery(this);
                var $panel = $this.parent();

                // close the container and remove the active status
                $panel.addClass("hidden")
                        .parent().removeClass("list-view-pf-expand-active")
                        .find(".list-view-pf-expand.active").removeClass("active")
                        .find(".fa-angle-right").removeClass("fa-angle-down")
            });
        });

    }-*/;

    @Override
    public void closeAllExpandableListItems() {
        close(listenersButton);
        close(workItemHandlersButton);
    }

    private native void close(final HTMLElement element) /*-{
        $wnd.jQuery(element)
                .parents(".list-group-item")
                .find(".list-group-item-container")
                .find(".close")
                .click();
    }-*/;

    @Override
    public void setIsDefault(final boolean isDefault) {
        this.isDefault.checked = isDefault;
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setType(final String type) {
        this.type.value = type;
    }

    @Override
    public HTMLElement getListenersContainer() {
        return listenersContainer;
    }

    @Override
    public HTMLElement getWorkItemHandlersContainer() {
        return workItemHandlersContainer;
    }

    @Override
    public void setListenersCount(final int listenersCount) {
        this.listenersCount.textContent = Integer.toString(listenersCount);
    }

    @Override
    public void setWorkItemHandlersCount(final int workItemHandlersCount) {
        this.workItemHandlersCount.textContent = Integer.toString(workItemHandlersCount);
    }

    @Override
    public void setupClockElement(final KSessionModel kSessionModel,
                                  final KnowledgeSessionsModal parentPresenter) {
        clockSelect.setup(
                ClockTypeOption.values(),
                kSessionModel.getClockType(),
                clockTypeOption -> {
                    kSessionModel.setClockType(clockTypeOption);
                    parentPresenter.fireChangeEvent();
                });
    }
}
