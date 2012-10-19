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

package org.uberfire.client.editors.test7;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.resources.ShowcaseResources;
import org.uberfire.client.workbench.widgets.events.ChangeTabContentEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "Test7")
public class TestPresenter7 {

    public interface View
        extends
        UberView<TestPresenter7> {
    }

    private PlaceRequest                place;

    @Inject
    public UberView<TestPresenter7>     view;

    @Inject
    public Event<ChangeTabContentEvent> changeTabContentEvent;

    public TestPresenter7() {
    }

    @OnStart
    public void OnStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public IsWidget getTitle() {
        return new Image( ShowcaseResources.INSTANCE.images().spinner() );
    }

    @WorkbenchPartView
    public UberView<TestPresenter7> getView() {
        return view;
    }

    public void changeTabContent() {
        changeTabContentEvent.fire( new ChangeTabContentEvent( place,
                                                               new InlineLabel( "Changed" ) ) );
    }

}