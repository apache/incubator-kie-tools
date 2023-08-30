/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

@Templated
public class FieldHelpViewImpl implements IsElement,
                                          FieldHelpView {

    private Presenter presenter;

    @Inject
    @DataField
    private Anchor helpMessage;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Override
    public void showHelpMessage(String helpMessage) {
        this.helpMessage.setAttribute("data-content",
                                      helpMessage);

        jQueryPopover.wrap(this.helpMessage).popover();
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }
}
