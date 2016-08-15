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

import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class BreadcrumbsPresenter {

    private DefaultPlaceRequest placeRequest;

    public interface View extends UberElement<BreadcrumbsPresenter> {

        void setup( String label, Command clickCommand );

        void activate();

        void deactivate();
    }
    private final View view;

    @Inject
    public BreadcrumbsPresenter( final View view ) {
        this.view = view;
        view.init( this );
    }

    public void activate() {
        view.activate();
    }

    public void deactivate() {
        view.deactivate();
    }

    public void setup( String label, DefaultPlaceRequest placeRequest, Command selectCommand ) {
        this.placeRequest = placeRequest;
        view.setup( label, selectCommand );
    }

    public DefaultPlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public UberElement<BreadcrumbsPresenter> getView() {
        return view;
    }

}