/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.container.empty;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ServerContainerEmptyView extends Composite
        implements ServerContainerEmptyPresenter.View {

    @Inject
    @DataField("empty-server-template-id")
    Span serverTemplateId;

    @Inject
    @DataField("empty-template-add-container")
    Button addContainer;

    private ServerContainerEmptyPresenter presenter;

    @Override
    public void init( final ServerContainerEmptyPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setTemplateName( final String templateName ) {
        serverTemplateId.setText( templateName );
    }

    @EventHandler("empty-template-add-container")
    public void addContainer( final ClickEvent event ) {
        presenter.addContainer();
    }

}
