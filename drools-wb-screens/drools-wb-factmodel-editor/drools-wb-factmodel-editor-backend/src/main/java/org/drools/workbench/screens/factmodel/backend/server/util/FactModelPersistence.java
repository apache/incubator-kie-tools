package org.drools.workbench.screens.factmodel.backend.server.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.backend.imports.ImportsWriter;
import org.drools.workbench.models.commons.backend.packages.PackageNameParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameWriter;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.screens.factmodel.model.AnnotationMetaModel;
import org.drools.workbench.screens.factmodel.model.FactMetaModel;
import org.drools.workbench.screens.factmodel.model.FactModels;
import org.drools.workbench.screens.factmodel.model.FieldMetaModel;
import org.uberfire.commons.validation.Preconditions;

import static java.util.Collections.*;

/**
 * Utilities for FactModels
 */
public class FactModelPersistence {

    public static String marshal( final FactModels content ) {
        final StringBuilder sb = new StringBuilder();

        PackageNameWriter.write( sb,
                                 content );
        ImportsWriter.write( sb,
                             content );

        for ( final FactMetaModel factMetaModel : content.getModels() ) {
            sb.append( toDRL( factMetaModel ) ).append( "\n\n" );
        }
        return sb.toString().trim();
    }

    private static String toDRL( final FactMetaModel mm ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "declare " ).append( mm.getName() );
        if ( mm.hasSuperType() ) {
            sb.append( " extends " );
            sb.append( mm.getSuperType() );
        }
        for ( int i = 0; i < mm.getAnnotations().size(); i++ ) {
            AnnotationMetaModel a = mm.getAnnotations().get( i );
            sb.append( "\n\t" );
            sb.append( buildAnnotationDRL( a ) );
        }
        for ( int i = 0; i < mm.getFields().size(); i++ ) {
            FieldMetaModel f = mm.getFields().get( i );
            sb.append( "\n\t" );
            sb.append( f.name ).append( ": " ).append( f.type );
        }
        sb.append( "\nend" );
        return sb.toString();
    }

    private static StringBuilder buildAnnotationDRL( AnnotationMetaModel a ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( "@" );
        sb.append( a.name );
        sb.append( "(" );
        for ( final Map.Entry<String, String> e : a.getValues().entrySet() ) {
            if ( e.getKey() != null && e.getKey().length() > 0 ) {
                sb.append( e.getKey() );
                sb.append( " = " );
            }
            if ( e.getValue() != null && e.getValue().length() > 0 ) {
                sb.append( e.getValue() );
            }
            sb.append( ", " );
        }
        sb.delete( sb.length() - 2,
                   sb.length() );
        sb.append( ")" );
        return sb;
    }

    public static FactModels unmarshal( final String content ) {
        try {
            //De-serialize model
            final List<FactMetaModel> models = toModel( content );
            final FactModels factModels = new FactModels();
            factModels.getModels().addAll( models );

            //De-serialize Package name
            final String packageName = PackageNameParser.parsePackageName( content );
            factModels.setPackageName( packageName );

            //De-serialize imports
            final Imports imports = ImportsParser.parseImports( content );
            factModels.setImports( imports );

            return factModels;

        } catch ( final DroolsParserException e ) {
            throw new RuntimeException( e );
        }
        //TODO {porcelli} needs define error handling strategy
//            log.error( "Unable to parse the DRL for the model - falling back to text (" + e.getMessage() + ")" );
//            RuleContentText text = new RuleContentText();
//            text.content = item.getContent();
//            asset.setContent( text );
    }

    private static List<FactMetaModel> toModel( String drl )
            throws DroolsParserException {
        Preconditions.checkNotNull(drl, "The string representing DRL can't be null!");

        if ( drl.startsWith( "#advanced" ) || drl.startsWith( "//advanced" ) ) {
            throw new DroolsParserException( "Using advanced editor" );
        }
        final DrlParser parser = new DrlParser();
        final StringReader reader = new StringReader( drl );
        final PackageDescr pkg = parser.parse( reader );
        if ( parser.hasErrors() ) {
            throw new DroolsParserException( "The model drl " + drl + " is not valid" );
        }

        if ( pkg == null ) {
            return emptyList();
        }
        final List<TypeDeclarationDescr> types = pkg.getTypeDeclarations();
        final List<FactMetaModel> list = new ArrayList<FactMetaModel>( types.size() );
        for ( final TypeDeclarationDescr td : types ) {
            final FactMetaModel mm = new FactMetaModel();
            mm.setName( td.getTypeName() );
            mm.setSuperType( td.getSuperTypeName() );

            final Map<String, TypeFieldDescr> fields = td.getFields();
            for ( Map.Entry<String, TypeFieldDescr> en : fields.entrySet() ) {
                final String fieldName = en.getKey();
                final TypeFieldDescr descr = en.getValue();
                final FieldMetaModel fm = new FieldMetaModel( fieldName,
                                                              descr.getPattern().getObjectType() );

                mm.getFields().add( fm );
            }

            final Map<String, AnnotationDescr> annotations = td.getAnnotations();
            for ( final Map.Entry<String, AnnotationDescr> en : annotations.entrySet() ) {
                final String annotationName = en.getKey();
                final AnnotationDescr descr = en.getValue();
                final Map<String, String> values = descr.getValues();
                final AnnotationMetaModel am = new AnnotationMetaModel( annotationName,
                                                                        values );

                mm.getAnnotations().add( am );
            }

            list.add( mm );
        }

        return list;
    }

}
