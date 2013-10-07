/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.widgets.client.datamodel;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;

public class AsyncPackageDataModelOracleUtilities {

    public static void populateDataModelOracle( final AsyncPackageDataModelOracle oracle,
                                                final PackageDataModelOracleBaselinePayload payload ) {
        populate( oracle,
                  payload );
        oracle.filter();
    }

    public static void populateDataModelOracle( final AsyncPackageDataModelOracle oracle,
                                                final PackageDataModelOracleIncrementalPayload payload ) {
        populate( oracle,
                  payload );
        oracle.filter();
    }

    public static void populateDataModelOracle( final HasImports hasImports,
                                                final AsyncPackageDataModelOracle oracle,
                                                final PackageDataModelOracleBaselinePayload payload ) {
        populate( oracle,
                  payload );
        oracle.filter( hasImports.getImports() );
    }

    public static void populateDataModelOracle( final HasImports hasImports,
                                                final AsyncPackageDataModelOracle oracle,
                                                final PackageDataModelOracleIncrementalPayload payload ) {
        populate( oracle,
                  payload );
        oracle.filter( hasImports.getImports() );
    }

    private static void populate( final AsyncPackageDataModelOracle oracle,
                                  final PackageDataModelOracleBaselinePayload payload ) {
        oracle.setProjectName( payload.getProjectName() );
        oracle.addFactsAndFields( payload.getModelFields() );
        oracle.addRuleNames( payload.getRuleNames() );
        oracle.addFieldParametersType( payload.getFieldParametersType() );
        oracle.addEventTypes( payload.getEventTypes() );
        oracle.addTypeSources( payload.getTypeSources() );
        oracle.addSuperTypes( payload.getSuperTypes() );
        oracle.addTypeAnnotations( payload.getTypeAnnotations() );
        oracle.addTypeFieldsAnnotations( payload.getTypeFieldsAnnotations() );
        oracle.addEnumDefinitions( payload.getJavaEnumLists() );
        oracle.addMethodInformation( payload.getMethodInformation() );
        oracle.addCollectionTypes( payload.getCollectionTypes() );
        oracle.addPackageNames( payload.getPackageNames() );

        oracle.setPackageName( payload.getPackageName() );
        oracle.addWorkbenchEnums( payload.getWorkbenchEnumLists() );
        oracle.addDslConditionSentences( payload.getDslConditionSentences() );
        oracle.addDslActionSentences( payload.getDslActionSentences() );
        oracle.addGlobals( payload.getGlobalTypes() );
    }

    private static void populate( final AsyncPackageDataModelOracle oracle,
                                  final PackageDataModelOracleIncrementalPayload payload ) {
        oracle.addFactsAndFields( payload.getModelFields() );
        oracle.addFieldParametersType( payload.getFieldParametersType() );
        oracle.addEventTypes( payload.getEventTypes() );
        oracle.addTypeSources( payload.getTypeSources() );
        oracle.addSuperTypes( payload.getSuperTypes() );
        oracle.addTypeAnnotations( payload.getTypeAnnotations() );
        oracle.addTypeFieldsAnnotations( payload.getTypeFieldsAnnotations() );
        oracle.addMethodInformation( payload.getMethodInformation() );
        oracle.addCollectionTypes( payload.getCollectionTypes() );
    }

}
