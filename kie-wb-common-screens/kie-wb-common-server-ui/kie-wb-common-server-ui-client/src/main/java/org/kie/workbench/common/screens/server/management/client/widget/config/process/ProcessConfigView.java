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

package org.kie.workbench.common.screens.server.management.client.widget.config.process;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;

@Dependent
@Templated
public class ProcessConfigView extends Composite
        implements ProcessConfigPresenter.View {

    private ProcessConfigPresenter presenter;

    private TranslationService translationService;

    @DataField("container-config-runtime-strategy-label")
    Element runtimeStrategyLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-runtime-strategy-button")
    Button runtimeStrategy;

    @Inject
    @DataField("container-config-runtime-strategy-dropdown-menu")
    DropDownMenu runtimeStrategyDropdown;

    @DataField("container-config-kbase-name-form")
    Element kbaseForm = DOM.createDiv();

    @DataField("container-config-kbase-name-label")
    Element kbaseLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-kbase-name-textbox")
    TextBox kbase;

    @DataField("container-config-ksession-name-form")
    Element ksessionForm = DOM.createDiv();

    @DataField("container-config-ksession-name-label")
    Element ksessionLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-ksession-name-textbox")
    TextBox ksession;

    @DataField("container-config-merge-mode-label")
    Element mergeModeLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-merge-mode-button")
    Button mergeMode;

    @Inject
    @DataField("container-config-merge-mode-dropdown-menu")
    DropDownMenu mergeModeDropdown;

    @Inject
    public ProcessConfigView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final ProcessConfigPresenter presenter ) {
        this.presenter = presenter;

        final List<String> runtimeStrategies = presenter.getRuntimeStrategies();

        runtimeStrategy.setText(ClientRuntimeStrategy.PER_PROCESS_INSTANCE.getValue(translationService));
        for ( final String strategy : runtimeStrategies ) {
            runtimeStrategyDropdown.add( new AnchorListItem( strategy ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        runtimeStrategy.setText( strategy );
                    }
                } );
            }} );
        }

        final List<String> mergeModes = presenter.getMergeModes();

        mergeMode.setText( mergeModes.get( 0 ) );
        for ( final String merge : mergeModes ) {
            mergeModeDropdown.add( new AnchorListItem() {{
                setText( merge );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        mergeMode.setText( merge );
                    }
                } );
            }} );
        }
    }

    @Override
    public void setContent( final String runtimeStrategy,
                            final String kbase,
                            final String ksession,
                            final String mergeMode ) {
        this.runtimeStrategy.setText( runtimeStrategy );
        this.kbase.setText( kbase );
        this.ksession.setText( ksession );
        this.mergeMode.setText( mergeMode );

        this.runtimeStrategy.setEnabled( true );
        this.kbase.setEnabled( true );
        this.ksession.setEnabled( true );
        this.mergeMode.setEnabled( true );
    }

    @Override
    public String getRuntimeStrategy() {
        return runtimeStrategy.getText();
    }

    @Override
    public void disable() {
        runtimeStrategy.setEnabled( false );
        kbase.setEnabled( false );
        ksession.setEnabled( false );
        mergeMode.setEnabled( false );
    }

    @Override
    public void clear() {
        kbase.setText( "" );
        ksession.setText( "" );
        runtimeStrategy.setText( presenter.getRuntimeStrategies().get( 0 ) );
        mergeMode.setText( presenter.getMergeModes().get( 0 ) );
    }

    @Override
    public String getKBase() {
        return kbase.getText();
    }

    @Override
    public String getKSession() {
        return ksession.getText();
    }

    @Override
    public String getMergeMode() {
        return mergeMode.getText();
    }

    @Override
    public TranslationService getTranslationService() {
        return translationService;
    }

    @Override
    public String getConfigPageTitle() {
        return translationService.format( Constants.ProcessConfigView_ConfigPageTitle );
    }
}
