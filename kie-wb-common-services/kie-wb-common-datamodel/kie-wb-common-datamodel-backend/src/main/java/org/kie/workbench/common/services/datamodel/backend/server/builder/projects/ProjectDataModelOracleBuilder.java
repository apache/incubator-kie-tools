package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.builder.model.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for DataModelOracle
 */
public final class ProjectDataModelOracleBuilder {

    private ProjectDataModelOracleImpl oracle = new ProjectDataModelOracleImpl();

    private List<FactBuilder> factTypeBuilders = new ArrayList<FactBuilder>();
    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();

    private List<String> errors = new ArrayList<String>();

    public static ProjectDataModelOracleBuilder newProjectOracleBuilder() {
        return new ProjectDataModelOracleBuilder();
    }

    private ProjectDataModelOracleBuilder() {
    }

    public SimpleFactBuilder addFact( final String factType ) {
        return addFact( factType,
                        false );
    }

    public SimpleFactBuilder addFact( final String factType,
                                      final boolean isEvent ) {
        return addFact( factType,
                        isEvent,
                        TypeSource.JAVA_PROJECT );
    }

    public SimpleFactBuilder addFact( final String factType,
                                      final boolean isEvent,
                                      final TypeSource typeSource ) {
        final SimpleFactBuilder builder = new SimpleFactBuilder( this,
                                                                 factType,
                                                                 isEvent,
                                                                 typeSource );
        factTypeBuilders.add( builder );
        return builder;
    }

    public ProjectDataModelOracleBuilder addClass( final Class clazz ) throws IOException {
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
                                                          clazz,
                                                          isEvent,
                                                          typeSource );
        factTypeBuilders.add( builder );
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

    public ProjectDataModelOracleBuilder addEnum( final String enumDefinition ) {
        parseEnumDefinition( enumDefinition );
        return this;
    }

    private void parseEnumDefinition( final String enumDefinition ) {
        final DataEnumLoader enumLoader = new DataEnumLoader( enumDefinition );
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
        return oracle;
    }

    private void loadFactTypes() {
        for ( final FactBuilder factBuilder : this.factTypeBuilders ) {
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
        oracle.addEnumDefinitions( loadableEnums );
    }

}
