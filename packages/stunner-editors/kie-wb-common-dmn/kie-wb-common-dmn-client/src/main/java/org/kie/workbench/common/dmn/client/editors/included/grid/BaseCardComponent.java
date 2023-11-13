/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.Objects;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;

import com.google.gwt.dom.client.Style.HasCssName;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.commands.RemoveIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.commands.RenameIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.widgets.client.cards.CardComponent;
import org.uberfire.client.mvp.UberElemental;

import static org.gwtbootstrap3.client.ui.constants.IconType.DOWNLOAD;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public abstract class BaseCardComponent<R extends BaseIncludedModelActiveRecord, V extends BaseCardComponent.ContentView> implements CardComponent {

    protected final V contentView;
    protected final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;
    protected final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;
    protected final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    protected final SessionManager sessionManager;
    protected final ImportRecordEngine recordEngine;
    protected final DMNIncludeModelsClient client;

    protected R includedModel;

    protected DMNCardsGridComponent grid;

    protected BaseCardComponent(final V contentView,
                                final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                                final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                final SessionManager sessionManager,
                                final ImportRecordEngine recordEngine,
                                final DMNIncludeModelsClient client,
                                final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent) {
        this.contentView = contentView;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.sessionCommandManager = sessionCommandManager;
        this.sessionManager = sessionManager;
        this.recordEngine = recordEngine;
        this.client = client;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
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
        if (getGrid().presentPathAsLink() && !isEmpty(getIncludedModel().getPath())) {
            contentView.setPathLink(getTruncatedSubTitle());
        } else {
            contentView.setPath(getTruncatedSubTitle());
        }
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

            final CommandResult<CanvasViolation> result = sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                        new RenameIncludedModelCommand(getIncludedModel(),
                                                                                                                       getGrid(),
                                                                                                                       refreshDecisionComponentsEvent,
                                                                                                                       newName));

            return Objects.equals(CanvasCommandResultBuilder.SUCCESS, result);
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
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                      getRemoveCommand());
    }

    abstract RemoveIncludedModelCommand getRemoveCommand();

    void refreshDecisionComponents() {
        refreshDecisionComponentsEvent.fire(new RefreshDecisionComponents());
    }

    R getIncludedModel() {
        return includedModel;
    }

    DMNCardsGridComponent getGrid() {
        return grid;
    }

    public void openPathLink() {
        getGrid().openPathLink(getIncludedModel().getPath());
    }

    public interface ContentView extends UberElemental<BaseCardComponent>,
                                         IsElement {

        void setPath(final String path);

        void setPathLink(final String path);
    }
}
