/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.mockito.Mock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerFromFileTest {

    @GwtMock
    AnalysisConstants analysisConstants;
    
    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Mock
    AsyncPackageDataModelOracle oracle;

    private AnalysisReport analysisReport;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    @Test
    public void testFile1() throws Exception {
        String xml = loadResource( "Pricing loans.gdst" );

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( GuidedDTXMLPersistence.getInstance().unmarshal( xml ) );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertTrue( analysisReport.getAnalysisData().isEmpty() );

    }

    private DecisionTableAnalyzer getDecisionTableAnalyzer( GuidedDecisionTable52 table52 ) {
        return new DecisionTableAnalyzer( mock( PlaceRequest.class ),
                                          oracle,
                                          table52,
                                          mock( EventBus.class ) ) {
            @Override
            protected void sendReport( AnalysisReport report ) {
                analysisReport = report;
            }
        };
    }

    public static String loadResource( final String name ) throws Exception {
        final InputStream in = DecisionTableAnalyzerFromFileTest.class.getResourceAsStream( name );
        final Reader reader = new InputStreamReader( in );
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[ 1024 ];
        int len = 0;
        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

}