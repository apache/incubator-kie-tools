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

package org.kie.workbench.common.screens.server.management.client.container.config.process;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;

@Dependent
@Templated
public class ContainerProcessConfigView extends Composite
        implements ContainerProcessConfigPresenter.View {

    private ContainerProcessConfigPresenter presenter;

    private TranslationService translationService;

    @Inject
    @DataField("form-definition")
    FieldSet form;

    @Inject
    @DataField("container-config-save-button")
    Button save;

    @Inject
    @DataField("container-config-cancel-button")
    Button cancel;

    @Inject
    public ContainerProcessConfigView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final ContainerProcessConfigPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setProcessConfigView( final ProcessConfigPresenter.View view ) {
        form.add( view );
    }

    @Override
    public void disable() {
        disableActions();
    }

    @Override
    public void disableActions() {
        save.setEnabled( false );
        cancel.setEnabled( false );
    }

    @Override
    public void enableActions() {
        this.save.setEnabled( true );
        this.cancel.setEnabled( true );
    }

    @EventHandler("container-config-save-button")
    public void onSave( final ClickEvent event ) {
        presenter.save();
    }

    @EventHandler("container-config-cancel-button")
    public void onCancel( final ClickEvent event ) {
        presenter.cancel();
    }

    @Override
    public String getSaveSuccessMessage() {
        return translationService.format( Constants.ContainerProcessConfigView_SaveSuccessMessage );
    }

    @Override
    public String getSaveErrorMessage() {
        return translationService.format( Constants.ContainerProcessConfigView_SaveErrorMessage );
    }
}
