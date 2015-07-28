/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.social.hp.client.userpage.main;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;

@Dependent
public class MainView extends Composite
        implements MainPresenter.View {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, MainView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    @UiField
    FlowPanel timeline;

    public MainView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setup( final SimpleSocialTimelineWidgetModel model ) {
        timeline.clear();
        SimpleSocialTimelineWidget socialTimelineWidget = new SimpleSocialTimelineWidget( model );
        timeline.add( socialTimelineWidget );

    }
}