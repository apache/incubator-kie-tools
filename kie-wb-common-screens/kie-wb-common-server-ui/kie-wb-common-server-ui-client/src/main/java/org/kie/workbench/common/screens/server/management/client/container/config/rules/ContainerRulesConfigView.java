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

package org.kie.workbench.common.screens.server.management.client.container.config.rules;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.client.util.StyleHelper;
import org.kie.workbench.common.screens.server.management.client.widget.NumericTextBox;

@Dependent
@Templated
public class ContainerRulesConfigView extends Composite
        implements ContainerRulesConfigPresenter.View {

    private ContainerRulesConfigPresenter presenter;

    private TranslationService translationService;

    @DataField("container-config-alert-span")
    Element alert = DOM.createSpan();

    @DataField("container-config-scanner-form")
    Element scannerForm = DOM.createDiv();

    @DataField("container-config-scanner-label")
    Element scannerLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-interval-textbox")
    NumericTextBox interval;

    @Inject
    @DataField("container-config-stop-scanner")
    Button stopScanner;

    @Inject
    @DataField("container-config-start-scanner")
    Button startScanner;

    @Inject
    @DataField("container-config-scan-now")
    Button scanNow;

    @DataField("container-config-version-form")
    Element versionForm = DOM.createDiv();

    @DataField("container-config-version-label")
    Element versionLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-version-textbox")
    TextBox version;

    @Inject
    @DataField("container-config-upgrade-button")
    Button upgrade;

    @Inject
    public ContainerRulesConfigView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    private org.uberfire.mvp.Command stopScannerActive = new org.uberfire.mvp.Command() {
        @Override
        public void execute() {
            Scheduler.get().scheduleDeferred( new Command() {
                @Override
                public void execute() {
                    stopScanner.setActive( true );
                    startScanner.setActive( false );
                }
            } );
        }
    };

    @Override
    public void init( final ContainerRulesConfigPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        version.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !version.getText().trim().isEmpty() ) {
                    StyleHelper.addUniqueEnumStyleName( versionForm, ValidationState.class, ValidationState.NONE );
                }
            }
        } );
        version.getElement().setAttribute( "placeholder", getVersionTextBoxPlaceholder() );

        interval.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if ( !interval.getText().trim().isEmpty() ) {
                    StyleHelper.addUniqueEnumStyleName( scannerForm, ValidationState.class, ValidationState.NONE );
                }
            }
        } );
        interval.getElement().setAttribute( "placeholder", getIntervalTextBoxPlaceholder() );
        interval.getElement().setAttribute( "data-original-title", getIntervalTextBoxDataOriginalTitle() );
    }

    @Override
    public void setContent( final String interval,
                            final String version,
                            final State startScanner,
                            final State stopScanner,
                            final State scanNow,
                            final State upgrade ) {
        StyleHelper.addUniqueEnumStyleName( scannerForm, ValidationState.class, ValidationState.NONE );
        StyleHelper.addUniqueEnumStyleName( versionForm, ValidationState.class, ValidationState.NONE );

        if ( interval == null || interval.trim().isEmpty() ) {
            this.interval.setText( "" );
        } else {
            try {
                this.interval.setText( Integer.valueOf( interval ).toString() );
            } catch ( final NumberFormatException ex ) {
                this.interval.setText( "" );
            }
        }
        this.version.setText( version != null ? version : "" );
        this.interval.setText( interval );

        setStartScannerState( startScanner );
        setStopScannerState( stopScanner );
        setScanNowState( scanNow );
        setUpgradeState( upgrade );
    }

    @Override
    public void setStartScannerState( final State state ) {
        this.startScanner.setEnabled( state.equals( State.ENABLED ) );
        this.startScanner.setActive( !state.equals( State.ENABLED ) );
    }

    @Override
    public void setStopScannerState( final State state ) {
        this.stopScanner.setEnabled( state.equals( State.ENABLED ) );
        this.stopScanner.setActive( !state.equals( State.ENABLED ) );
    }

    @Override
    public void setScanNowState( final State state ) {
        this.scanNow.setEnabled( state.equals( State.ENABLED ) );
    }

    @Override
    public void setUpgradeState( final State state ) {
        this.upgrade.setEnabled( state.equals( State.ENABLED ) );
    }

    @Override
    public void disableActions() {
        startScanner.setEnabled( false );
        scanNow.setEnabled( false );
        stopScanner.setEnabled( false );
        upgrade.setEnabled( false );
    }

    @Override
    public void errorOnInterval() {
        startScanner.setEnabled( true );
        startScanner.setActive( false );
        StyleHelper.addUniqueEnumStyleName( scannerForm, ValidationState.class, ValidationState.ERROR );
        interval.setFocus( true );
    }

    @Override
    public String getInterval() {
        return interval.getText();
    }

    @Override
    public String getVersion() {
        return version.getText();
    }

    @Override
    public void setVersion( final String version ) {
        this.version.setText( version );
    }

    @EventHandler("container-config-start-scanner")
    public void startScanner( final ClickEvent event ) {
        if ( startScanner.isActive() ) {
            return;
        }

        try {
            presenter.startScanner( getInterval() );
        } catch ( final IllegalArgumentException ex ) {
            StyleHelper.addUniqueEnumStyleName( scannerForm, ValidationState.class, ValidationState.ERROR );
            stopScannerActive.execute();
        }
    }

    @EventHandler("container-config-stop-scanner")
    public void stopScanner( final ClickEvent e ) {
        if ( stopScanner.isActive() ) {
            return;
        }
        stopScannerActive.execute();
        presenter.stopScanner();
    }

    @EventHandler("container-config-scan-now")
    public void scanNow( final ClickEvent e ) {
        stopScannerActive.execute();
        presenter.scanNow();
    }

    @EventHandler("container-config-upgrade-button")
    public void upgrade( final ClickEvent e ) {
        try {
            presenter.upgrade( version.getText() );
        } catch ( final IllegalArgumentException ex ) {
            StyleHelper.addUniqueEnumStyleName( versionForm, ValidationState.class, ValidationState.ERROR );
        }
    }

    @Override
    public String getStartScannerErrorMessage() {
        return translationService.format( Constants.ContainerRulesConfigView_StartScannerErrorMessage );
    }

    @Override
    public String getStopScannerErrorMessage() {
        return translationService.format( Constants.ContainerRulesConfigView_StopScannerErrorMessage );
    }

    @Override
    public String getScanNowErrorMessage() {
        return translationService.format( Constants.ContainerRulesConfigView_ScanNowErrorMessage );
    }

    @Override
    public String getUpgradeErrorMessage() {
        return translationService.format( Constants.ContainerRulesConfigView_UpgradeErrorMessage );
    }

    private String getVersionTextBoxPlaceholder() {
        return translationService.format( Constants.ContainerRulesConfigView_VersionTextBoxPlaceholder );
    }

    private String getIntervalTextBoxPlaceholder() {
        return translationService.format( Constants.ContainerRulesConfigView_IntervalTextBoxPlaceholder );
    }

    private String getIntervalTextBoxDataOriginalTitle() {
        return translationService.format( Constants.ContainerRulesConfigView_IntervalTextBoxDataOriginalTitle );
    }
}
