/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;

import com.google.gwt.dom.client.Style.HasCssName;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.decision.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.widgets.client.cards.CardComponent;
import org.uberfire.client.mvp.UberElemental;

import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public abstract class BaseCardComponent<R extends BaseIncludedModelActiveRecord, V extends BaseCardComponent.ContentView> implements CardComponent {

    protected final V contentView;

    protected final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    protected R includedModel;

    protected DMNCardsGridComponent grid;

    public BaseCardComponent(final V contentView,
                             final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent) {
        this.contentView = contentView;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
    }

    @PostConstruct
    public void init() {
        contentView.init(this);
    }

    public void setup(final DMNCardsGridComponent grid,
                      final R includedModel) {
        this.grid = grid;
        this.includedModel = includedModel;
        refreshView();
    }

    protected void refreshView() {
        contentView.setPath(getTruncatedSubTitle());
    }

    @Override
    public HasCssName getIcon() {
        return DOWNLOAD;
    }

    @Override
    public String getTitle() {
        return getIncludedModel().getName();
    }

    @Override
    public String getUUID() {
        return getIncludedModel().getUUID();
    }

    @Override
    public HTMLElement getContent() {
        return contentView.getElement();
    }

    @Override
    public Function<String, Boolean> onTitleChanged() {
        return newName -> {

            final String oldName = getIncludedModel().getName();

            getIncludedModel().setName(newName);

            if (getIncludedModel().isValid()) {
                getIncludedModel().update();
                getGrid().refresh();
                refreshDecisionComponents();
                return true;
            } else {
                getIncludedModel().setName(oldName);
                return false;
            }
        };
    }

    protected String getTruncatedSubTitle() {
        return truncate(getSubTitle(), 60);
    }

    String getSubTitle() {
        if (isEmpty(getIncludedModel().getPath())) {
            return getIncludedModel().getNamespace();
        } else {
            return getIncludedModel().getPath();
        }
    }

    String truncate(final String value,
                    final int limit) {

        if (value.length() > limit) {
            return "..." + value.substring(value.length() - limit);
        }

        return value;
    }

    public void remove() {
        getIncludedModel().destroy();
        getGrid().refresh();
        refreshDecisionComponents();
    }

    void refreshDecisionComponents() {
        refreshDecisionComponentsEvent.fire(new RefreshDecisionComponents());
    }

    R getIncludedModel() {
        return includedModel;
    }

    DMNCardsGridComponent getGrid() {
        return grid;
    }

    public interface ContentView extends UberElemental<BaseCardComponent>,
                                         IsElement {

        void onRemoveButtonClick(final ClickEvent e);

        void setPath(final String path);
    }
}
