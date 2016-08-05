/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.explorer.common;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DefItemViewImpl
        extends Composite
        implements DefItemView {

    @Inject
    @DataField( "item" )
    private LinkedGroupItem item;

    Presenter presenter;

    public DefItemViewImpl() {
    }

    @Override
    public void setName( String name ) {
        item.setText( name );
    }

    @Override
    public String getName() {
        return item.getText();
    }

    @Override
    public void init( DefItemView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @EventHandler( "item" )
    private void onItemClick( ClickEvent event ) {
        presenter.onClick();
    }

}
