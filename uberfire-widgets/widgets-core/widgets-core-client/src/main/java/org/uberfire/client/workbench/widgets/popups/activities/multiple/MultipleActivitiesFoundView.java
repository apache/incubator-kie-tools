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
package org.uberfire.client.workbench.widgets.popups.activities.multiple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.uberfire.client.mvp.Activity;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *
 */
public class MultipleActivitiesFoundView extends SimplePanel
        implements
        MultipleActivitiesFoundPresenter.View {

    interface MultipleActivitiesFoundViewBinder
            extends
            UiBinder<Widget, MultipleActivitiesFoundView> {

    }

    private static MultipleActivitiesFoundViewBinder uiBinder = GWT.create(MultipleActivitiesFoundViewBinder.class);

    private MultipleActivitiesFoundPresenter presenter;

    @UiField
    public Label requestedPlaceIdentifierLabel;

    @UiField
    VerticalPanel activitiesList;

    @PostConstruct
    public void init() {
        setWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final MultipleActivitiesFoundPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setRequestedPlaceIdentifier(String requestedPlaceIdentifier) {
        requestedPlaceIdentifierLabel.setText(requestedPlaceIdentifier);
    }

    @Override
    public void setActivities(List<Activity> activities) {
        for (final Activity activity : activities) {
            Button button = new Button(activity.getSignatureId());
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.activitySelected(activity);
                }
            });
            activitiesList.add(button);
        }
    }

    @UiHandler("okButton")
    public void onClickOkButton(final ClickEvent event) {
        presenter.close();
    }

}
