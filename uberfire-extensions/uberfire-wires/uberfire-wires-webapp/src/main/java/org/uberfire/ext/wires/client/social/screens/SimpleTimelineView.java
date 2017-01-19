/*
 * Copyright 2017 JBoss Inc
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

package org.uberfire.ext.wires.client.social.screens;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import org.ext.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.ext.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
@Templated
public class SimpleTimelineView implements IsElement, SimpleTimelinePresenter.View {

    private SimpleTimelinePresenter presenter;

    @Inject
    @DataField
    Div panelContainer;

    @Inject
    @DataField
    Button newEvent;

    @EventHandler( "newEvent" )
    public void onNewEvent( final ClickEvent clickEvent ) {
        presenter.fireEvent();
    }

    @Override
    public void setupWidget( SimpleSocialTimelineWidgetModel model ) {
        DOMUtil.removeAllChildren( panelContainer );
        DOMUtil.appendWidgetToElement( panelContainer, new SimpleSocialTimelineWidget( model ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Social Simple Timeline";
    }

    @Override
    public void init( final SimpleTimelinePresenter presenter ) {
        this.presenter = presenter;
    }


}