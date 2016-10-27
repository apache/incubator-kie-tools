/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.ClientSessionPresenter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class ClientSessionPresenterView
        extends AbstractClientSessionPresenterView
        implements ClientSessionPresenter.View {

    @Inject
    @DataField
    private Label loadingPanel;

    @Inject
    @DataField
    private FlowPanel toolbarPanel;

    @Inject
    @DataField
    private FlowPanel canvasPanel;

    @Inject
    @DataField
    private FlowPanel palettePanel;

    @Override
    public ClientSessionPresenter.View setToolbar( final IsWidget widget ) {
        setWidgetForPanel( toolbarPanel, widget );
        return this;
    }

    @Override
    public ClientSessionPresenter.View setPalette( final IsWidget widget ) {
        setWidgetForPanel( palettePanel, widget );
        return this;
    }

    @Override
    public ClientSessionPresenter.View setCanvas( final IsWidget widget ) {
        setWidgetForPanel( canvasPanel, widget );
        return this;
    }

    @Override
    public ClientSessionPresenter.View setLoading( final boolean loading ) {
        loadingPanel.setVisible( loading );
        return this;
    }

}
