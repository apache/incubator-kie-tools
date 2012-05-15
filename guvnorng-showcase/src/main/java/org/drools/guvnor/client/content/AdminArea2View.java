/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.content;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class AdminArea2View extends Composite implements AdminArea2Presenter.View {

    @Inject UiBinder<Panel, AdminArea2View> uiBinder;
    @UiField Label nameLabel;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setName(final String name) {
        nameLabel.setText(name);
    }

}