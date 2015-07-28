/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.SimpleSocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class SocialHomePageSideView extends Composite implements SocialHomePageSidePresenter.View {


    interface SocialHomePageSideViewBinder
            extends
            UiBinder<Widget, SocialHomePageSideView> {
    }

    private static SocialHomePageSideViewBinder uiBinder = GWT.create( SocialHomePageSideViewBinder.class );

    @UiField
    Column column;

    private SocialHomePageSidePresenter presenter = null;

    @Inject
    PlaceManager placeManager;

    @AfterInitialization
    public void setup() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final SocialHomePageSidePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupWidget( SimpleSocialTimelineWidgetModel model ) {
        column.clear();
        column.add( new SimpleSocialTimelineWidget( model ) );
    }

}
