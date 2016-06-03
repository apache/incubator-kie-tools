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

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SaveAllMenuViewImpl extends Composite implements SaveAllMenuView {

    @DataField("saveAllMenuButton")
    ButtonElement saveAllMenuButton = Document.get().createPushButtonElement();

    protected SaveAllMenuBuilder presenter;

    @Override
    public void init( final SaveAllMenuBuilder presenter ) {
        this.presenter = presenter;
    }

    @Override
    public boolean isEnabled() {
        return !saveAllMenuButton.isDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        saveAllMenuButton.setDisabled( !enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("saveAllMenuButton")
    public void onClickSaveAllButton( final ClickEvent e ) {
        presenter.onSaveAll();
    }

}
