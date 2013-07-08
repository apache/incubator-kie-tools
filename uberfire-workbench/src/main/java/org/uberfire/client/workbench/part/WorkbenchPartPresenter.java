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
package org.uberfire.client.workbench.part;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel part.
 */
@Dependent
public class WorkbenchPartPresenter {

    public interface View
            extends
            UberView<WorkbenchPartPresenter>,
            RequiresResize {

        WorkbenchPartPresenter getPresenter();

        void setWrappedWidget( IsWidget widget );

        IsWidget getWrappedWidget();
    }

    private View view;

    private String title;

    private IsWidget titleDecoration;

    private PartDefinition definition;

    @Inject
    public WorkbenchPartPresenter( final View view ) {
        this.view = view;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        view.init( this );
    }

    public PartDefinition getDefinition() {
        return definition;
    }

    public void setDefinition( final PartDefinition definition ) {
        this.definition = definition;
    }

    public View getPartView() {
        return view;
    }

    public void setWrappedWidget( final IsWidget widget ) {
        this.view.setWrappedWidget( widget );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( final String title ) {
        this.title = title;
    }

    public IsWidget getTitleDecoration() {
        return titleDecoration;
    }

    public void setTitleDecoration( final IsWidget titleDecoration ) {
        this.titleDecoration = titleDecoration;
    }
}
