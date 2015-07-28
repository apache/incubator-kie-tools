/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdvancedDomainScreenViewImpl
        extends Composite
        implements AdvancedDomainScreenView {

    interface Binder
            extends UiBinder<Widget, AdvancedDomainScreenViewImpl> {

    }

    ;

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FlowPanel containerPanel;

    private AdvancedDomainEditor domainEditor;

    public AdvancedDomainScreenViewImpl() {
    }

    @Inject
    public AdvancedDomainScreenViewImpl( AdvancedDomainEditor domainEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        //TODO review this, if use the normal injection I get an error because of the property editor.
        this.domainEditor = domainEditor;
    }

    @PostConstruct
    private void init() {
        containerPanel.add( domainEditor );
    }
}
