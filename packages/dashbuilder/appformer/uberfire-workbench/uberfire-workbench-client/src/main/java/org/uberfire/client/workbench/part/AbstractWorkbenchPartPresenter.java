/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.part;

import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Workbench panel part.
 */
public abstract class AbstractWorkbenchPartPresenter implements WorkbenchPartPresenter {

    private final View view;

    private String title;

    private String contextId;

    private Menus menus;

    private IsWidget titleDecoration;

    private PartDefinition definition;

    @Inject
    public AbstractWorkbenchPartPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    @Override
    public PartDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final PartDefinition definition) {
        this.definition = definition;
    }

    @Override
    public View getPartView() {
        return view;
    }

    @Override
    public void setWrappedWidget(final IsWidget widget) {
        this.view.setWrappedWidget(widget);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menus);
    }

    @Override
    public void setMenus(Menus menus) {
        this.menus = menus;
    }

    @Override
    public IsWidget getTitleDecoration() {
        return titleDecoration;
    }

    @Override
    public void setTitleDecoration(final IsWidget titleDecoration) {
        this.titleDecoration = titleDecoration;
    }

    @Override
    public String getContextId() {
        return contextId;
    }

    @Override
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
}
