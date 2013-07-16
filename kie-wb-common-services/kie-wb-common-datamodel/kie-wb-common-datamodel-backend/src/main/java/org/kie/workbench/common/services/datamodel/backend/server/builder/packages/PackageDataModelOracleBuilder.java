package org.kie.workbench.common.services.datamodel.backend.server.builder.packages;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.dsl.DSLMappingEntry;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.workbench.models.commons.shared.rule.DSLSentence;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.GlobalsParser;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracleImpl;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for PackageDataModelOracle
 */
public final class PackageDataModelOracleBuilder {

    private static final Logger log = LoggerFactory.getLogger( PackageDataModelOracleBuilder.class );

    private final String packageName;

    private PackageDataModelOracleImpl packageOracle = new PackageDataModelOracleImpl();
    private ProjectDataModelOracle projectOracle = new ProjectDataModelOracleImpl();

    private Map<String, String[]> factFieldEnums = new HashMap<String, String[]>();
    private List<DSLSentence> dslConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslActionSentences = new ArrayList<DSLSentence>();
    //These are not used anywhere in Guvnor 5.5.x, but have been retained for future scope
    private List<DSLSentence> dslKeywordItems = new ArrayList<DSLSentence>();
    private List<DSLSentence> dslAnyScopeItems = new ArrayList<DSLSentence>();

    // Package-level map of Globals (name is key) and their type (value).
    private Map<String, String> packageGlobalTypes = new HashMap<String, String>();

    public static PackageDataModelOracleBuilder newPackageOracleBuilder() {
        return new PackageDataModelOracleBuilder( "" );
    }

    public static PackageDataModelOracleBuilder newPackageOracleBuilder( final String packageName ) {
        return new PackageDataModelOracleBuilder( packageName );
    }

    private PackageDataModelOracleBuilder( final String packageName ) {
        this.packageName = packageName;
    }

    public PackageDataModelOracleBuilder setProjectOracle( final ProjectDataModelOracle projectOracle ) {
        this.projectOracle = projectOracle;
        return this;
    }

    public PackageDataModelOracleBuilder addEnum( final String factType,
                                                  final String fieldName,
                                                  final String[] values ) {
        final String qualifiedFactField = factType + "#" + fieldName;
        factFieldEnums.put( qualifiedFactField,
                            values );
        return this;
    }

    public PackageDataModelOracleBuilder addEnum( final String enumDefinition ) {
        parseEnumDefinition( enumDefinition );
        return this;
    }

    private void parseEnumDefinition( final String enumDefinition ) {
        final DataEnumLoader enumLoader = new DataEnumLoader( enumDefinition );
        if ( !enumLoader.hasErrors() ) {
            factFieldEnums.putAll( enumLoader.getData() );
        }
    }

    public PackageDataModelOracleBuilder addDsl( final String dslDefinition ) {
        parseDslDefinition( dslDefinition );
        return this;
    }

    private void parseDslDefinition( final String dslDefinition ) {
        final DSLTokenizedMappingFile dslLoader = new DSLTokenizedMappingFile();
        try {
            if ( dslLoader.parseAndLoad( new StringReader( dslDefinition ) ) ) {
                populateDSLSentences( dslLoader );
            }
        } catch ( IOException e ) {
            log.error( e.getMessage() );
        }
    }

    private void populateDSLSentences( final DSLTokenizedMappingFile dslLoader ) {
        for ( DSLMappingEntry entry : dslLoader.getMapping().getEntries() ) {
            if ( entry.getSection() == DSLMappingEntry.CONDITION ) {
                addDSLConditionSentence( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.CONSEQUENCE ) {
                addDSLActionSentence( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.KEYWORD ) {
                addDSLKeywordMapping( entry.getMappingKey() );
            } else if ( entry.getSection() == DSLMappingEntry.ANY ) {
                addDSLAnyScopeMapping( entry.getMappingKey() );
            }
        }
    }

    private void addDSLConditionSentence( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslConditionSentences.add( sentence );
    }

    private void addDSLActionSentence( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslActionSentences.add( sentence );
    }

    private void addDSLKeywordMapping( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslKeywordItems.add( sentence );
    }

    private void addDSLAnyScopeMapping( final String definition ) {
        final DSLSentence sentence = new DSLSentence();
        sentence.setDefinition( definition );
        this.dslAnyScopeItems.add( sentence );
    }

    public PackageDataModelOracleBuilder addGlobals( final String definition ) {
        List<Pair<String, String>> globals = GlobalsParser.parseGlobals( definition );
        for ( Pair<String, String> g : globals ) {
            packageGlobalTypes.put( g.getK1(),
                                    g.getK2() );
        }
        return this;
    }

    public PackageDataModelOracle build() {
        //Copy Project DMO into Package DMO
        final ProjectDataModelOracleImpl pd = (ProjectDataModelOracleImpl) projectOracle;
        packageOracle.addFactsAndFields( pd.getModelFields() );
        packageOracle.addFieldParametersType( pd.getProjectFieldParametersType() );
        packageOracle.addEnumDefinitions( pd.getProjectJavaEnumLists() );
        packageOracle.addMethodInformation( pd.getProjectMethodInformation() );
        packageOracle.addCollectionTypes( pd.getProjectCollectionTypes() );
        packageOracle.addEventTypes( pd.getProjectEventTypes() );
        packageOracle.addTypeSources( pd.getProjectTypeSources() );
        packageOracle.addSuperTypes( pd.getProjectSuperTypes() );
        packageOracle.addTypeAnnotations( pd.getTypeAnnotations() );
        packageOracle.addTypeFieldsAnnotations( pd.getTypeFieldsAnnotations() );

        //Add Package DMO specifics
        loadEnums();
        loadDsls();
        loadGlobals();
        loadProjectOracle();

        return packageOracle;
    }

    private void loadProjectOracle() {
        packageOracle.setPackageName( packageName );
        packageOracle.filter();
    }

    private void loadEnums() {
        final Map<String, String[]> loadableEnums = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : factFieldEnums.entrySet() ) {
            final String qualifiedFactField = e.getKey();
            loadableEnums.put( qualifiedFactField,
                               e.getValue() );
        }
        packageOracle.addPackageGuvnorEnums( loadableEnums );
    }

    private void loadDsls() {
        packageOracle.addPackageDslConditionSentences( dslConditionSentences );
        packageOracle.addPackageDslActionSentences( dslActionSentences );
    }

    private void loadGlobals() {
        packageOracle.addPackageGlobals( packageGlobalTypes );
    }

}
