/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
public class SaveAllMenuBuilder implements MenuFactory.CustomMenuBuilder,
                                           SaveAllMenuView.Presenter {

    private SaveAllMenuView view;

    private Command saveAllCommand;

    @Inject
    public SaveAllMenuBuilder( final SaveAllMenuView view ) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init( this );
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return view;
            }

            @Override
            public boolean isEnabled() {
                return view.isEnabled();
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                view.setEnabled( enabled );
            }

            @Override
            public String getSignatureId() {
                return "org.kie.workbench.common.widgets.metadata.client.menu.SaveAllMenuBuilder";
            }

        };
    }

    @Override
    public void setSaveAllCommand( final Command saveAllCommand ) {
        this.saveAllCommand = saveAllCommand;
    }

    @Override
    public void onSaveAll() {
        if ( saveAllCommand != null ) {
            saveAllCommand.execute();
        }
    }

}
