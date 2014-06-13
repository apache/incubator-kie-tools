/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.pmgr.template.part;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Workbench panel part.
 */
@Dependent
public class WorkbenchPartTemplatePresenter implements WorkbenchPartPresenter {

    private View view;

    private String title;

    private String contextId;

    private Menus menus;

    private IsWidget titleDecoration;

    private PartDefinition definition;

    @Inject
    public WorkbenchPartTemplatePresenter( final View view ) {
        this.view = view;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public PartDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition( final PartDefinition definition ) {
        this.definition = definition;
    }

    @Override
    public View getPartView() {
        return view;
    }

    @Override
    public void setWrappedWidget( final IsWidget widget ) {
        this.view.setWrappedWidget( widget );
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle( final String title ) {
        this.title = title;
    }

    @Override
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void setMenus( Menus menus ) {
        this.menus = menus;
    }

    @Override
    public IsWidget getTitleDecoration() {
        return titleDecoration;
    }

    @Override
    public void setTitleDecoration( final IsWidget titleDecoration ) {
        this.titleDecoration = titleDecoration;
    }

    @Override
    public String getContextId() {
        return contextId;
    }

    @Override
    public void setContextId( String contextId ) {
        this.contextId = contextId;
    }

}
