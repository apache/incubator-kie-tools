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
package org.drools.workbench.jcr2vfsmigration.xml.model.asset;

public enum AssetType {

    // Plain text assets
    ENUMERATION( "enumeration" ),
    DSL( "dsl" ),
    DSL_TEMPLATE_RULE( "dslr" ),
    RULE_TEMPLATE( "template" ),
    FORM_DEFINITION( "formdef" ),
    SPRING_CONTEXT( "springContext" ),
    SERVICE_CONFIG( "serviceConfig" ),
    WORKITEM_DEFINITION( "wid" ),
    CHANGE_SET( "changeset" ),
    RULE_FLOW_RF( "rf" ),
    BPMN_PROCESS( "bpmn" ),
    BPMN2_PROCESS( "bpmn2" ),
    FTL( "ftl" ),
    JSON( "json" ),
    FW( "fw" );


    private String type;

    private AssetType( String type ) {
        this.type = type;
    }

    public static AssetType getByName( String name ) {
        if (name == null || name.length() == 0) return null;
        return valueOf(name.toUpperCase());
    }

//    XmlFormat<T> getAssetFormatForType( AssetType assetType ) {
//
//    }


//    public static XXX createDataSetDef(DataSetProviderType type) {
//        switch (type) {
//            case STATIC: return new StaticDataSetDef();
//            case BEAN: return new BeanDataSetDef();
//            case CSV: return new CSVDataSetDef();
//        }
//        throw new RuntimeException("Unknown type: " + type);
//    }

}
/*
public static final String FUNCTION = "function";
public static final String MODEL = "jar";
    public static final String DSL = "dsl";
public static final String DRL = "drl";
public static final String BUSINESS_RULE = "brl";
    public static final String DSL_TEMPLATE_RULE = "dslr";
public static final String DECISION_SPREADSHEET_XLS = "xls";
public static final String DECISION_TABLE_GUIDED = "gdst";
public static final String SCORECARD_SPREADSHEET_XLS = "scxls";
public static final String SCORECARD_GUIDED = "scgd";
    public static final String RULE_FLOW_RF = "rf";
    public static final String BPMN_PROCESS = "bpmn";
    public static final String BPMN2_PROCESS = "bpmn2";
    public static final String FORM_DEFINITION = "formdef";
    public static final String WORKITEM_DEFINITION = "wid";
    public static final String ENUMERATION = "enumeration";
public static final String TEST_SCENARIO = "scenario";
public static final String SIMULATION_TEST = "simulationTest";
public static final String DRL_MODEL = "model.drl";
public static final String XML = "xml";
public static final String PROPERTIES = "properties";
public static final String CONFIGURATION = "conf";
public static final String WORKING_SET = "workingset";
    public static final String RULE_TEMPLATE = "template";
// public static final String DOCUMENTATION = "pdf";
public static final String ZIP = "zip";
    public static final String SPRING_CONTEXT = "springContext";
    public static final String SERVICE_CONFIG = "serviceConfig";
    public static final String CHANGE_SET = "changeset";
*/
