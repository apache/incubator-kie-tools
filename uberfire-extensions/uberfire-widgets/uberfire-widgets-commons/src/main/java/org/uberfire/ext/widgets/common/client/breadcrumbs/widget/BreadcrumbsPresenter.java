/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.breadcrumbs.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class BreadcrumbsPresenter {

    private final View view;
    private PlaceRequest placeRequest;
    private boolean removeDeepLevelBreadcrumbsAfterActivation = true;

    @Inject
    public BreadcrumbsPresenter(final View view) {
        this.view = view;
        view.init(this);
    }

    public void activate() {
        view.activate();
    }

    public void deactivate() {
        view.deactivate();
    }

    public void setup(final String label,
                      final PlaceRequest placeRequest,
                      final Command selectCommand) {
        setup(label,
              placeRequest,
              selectCommand,
              true);
    }

    public void setup(final String label,
                      final PlaceRequest placeRequest,
                      final Command selectCommand,
                      final boolean removeDeepLevelBreadcrumbsAfterActivation) {
        this.placeRequest = placeRequest;
        this.removeDeepLevelBreadcrumbsAfterActivation = removeDeepLevelBreadcrumbsAfterActivation;
        view.setup(label,
                   selectCommand);
    }

    public boolean hasToRemoveDeepLevelBreadcrumbsAfterActivation() {
        return removeDeepLevelBreadcrumbsAfterActivation;
    }

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public UberElement<BreadcrumbsPresenter> getView() {
        return view;
    }

    public interface View extends UberElement<BreadcrumbsPresenter> {

        void setup(String label,
                   Command clickCommand);

        void activate();

        void deactivate();
    }
}