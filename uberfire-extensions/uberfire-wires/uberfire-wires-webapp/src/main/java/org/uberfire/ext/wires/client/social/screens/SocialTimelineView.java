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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.inject.Inject;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.SocialTimelineWidget;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;

import javax.enterprise.context.Dependent;

@Dependent
@Templated
public class SocialTimelineView implements IsElement, SocialTimelinePresenter.View {

    private SocialTimelinePresenter presenter;
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
    public void setupWidget( SocialTimelineWidgetModel model ) {
        DOMUtil.removeAllChildren( panelContainer );
        final SocialTimelineWidget socialTimelineWidget = new SocialTimelineWidget();
        socialTimelineWidget.init( model );
        DOMUtil.appendWidgetToElement( panelContainer, socialTimelineWidget );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Social Timeline";
    }

    @Override
    public void init( final SocialTimelinePresenter presenter ) {
        this.presenter = presenter;
    }

}