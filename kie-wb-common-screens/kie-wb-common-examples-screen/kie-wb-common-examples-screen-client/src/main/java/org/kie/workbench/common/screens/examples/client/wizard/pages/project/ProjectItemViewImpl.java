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

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.examples.model.ExampleProject;

@Dependent
@Templated
public class ProjectItemViewImpl extends Composite implements ProjectItemView {

    @DataField("project")
    Element project = DOM.createDiv();

    @DataField("project-selected")
    InputElement projectSelected = DOM.createInputCheck().cast();

    @DataField("project-name")
    SpanElement projectName = DOM.createSpan().cast();

    @Override
    public HandlerRegistration addClickHandler( final ClickHandler handler ) {
        return addHandler( handler,
                           ClickEvent.getType() );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<Boolean> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

    @Override
    public void setProject( final ExampleProject project ) {
        final SafeHtmlBuilder shb = new SafeHtmlBuilder();
        shb.appendEscaped( project.getName() );
        projectName.setInnerSafeHtml( shb.toSafeHtml() );
    }

    @EventHandler("project")
    public void onClick( final ClickEvent event ) {
        fireEvent( event );
    }

    @EventHandler("project-selected")
    public void onSelectProject( final ClickEvent event ) {
        ValueChangeEvent.fire( this,
                               projectSelected.isChecked() );
    }

}
