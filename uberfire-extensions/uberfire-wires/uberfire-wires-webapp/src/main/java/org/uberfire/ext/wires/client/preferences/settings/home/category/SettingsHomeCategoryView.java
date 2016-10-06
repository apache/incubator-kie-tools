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

package org.uberfire.ext.wires.client.preferences.settings.home.category;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.wires.client.preferences.settings.home.item.SettingsHomeItemPresenter;

@Dependent
@Templated
public class SettingsHomeCategoryView implements IsElement,
                                                 SettingsHomeCategoryPresenter.View {

    private SettingsHomeCategoryPresenter presenter;

    @Inject
    @DataField("content")
    private Div content;

    @Override
    public void init( final SettingsHomeCategoryPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void add( final SettingsHomeItemPresenter.View rootItemView ) {
        content.appendChild( rootItemView.getElement() );
    }
}
