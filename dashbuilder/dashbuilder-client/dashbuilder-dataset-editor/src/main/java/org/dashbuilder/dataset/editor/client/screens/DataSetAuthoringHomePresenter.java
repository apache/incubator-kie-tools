/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.editor.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.editor.client.resources.i18n.DataSetAuthoringConstants;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchScreen(identifier = "DataSetAuthoringHome")
@Dependent
public class DataSetAuthoringHomePresenter {

    public interface View extends UberView<DataSetAuthoringHomePresenter> {

        void setDataSetCount(int n);
    }

    View view;
    PlaceManager placeManager;
    DataSetClientServices clientServices;
    int dataSetCount = 0;

    @Inject
    public DataSetAuthoringHomePresenter(final View view,
                                         final PlaceManager placeManager,
                                         final DataSetClientServices clientServices) {
        this.view = view;
        this.placeManager = placeManager;
        this.clientServices = clientServices;
    }

    @OnStartup
    public void init() {
        clientServices.getPublicDataSetDefs(dataSetDefs -> {
            dataSetCount = dataSetDefs.size();
            view.setDataSetCount(dataSetCount);
        });
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return DataSetAuthoringConstants.INSTANCE.homeTitle();
    }

    @WorkbenchPartView
    public UberView<DataSetAuthoringHomePresenter> getView() {
        return view;
    }

    public void newDataSet() {
        placeManager.goTo("DataSetDefWizard");
    }

    public int getDataSetCount() {
        return dataSetCount;
    }

    // Be aware of data set lifecycle events

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        PortablePreconditions.checkNotNull("DataSetDefRegisteredEvent",
                                           event);
        view.setDataSetCount(++dataSetCount);
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        PortablePreconditions.checkNotNull("DataSetDefRemovedEvent",
                                           event);
        view.setDataSetCount(--dataSetCount);
    }
}
