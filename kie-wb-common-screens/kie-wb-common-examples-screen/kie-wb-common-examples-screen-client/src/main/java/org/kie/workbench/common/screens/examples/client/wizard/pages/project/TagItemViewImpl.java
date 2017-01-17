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

package org.kie.workbench.common.screens.examples.client.wizard.pages.project;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class TagItemViewImpl extends Composite implements TagItemView {

    @Inject
    @DataField("name")
    Span name;

    @Inject
    @DataField("closeIcon")
    Span closeIcon;

    @Override
    public String getName() {
        return name.getTextContent();
    }

    @Override
    public void setName( String name ) {
        this.name.setTextContent( name );
    }

    @Override
    public void hideCloseIcon() {
        closeIcon.getStyle().setProperty( "display", "none" );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler clickHandler ) {
        return addHandler( clickHandler, ClickEvent.getType() );
    }

    @EventHandler("closeIcon")
    public void onCloseIconClicked( final ClickEvent event ) {
        fireEvent( event );
    }

}
