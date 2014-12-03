/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.xml;

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetTypeTest {

    // Copied from org.drools.guvnor.client.common.AssetFormats
    public static final String DRL_MODEL = "model.drl";
    public static final String BUSINESS_RULE = "brl";
    public static final String DECISION_TABLE_GUIDED = "gdst";
    public static final String DRL = "drl";
    public static final String FUNCTION = "function";
    public static final String DECISION_SPREADSHEET_XLS = "xls";
    public static final String SCORECARD_SPREADSHEET_XLS = "scxls";
    public static final String SCORECARD_GUIDED = "scgd";
    public static final String TEST_SCENARIO = "scenario";
    public static final String ENUMERATION = "enumeration";
    public static final String DSL = "dsl";
    public static final String DSL_TEMPLATE_RULE = "dslr";
    public static final String RULE_TEMPLATE = "template";
    public static final String FORM_DEFINITION = "formdef";
    public static final String SPRING_CONTEXT = "springContext";
    public static final String SERVICE_CONFIG = "serviceConfig";
    public static final String WORKITEM_DEFINITION = "wid";
    public static final String CHANGE_SET = "changeset";
    public static final String RULE_FLOW_RF = "rf";
    public static final String BPMN_PROCESS = "bpmn";
    public static final String BPMN2_PROCESS = "bpmn2";

    // Other formats, defined in the AssetMigrator, by their extension
    public static final String PNG = "png";
    public static final String GIF = "gif";
    public static final String JPG = "jpg";
    public static final String PDF = "pdf";
    public static final String DOC = "doc";
    public static final String ODT = "odt";
    public static final String FTL = "ftl";
    public static final String JSON = "json";
    public static final String FW = "fw";

    // Test equivalence between AssetFormats (5.6.x) and AssetType enum defined in the xml module
    @Test
    public void testTypeStringEquivalence() {
        assertEquals( AssetType.DRL_MODEL, AssetType.getByType( DRL_MODEL ) );
        assertEquals( AssetType.BUSINESS_RULE, AssetType.getByType( BUSINESS_RULE ) );
        assertEquals( AssetType.DECISION_TABLE_GUIDED, AssetType.getByType( DECISION_TABLE_GUIDED ) );
        assertEquals( AssetType.DRL, AssetType.getByType( DRL ) );
        assertEquals( AssetType.FUNCTION, AssetType.getByType( FUNCTION ) );
        assertEquals( AssetType.DECISION_SPREADSHEET_XLS, AssetType.getByType( DECISION_SPREADSHEET_XLS ) );
        assertEquals( AssetType.SCORECARD_SPREADSHEET_XLS, AssetType.getByType( SCORECARD_SPREADSHEET_XLS ) );
        assertEquals( AssetType.SCORECARD_GUIDED, AssetType.getByType( SCORECARD_GUIDED ) );
        assertEquals( AssetType.TEST_SCENARIO, AssetType.getByType( TEST_SCENARIO ) );
        assertEquals( AssetType.ENUMERATION, AssetType.getByType( ENUMERATION ) );
        assertEquals( AssetType.DSL, AssetType.getByType( DSL ) );
        assertEquals( AssetType.DSL_TEMPLATE_RULE, AssetType.getByType( DSL_TEMPLATE_RULE ) );
        assertEquals( AssetType.RULE_TEMPLATE, AssetType.getByType( RULE_TEMPLATE ) );
        assertEquals( AssetType.FORM_DEFINITION, AssetType.getByType( FORM_DEFINITION ) );
        assertEquals( AssetType.SPRING_CONTEXT, AssetType.getByType( SPRING_CONTEXT ) );
        assertEquals( AssetType.SERVICE_CONFIG, AssetType.getByType( SERVICE_CONFIG ) );
        assertEquals( AssetType.WORKITEM_DEFINITION, AssetType.getByType( WORKITEM_DEFINITION ) );
        assertEquals( AssetType.CHANGE_SET, AssetType.getByType( CHANGE_SET ) );
        assertEquals( AssetType.RULE_FLOW_RF, AssetType.getByType( RULE_FLOW_RF ) );
        assertEquals( AssetType.BPMN_PROCESS, AssetType.getByType( BPMN_PROCESS ) );
        assertEquals( AssetType.BPMN2_PROCESS, AssetType.getByType( BPMN2_PROCESS ) );

        // Other formats, defined in the AssetMigrator, by their extension
        assertEquals( AssetType.PNG, AssetType.getByType( PNG ) );
        assertEquals( AssetType.GIF, AssetType.getByType( GIF ) );
        assertEquals( AssetType.JPG, AssetType.getByType( JPG ) );
        assertEquals( AssetType.PDF, AssetType.getByType( PDF ) );
        assertEquals( AssetType.DOC, AssetType.getByType( DOC ) );
        assertEquals( AssetType.ODT, AssetType.getByType( ODT ) );
        assertEquals( AssetType.FTL, AssetType.getByType( FTL ) );
        assertEquals( AssetType.JSON, AssetType.getByType( JSON ) );
        assertEquals( AssetType.FW, AssetType.getByType( FW ) );
    }
}
