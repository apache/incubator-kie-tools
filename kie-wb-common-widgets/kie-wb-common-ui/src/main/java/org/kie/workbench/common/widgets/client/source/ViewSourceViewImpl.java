/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.source;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@Dependent
public class ViewSourceViewImpl
        extends Composite
        implements ViewSourceView {

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private ViewDRLSourceWidget drlSourceViewer;

    @PostConstruct
    public void initialize() {
        initWidget( drlSourceViewer );
    }

    @Override
    public void setContent( final String content ) {
        drlSourceViewer.setContent( content );
    }

    @Override
    public void clear() {
        drlSourceViewer.clearContent();
    }

    @Override
    public void showBusyIndicator( final String message ) {
        busyIndicatorView.showBusyIndicator( message );
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

}
