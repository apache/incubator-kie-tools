/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.common;


/**
 * Keeps track of the different rule formats we support.
 * Each format type corresponds to the dublin core "format" attribute.
 * <p/>
 * This is used both by the UI, to determine what are valid formats, and also on the server.
 * If you are adding new types they need to be registered here.
 * <p/>
 * If an asset type is unknown, then it will be opened with the default editor.
 */
public class AssetFormats {

    /**
     * For functions
     */
    public static final String FUNCTION = "function";

    /**
     * For "model" assets
     */
    public static final String MODEL = "jar";

    /**
     * For DSL language grammars
     */
    public static final String DSL = "dsl";

    /**
     * Vanilla DRL "file"
     */
    public static final String DRL = "drl";

    /**
     * Use the rule modeller
     */
    public static final String BUSINESS_RULE = "brl";


    /**
     * use a DSL, free text editor
     */
    public static final String DSL_TEMPLATE_RULE = "dslr";


    /**
     * Use a decision table.
     */
    public static final String DECISION_SPREADSHEET_XLS = "xls";

    public static final String DECISION_TABLE_GUIDED = "gdst";

    /**
     * Use a ruleflow.
     */
    public static final String RULE_FLOW_RF = "rf";
    public static final String BPMN_PROCESS = "bpmn";
    public static final String BPMN2_PROCESS = "bpmn2";

    /**
     * Use a form definition.
     */
    public static final String FORM_DEFINITION = "formdef";
    
    /**
     * For WorkItems
     */
    public static final String WORKITEM_DEFINITION = "wid";

    /**
     * Use a data enum.
     */
    public static final String ENUMERATION = "enumeration";

    /**
     * For test scenarios.
     */
    public static final String TEST_SCENARIO = "scenario";

    /**
     * Simulation test.
     */
    public static final String SIMULATION_TEST = "simulationTest";

    /**
     * For fact models in drl.
     */
    public static final String DRL_MODEL = "model.drl";

    public static final String XML = "xml";

    public static final String PROPERTIES = "properties";

    public static final String CONFIGURATION = "conf";

    public static final String WORKING_SET = "workingset";

    public static final String RULE_TEMPLATE = "template";

    // commenting so it shows up under other assets, documentation
    // if added back, it needs it own asset configuration section
    // public static final String DOCUMENTATION = "pdf";

    // commenting so it shows up under other assets, documentation
    // if added back, it needs it own asset configuration section
    public static final String ZIP = "zip";

    public static final String SPRING_CONTEXT = "springContext";

    public static final String SERVICE_CONFIG = "serviceConfig";

    public static final String CHANGE_SET = "changeset";
    
    /**
     * The following group the assets together for lists, helpers etc...
     */
    public static final String[] BUSINESS_RULE_FORMATS = new String[]{AssetFormats.BUSINESS_RULE, AssetFormats.DSL_TEMPLATE_RULE, AssetFormats.DECISION_SPREADSHEET_XLS, AssetFormats.DECISION_TABLE_GUIDED, AssetFormats.RULE_TEMPLATE};
    /**
     * These define assets that are really package level "things". Used to decide when to flush any caches.
     */
    private static final String[] PACKAGE_DEPENCENCIES = new String[]{AssetFormats.FUNCTION, AssetFormats.DSL, AssetFormats.MODEL, AssetFormats.ENUMERATION, AssetFormats.DRL_MODEL, AssetFormats.WORKING_SET};

    /**
     * These define assets that can be added as a resource definition inside a change-set
     */
    public static final String[] CHANGE_SET_RESOURCE = new String[]{AssetFormats.BUSINESS_RULE, AssetFormats.DRL, AssetFormats.DSL, AssetFormats.BPMN2_PROCESS, AssetFormats.DECISION_TABLE_GUIDED, AssetFormats.RULE_TEMPLATE, AssetFormats.CHANGE_SET};

    /**
     * These define assets that can be added as resource to service config
     */
    public static final String[] SERVICE_CONFIG_RESOURCE = new String[]{AssetFormats.BUSINESS_RULE, AssetFormats.DRL, AssetFormats.DSL, AssetFormats.BPMN2_PROCESS, AssetFormats.DECISION_TABLE_GUIDED, AssetFormats.CHANGE_SET, AssetFormats.MODEL};

    /**
     * Will return true if the given asset format is a package dependency (eg a function, DSL, model etc).
     * Package dependencies are needed before the package is validated, and any rule assets are processed.
     */
    public static boolean isPackageDependency(String format) {
        for (String dep : PACKAGE_DEPENCENCIES) {
            if (dep.equals(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param format never null
     * @return true if any changes to this asset make the module's binary file stale
     */
    public static boolean affectsBinaryUpToDate(String format) {
        return !format.equals(AssetFormats.TEST_SCENARIO) && !format.equals(AssetFormats.SIMULATION_TEST);
    }

    public static String convertAssetFormatToResourceType(final String format) {
        if (format.equals(BUSINESS_RULE)
                || format.equals(DRL)
                || format.equals(DECISION_TABLE_GUIDED)
                || format.equals(RULE_TEMPLATE)) {
            return "DRL";
        } else if (format.equals(DSL)) {
            return "DSL";
        } else if (format.equals(BPMN2_PROCESS)) {
            return "BPMN2";
        } else if (format.equals(CHANGE_SET)) {
            return "CHANGE_SET";
        }

        return null;
    }

}
