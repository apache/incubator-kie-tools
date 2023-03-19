/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;

import static org.dashbuilder.perspectives.PerspectiveIds.CONTENT_MANAGER;
import static org.dashbuilder.perspectives.PerspectiveIds.DATA_SETS;
import static org.dashbuilder.perspectives.PerspectiveIds.DATA_TRANSFER;
import static org.dashbuilder.perspectives.PerspectiveIds.GALLERY;

@WorkbenchScreen(identifier = HomeScreen.ID)
public class HomeScreen {

    public static final String ID = "HomeScreen";

    @Inject
    View view;

    @Inject
    PlaceManager placeManager;

    public interface View extends UberElemental<HomeScreen> {

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartTitle
    public String title() {
        return "Home Screen";
    }

    @WorkbenchPartView
    public View root() {
        return view;
    }

    public void goToSample() {
        go(GALLERY);
    }

    public void goToDataset() {
        go(DATA_SETS);
    }

    public void goToDesign() {
        go(CONTENT_MANAGER);
    }

    public void goToTransfer() {
        go(DATA_TRANSFER);
    }

    public void go(String perspectiveId) {
        placeManager.goTo(perspectiveId);
    }

}