/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;

/**
 * Builder for DataModelOracle
 */
public final class ProjectDataModelOracleBuilder {

    private ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

    private Map<String, FactBuilder> factTypeBuilders = new HashMap<String, FactBuilder>();
    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();
    private List<String> packageNames = new ArrayList<String>();

    private final Map<String, FactBuilder> discoveredFieldFactBuilders = new HashMap<String, FactBuilder>();

    private List<String> errors = new ArrayList<String>();

    public static ProjectDataModelOracleBuilder newProjectOracleBuilder() {
        return new ProjectDataModelOracleBuilder();
    }

    private ProjectDataModelOracleBuilder() {
    }

    //Used by tests
    public SimpleFactBuilder addFact( final String factType ) {
        return addFact( factType,
                        false );
    }

    //Used by tests
    public SimpleFactBuilder addFact( final String factType,
                                      final boolean isEvent ) {
        return addFact( factType,
                        isEvent,
                        TypeSource.JAVA_PROJECT );
    }

    //Used by tests
    public SimpleFactBuilder addFact( final String factType,
                                      final boolean isEvent,
                                      final TypeSource typeSource ) {
        final SimpleFactBuilder builder = new SimpleFactBuilder( this,
                                                                 factType,
                                                                 isEvent,
                                                                 typeSource );
        factTypeBuilders.put( factType,
                              builder );
        return builder;
    }

    public ProjectDataModelOracleBuilder addClass( final Class clazz) throws IOException {
        return addClass( clazz,
                         false );
    }

    public ProjectDataModelOracleBuilder addClass( final Class clazz,
                                                   final boolean isEvent ) throws IOException {
        return addClass( clazz,
                         isEvent,
                         TypeSource.JAVA_PROJECT );
    }

    public ProjectDataModelOracleBuilder addClass( final Class clazz,
                                                   final boolean isEvent,
                                                   final TypeSource typeSource ) throws IOException {
        final FactBuilder builder = new ClassFactBuilder( this,
                                                          discoveredFieldFactBuilders,
                                                          clazz,
                                                          isEvent,
                                                          typeSource );
        factTypeBuilders.put( clazz.getName(),
                              builder );
        return this;
    }

    public ProjectDataModelOracleBuilder addEnum( final String factType,
                                                  final String fieldName,
                                                  final String[] values ) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put( qualifiedFactField,
                            values );
        return this;
    }

    public ProjectDataModelOracleBuilder addEnum( final String enumDefinition,
                                                  final ClassLoader classLoader ) {
        parseEnumDefinition( enumDefinition,
                             classLoader );
        return this;
    }

    private void parseEnumDefinition( final String enumDefinition,
                                      final ClassLoader classLoader ) {
        final DataEnumLoader enumLoader = new DataEnumLoader( enumDefinition,
                                                              classLoader );
        if ( enumLoader.hasErrors() ) {
            logEnumErrors( enumLoader );
        } else {
            factFieldEnums.putAll( enumLoader.getData() );
        }
    }

    private void logEnumErrors( final DataEnumLoader enumLoader ) {
        errors.addAll( enumLoader.getErrors() );
    }

    public ProjectDataModelOracle build() {
        loadFactTypes();
        loadEnums();
        loadPackageNames();

        return oracle;
    }

    private void loadPackageNames() {
        oracle.addProjectPackageNames( packageNames );
    }

    private void loadFactTypes() {

        for ( final FactBuilder factBuilder : new ArrayList<FactBuilder>( this.factTypeBuilders.values() ) ) {
            this.factTypeBuilders.putAll( factBuilder.getInternalBuilders() );
        }

        for ( final FactBuilder factBuilder : this.factTypeBuilders.values() ) {
            factBuilder.build( oracle );
        }

    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        oracle.addProjectJavaEnumDefinitions( loadableEnums );
    }

    public void addPackage( String packageName ) {
        this.packageNames.add( packageName );
    }

    public void addPackages( Collection<String> packageNames ) {
        this.packageNames.addAll( packageNames );
    }
}
