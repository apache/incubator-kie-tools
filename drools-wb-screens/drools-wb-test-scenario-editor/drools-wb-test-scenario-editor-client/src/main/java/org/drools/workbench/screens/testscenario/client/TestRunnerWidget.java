/*
 * Copyright 2010 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.BuilderResultLine;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.AuditEventsImages;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.SmallLabel;

public class TestRunnerWidget extends Composite implements HasBusyIndicator {

    private FlexTable results = new FlexTable();
    private VerticalPanel layout = new VerticalPanel();
    private SimplePanel actions = new SimplePanel();

    public TestRunnerWidget( final Scenario scenario,
                             final Caller<ScenarioTestEditorService> testScenarioEditorService,
                             final Path path ) {

        final Button run = new Button( TestScenarioConstants.INSTANCE.RunScenario() );
        run.setTitle( TestScenarioConstants.INSTANCE.RunScenarioTip() );
        run.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                testScenarioEditorService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void v ) {
                        BusyPopup.close();
                        layout.clear();
                        layout.add( actions );
                        layout.add( results );
                        actions.setVisible( true );
                    }
                }, new HasBusyIndicatorDefaultErrorCallback( TestRunnerWidget.this ) ).runScenario( path,
                                                                                                    scenario );
            }
        } );

        actions.add( run );
        layout.add( actions );
        initWidget( layout );
    }

    private void showErrors( final List<BuilderResultLine> rs ) {
        results.clear();
        results.setVisible( true );

        FlexTable errTable = new FlexTable();
        errTable.setStyleName( "build-Results" );
        for ( int i = 0; i < rs.size(); i++ ) {
            int row = i;
            final BuilderResultLine res = rs.get( i );
            errTable.setWidget( row,
                                0,
                                new Image( CommonImages.INSTANCE.error() ) );
            if ( res.getAssetFormat().equals( "package" ) ) {
                errTable.setText( row,
                                  1,
                                  TestScenarioConstants.INSTANCE.packageConfigurationProblem1() + res.getMessage() );
            } else {
                errTable.setText( row,
                                  1,
                                  "[" + res.getAssetName() + "] " + res.getMessage() );
            }

        }
        ScrollPanel scroll = new ScrollPanel( errTable );

        scroll.setWidth( "100%" );
        results.setWidget( 0,
                           0,
                           scroll );

    }

//    private void showResults(final ScenarioEditorPresenter parent,
//                             final SingleScenarioResult data) {
//        results.clear();
//        results.setVisible(true);
//
//        parent.setScenario(data.getResult().getScenario());
//
//        parent.setShowResults(true);
//        parent.renderEditor();
//
//        int failures = 0;
//        int total = 0;
//        VerticalPanel resultsDetail = new VerticalPanel();
//
//        for (Iterator<Fixture> fixturesIterator = data.getResult().getScenario().getFixtures().iterator(); fixturesIterator.hasNext(); ) {
//            Fixture fixture = fixturesIterator.next();
//            if (fixture instanceof VerifyRuleFired) {
//
//                VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
//                HorizontalPanel panel = new HorizontalPanel();
//                if (!verifyRuleFired.getSuccessResult().booleanValue()) {
//                    panel.add(new Image(CommonImages.INSTANCE.warning()));
//                    failures++;
//                } else {
//                    panel.add(new Image(TestScenarioImages.INSTANCE.testPassed()));
//                }
//                panel.add(new SmallLabel(verifyRuleFired.getExplanation()));
//                resultsDetail.add(panel);
//                total++;
//            } else if (fixture instanceof VerifyFact) {
//                VerifyFact verifyFact = (VerifyFact) fixture;
//                for (Iterator<VerifyField> fieldIterator = verifyFact.getFieldValues().iterator(); fieldIterator.hasNext(); ) {
//                    total++;
//                    VerifyField verifyField = fieldIterator.next();
//                    HorizontalPanel panel = new HorizontalPanel();
//                    if (!verifyField.getSuccessResult().booleanValue()) {
//                        panel.add(new Image(CommonImages.INSTANCE.warning()));
//                        failures++;
//                    } else {
//                        panel.add(new Image(TestScenarioImages.INSTANCE.testPassed()));
//                    }
//                    panel.add(new SmallLabel(verifyField.getExplanation()));
//                    resultsDetail.add(panel);
//                }
//
//            } else if (fixture instanceof ExecutionTrace) {
//                ExecutionTrace ex = (ExecutionTrace) fixture;
//                if (ex.getNumberOfRulesFired() == data.getResult().getScenario().getMaxRuleFirings()) {
//                    Window.alert(TestScenarioConstants.INSTANCE.MaxRuleFiringsReachedWarning(
//                            data.getResult().getScenario().getMaxRuleFirings()));
//                }
//            }
//
//        }
//
//        results.setWidget(0,
//                0,
//                new SmallLabel(TestScenarioConstants.INSTANCE.Results()));
//        results.getFlexCellFormatter().setHorizontalAlignment(0,
//                0,
//                HasHorizontalAlignment.ALIGN_RIGHT);
//        if (failures > 0) {
//            results.setWidget(0,
//                    1,
//                    new PercentageBar("#CC0000",
//                            150,
//                            failures,
//                            total));
//        } else {
//            results.setWidget(0,
//                    1,
//                    new PercentageBar("GREEN",
//                            150,
//                            failures,
//                            total));
//        }
//
//        results.setWidget(1,
//                0,
//                new SmallLabel(TestScenarioConstants.INSTANCE.SummaryColon()));
//        results.getFlexCellFormatter().setHorizontalAlignment(1,
//                0,
//                HasHorizontalAlignment.ALIGN_RIGHT);
//        results.setWidget(1,
//                1,
//                resultsDetail);
//        results.setWidget(2,
//                0,
//                new SmallLabel(TestScenarioConstants.INSTANCE.AuditLogColon()));
//
//        final Button showExp = new Button(TestScenarioConstants.INSTANCE.ShowEventsButton());
//        results.setWidget(2,
//                1,
//                showExp);
//        showExp.addClickHandler(new ClickHandler() {
//
//            public void onClick(ClickEvent event) {
//                showExp.setVisible(false);
//                results.setWidget(2,
//                        1,
//                        doAuditView(data.getAuditLog()));
//            }
//        });
//
//    }

    private Widget doAuditView( final List<String[]> auditLog ) {
        VerticalPanel vp = new VerticalPanel();
        vp.add( new HTML( "<hr/>" ) );
        FlexTable g = new FlexTable();
        int row = 0;
        boolean firing = false;
        for ( int i = 0; i < auditLog.size(); i++ ) {
            String[] lg = auditLog.get( i );

            int id = Integer.parseInt( lg[ 0 ] );
            if ( id <= 7 ) {
                if ( id <= 3 ) {
                    if ( !firing ) {
                        g.setWidget( row,
                                     0,
                                     getEventImage( lg[ 0 ] ) );
                        g.setWidget( row,
                                     1,
                                     new SmallLabel( lg[ 1 ] ) );
                    } else {
                        g.setWidget( row,
                                     1,
                                     hz( getEventImage( lg[ 0 ] ),
                                         new SmallLabel( lg[ 1 ] ) ) );
                    }
                    row++;
                } else if ( id == 6 ) {
                    firing = true;
                    g.setWidget( row,
                                 0,
                                 getEventImage( lg[ 0 ] ) );
                    g.setWidget( row,
                                 1,
                                 new SmallLabel( "<b>" + lg[ 1 ] + "</b>" ) );
                    row++;
                } else if ( id == 7 ) {
                    firing = false;
                } else {
                    g.setWidget( row,
                                 0,
                                 getEventImage( lg[ 0 ] ) );
                    g.setWidget( row,
                                 1,
                                 new SmallLabel( "<font color='grey'>" + lg[ 1 ] + "</font>" ) );
                    row++;
                }
            } else {
                g.setWidget( row,
                             0,
                             new Image( AuditEventsImages.INSTANCE.miscEvent() ) );
                g.setWidget( row,
                             1,
                             new SmallLabel( "<font color='grey'>" + lg[ 1 ] + "</font>" ) );
                row++;
            }
        }
        vp.add( g );
        vp.add( new HTML( "<hr/>" ) );
        return vp;
    }

    private Widget hz( final Image image,
                       final SmallLabel smallLabel ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( image );
        h.add( smallLabel );
        return h;
    }

    private Image getEventImage( final String eventType ) {
        int type;

        try {
            type = Integer.parseInt( eventType );
        } catch ( NumberFormatException e ) {
            return new Image( AuditEventsImages.INSTANCE.miscEvent() );
        }

        switch ( type ) {
            case 1:
                return new Image( AuditEventsImages.INSTANCE.image1() );
            case 2:
                return new Image( AuditEventsImages.INSTANCE.image2() );
            case 3:
                return new Image( AuditEventsImages.INSTANCE.image3() );
            case 4:
                return new Image( AuditEventsImages.INSTANCE.image4() );
            case 5:
                return new Image( AuditEventsImages.INSTANCE.image5() );
            case 6:
                return new Image( AuditEventsImages.INSTANCE.image6() );
            case 7:
                return new Image( AuditEventsImages.INSTANCE.image7() );
            default:
                return new Image( AuditEventsImages.INSTANCE.miscEvent() );
        }
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
