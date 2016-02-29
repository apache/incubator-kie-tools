/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.project;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.examples.model.ExampleProject;

@Dependent
@Templated
public class ProjectPageViewImpl extends Composite implements ProjectPageView {

    @DataField
    Element projects = DOM.createDiv();

    @DataField("project-description")
    Element projectDescription = DOM.createDiv();

    private ProjectPage presenter;

    //Keep a record of ProjectItems created so they can be destroyed
    private Set<ProjectItemView> projectItemViews = new HashSet<ProjectItemView>();

    @Override
    public void init( final ProjectPage presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initialise() {
        destroy();
    }

    @Override
    public void setProjectsInRepository( final List<ExampleProject> projects ) {
        destroy();
        for ( ExampleProject project : projects ) {
            final ProjectItemView w = makeProjectWidget( project );
            this.projects.appendChild( w.asWidget().getElement() );
            this.projectItemViews.add( w );
        }
    }

    @Override
    public void destroy() {
        for ( ProjectItemView piw : projectItemViews ) {
            IOC.getBeanManager().destroyBean( piw );
        }
        projectItemViews.clear();
        projects.removeAllChildren();
    }

    private ProjectItemView makeProjectWidget( final ExampleProject project ) {
        final ProjectItemView projectItemView = IOC.getBeanManager().lookupBean( ProjectItemView.class ).getInstance();
        projectItemView.setProject( project );
        projectItemView.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( final ValueChangeEvent<Boolean> event ) {
                final boolean selected = event.getValue();
                if ( selected ) {
                    presenter.addProject( project );
                } else {
                    presenter.removeProject( project );
                }
            }
        } );
        projectItemView.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                final SafeHtmlBuilder shb = new SafeHtmlBuilder();
                shb.appendEscaped( project.getDescription() );
                projectDescription.setInnerSafeHtml( shb.toSafeHtml() );
            }
        } );
        return projectItemView;
    }

}
