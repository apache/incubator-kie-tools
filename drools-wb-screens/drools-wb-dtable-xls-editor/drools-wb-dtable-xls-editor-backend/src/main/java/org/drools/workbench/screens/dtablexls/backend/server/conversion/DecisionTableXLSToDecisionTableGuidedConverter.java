/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.util.DateUtils;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.guvnor.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.guvnor.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Files;
import org.drools.workbench.screens.drltext.service.DRLTextEditorService;
import org.drools.workbench.screens.drltext.type.DRLResourceTypeDefinition;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSConversionService;
import org.drools.workbench.screens.dtablexls.type.DecisionTableXLSResourceTypeDefinition;
import org.drools.workbench.screens.factmodel.type.FactModelResourceTypeDefinition;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.kie.guvnor.project.model.PackageConfiguration;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.shared.workbench.type.ResourceTypeDefinition;

/**
 * Converter from a XLS Decision Table to a Guided Decision Table
 */
@ApplicationScoped
public class DecisionTableXLSToDecisionTableGuidedConverter implements DecisionTableXLSConversionService {

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private DRLTextEditorService drlService;

    @Inject
    private GuidedDecisionTableEditorService guidedDecisionTableService;

    @Inject
    private GlobalsEditorService globalsService;

    @Inject
    private ProjectService projectService;

    @Inject
    private MetadataService metadataService;

    @Inject
    //Type Definition to ensure new files have correct extension
    private DecisionTableXLSResourceTypeDefinition xlsDTableType;

    @Inject
    //Type Definition to ensure new files have correct extension
    private GuidedDTableResourceTypeDefinition guidedDTableType;

    @Inject
    //Type Definition to ensure new files have correct extension
    private DRLResourceTypeDefinition drlType;

    @Inject
    //Type Definition to ensure new files have correct extension
    private FactModelResourceTypeDefinition modelType;

    @Inject
    //Type Definition to ensure new files have correct extension
    private GlobalResourceTypeDefinition globalsType;

    @Override
    public ConversionResult convert( final Path path ) {

        ConversionResult result = new ConversionResult();

        //Check Asset is of the correct format
        if ( !xlsDTableType.accept( path ) ) {
            result.addMessage( "Source Asset is not an XLS Decision Table.",
                               ConversionMessageType.ERROR );
            return result;
        }

        //Perform conversion!
        final GuidedDecisionTableGeneratorListener listener = parseAssets( path,
                                                                           result );

        //Root path for new resources is the same folder as the XLS file
        final Path context = paths.convert( paths.convert( path ).getParent() );

        //Add Ancillary resources
        createNewImports( context,
                          listener.getImports(),
                          result );
        createNewFunctions( context,
                            listener.getImports(),
                            listener.getFunctions(),
                            result );
        createNewQueries( context,
                          listener.getImports(),
                          listener.getQueries(),
                          result );
        createNewDeclarativeTypes( context,
                                   listener.getImports(),
                                   listener.getTypeDeclarations(),
                                   result );
        createNewGlobals( context,
                          listener.getGlobals(),
                          result );

        //Add Web Guided Decision Tables
        createNewDecisionTables( context,
                                 listener.getImports(),
                                 listener.getGuidedDecisionTables(),
                                 result );

        return result;
    }

    private GuidedDecisionTableGeneratorListener parseAssets( final Path path,
                                                              final ConversionResult result ) {

        final List<DataListener> listeners = new ArrayList<DataListener>();
        final GuidedDecisionTableGeneratorListener listener = new GuidedDecisionTableGeneratorListener( result );
        listeners.add( listener );

        final ExcelParser parser = new ExcelParser( listeners );
        final InputStream stream = ioService.newInputStream( paths.convert( path ) );

        try {
            parser.parseFile( stream );
        } finally {
            try {
                stream.close();
            } catch ( IOException ioe ) {
                result.addMessage( ioe.getMessage(),
                                   ConversionMessageType.ERROR );
            }
        }
        return listener;
    }

    private void createNewFunctions( final Path context,
                                     final List<Import> imports,
                                     final List<String> functions,
                                     final ConversionResult result ) {
        if ( functions == null || functions.isEmpty() ) {
            return;
        }

        //Create new assets for Functions
        for ( int iCounter = 0; iCounter < functions.size(); iCounter++ ) {

            final String assetName = makeNewAssetName( "Function " + ( iCounter + 1 ),
                                                       drlType );
            final String drl = makeDRL( imports,
                                        functions.get( iCounter ) );
            drlService.create( context,
                               assetName,
                               drl,
                               "Converted from XLS Decision Table" );

            result.addMessage( "Created Function '" + assetName + "'",
                               ConversionMessageType.INFO );
        }
    }

    private void createNewQueries( final Path context,
                                   final List<Import> imports,
                                   final List<String> queries,
                                   final ConversionResult result ) {
        if ( queries == null || queries.isEmpty() ) {
            return;
        }

        //Create new assets for Queries
        for ( int iCounter = 0; iCounter < queries.size(); iCounter++ ) {

            final String assetName = makeNewAssetName( "Query " + ( iCounter + 1 ),
                                                       drlType );
            final String drl = makeDRL( imports,
                                        queries.get( iCounter ) );
            drlService.create( context,
                               assetName,
                               drl,
                               "Converted from XLS Decision Table" );

            result.addMessage( "Created Query '" + assetName + "'",
                               ConversionMessageType.INFO );
        }
    }

    private void createNewDeclarativeTypes( final Path context,
                                            final List<Import> imports,
                                            final List<String> declaredTypes,
                                            final ConversionResult result ) {
        if ( declaredTypes == null || declaredTypes.isEmpty() ) {
            return;
        }

        //Create new assets for Declarative Types
        for ( int iCounter = 0; iCounter < declaredTypes.size(); iCounter++ ) {

            final String assetName = makeNewAssetName( "Model " + ( iCounter + 1 ),
                                                       modelType );
            final String drl = makeDRL( imports,
                                        declaredTypes.get( iCounter ) );
            drlService.create( context,
                               assetName,
                               drl,
                               "Converted from XLS Decision Table" );

            result.addMessage( "Created Declarative Model '" + assetName + "'",
                               ConversionMessageType.INFO );
        }
    }

    private String makeDRL( final List<Import> imports,
                            final String baseDRL ) {
        final StringBuilder sb = new StringBuilder();
        if ( !( imports == null || imports.isEmpty() ) ) {
            for ( Import item : imports ) {
                sb.append( "import " ).append( item.getClassName() ).append( ";\n" );
            }
            sb.append( "\n" );
        }
        sb.append( baseDRL ).append( "\n" );
        return sb.toString();
    }

    private void createNewGlobals( final Path context,
                                   final List<Global> globals,
                                   final ConversionResult result ) {
        if ( globals == null || globals.isEmpty() ) {
            return;
        }

        //Create new asset for Globals. All Globals can be in one file.
        final String assetName = makeNewAssetName( "Global",
                                                   globalsType );
        final GlobalsModel model = makeGlobalsModel( globals );
        globalsService.create( context,
                               assetName,
                               model,
                               "Converted from XLS Decision Table" );

        result.addMessage( "Created Globals '" + assetName + "'",
                           ConversionMessageType.INFO );
    }

    private GlobalsModel makeGlobalsModel( final List<Global> globals ) {
        final GlobalsModel model = new GlobalsModel();
        for ( Global global : globals ) {
            model.getGlobals().add( new org.drools.workbench.screens.globals.model.Global( global.getIdentifier(),
                                                                                           global.getClassName() ) );
        }
        return model;
    }

    private void createNewImports( final Path context,
                                   final List<Import> imports,
                                   final ConversionResult result ) {

        if ( imports == null || imports.isEmpty() ) {
            return;
        }

        //Load existing PackageConfiguration
        PackageConfiguration packageConfiguration = new PackageConfiguration();
        final org.kie.commons.java.nio.file.Path nioExternalImportsPath = paths.convert( context ).resolve( "project.imports" );
        final Path externalImportsPath = paths.convert( nioExternalImportsPath );
        if ( Files.exists( nioExternalImportsPath ) ) {
            packageConfiguration = projectService.load( externalImportsPath );
        }

        //Make collections of existing Imports so we don't duplicate them when adding the new
        List<String> existingImports = new ArrayList<String>();
        for ( org.drools.guvnor.models.commons.shared.imports.Import item : packageConfiguration.getImports().getImports() ) {
            existingImports.add( item.getType() );
        }

        //Add imports
        boolean isModified = false;
        for ( Import item : imports ) {
            if ( !existingImports.contains( item.getClassName() ) ) {
                isModified = true;
                result.addMessage( "Created Import for '" + item.getClassName() + "'.",
                                   ConversionMessageType.INFO );
                packageConfiguration.getImports().addImport( new org.drools.guvnor.models.commons.shared.imports.Import( item.getClassName() ) );
            }
        }

        //Save update
        if ( isModified ) {
            final Metadata metadata = metadataService.getMetadata( context );
            projectService.save( externalImportsPath,
                                 packageConfiguration,
                                 metadata,
                                 "Imports added during XLS conversion" );
        }
    }

    private void createNewDecisionTables( final Path context,
                                          final List<Import> imports,
                                          final List<GuidedDecisionTable52> dtables,
                                          final ConversionResult result ) {
        if ( dtables == null || dtables.isEmpty() ) {
            return;
        }

        //Create new assets for Guided Decision Tables
        for ( int iCounter = 0; iCounter < dtables.size(); iCounter++ ) {

            //Add imports
            final GuidedDecisionTable52 dtable = dtables.get( iCounter );
            for ( Import item : imports ) {
                dtable.getImports().addImport( new org.drools.guvnor.models.commons.shared.imports.Import( item.getClassName() ) );
            }

            //Make new resource
            final String assetName = makeNewAssetName( dtable.getTableName(),
                                                       guidedDTableType );
            guidedDecisionTableService.create( context,
                                               assetName,
                                               dtable,
                                               "Converted from XLS Decision Table" );

            result.addMessage( "Created Guided Decision Table '" + assetName + "'",
                               ConversionMessageType.INFO );
        }
    }

    private String makeNewAssetName( final String baseName,
                                     final ResourceTypeDefinition type ) {
        Calendar now = Calendar.getInstance();
        StringBuilder sb = new StringBuilder( baseName );
        sb.append( " (converted on " );
        sb.append( DateUtils.format( now.getTime() ) );
        sb.append( " " );
        sb.append( now.get( Calendar.HOUR_OF_DAY ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.MINUTE ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.SECOND ) );
        sb.append( ")" );
        sb.append( "." ).append( type.getSuffix() );
        return sb.toString();
    }

}
