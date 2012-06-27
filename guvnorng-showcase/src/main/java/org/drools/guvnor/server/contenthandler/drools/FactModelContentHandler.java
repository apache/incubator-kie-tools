/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.contenthandler.drools;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.shared.common.vo.assets.factmodel.AnnotationMetaModel;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactMetaModel;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactModels;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FieldMetaModel;
import org.drools.guvnor.shared.common.vo.assets.plaintext.RuleContentText;
import org.drools.lang.descr.AnnotationDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactModelContentHandler {

    private static final LoggingHelper log = LoggingHelper.getLogger( FactModelContentHandler.class );

    public void retrieveAssetContent(FactModels asset,
                                     String content) {
        try {
            List<FactMetaModel> models = toModel( content );
            //FactModels ms = new FactModels();
            asset.setContent(models);
            //asset.setContent( ms );
        } catch ( DroolsParserException e ) {
            log.error( "Unable to parse the DRL for the model - falling back to text (" + e.getMessage() + ")" );
            RuleContentText text = new RuleContentText();
            text.setContent(content);
            //asset.setContent( text );
        }

    }


    public void storeAssetContent(FactModels asset,
                                  String repoAsset) {
        repoAsset = toDRL( asset.getContent() );
    }

    String toDRL(FactMetaModel mm) {
        StringBuilder sb = new StringBuilder();
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

    List<FactMetaModel> toModel(String drl) throws DroolsParserException {
        if ( drl != null && (drl.startsWith( "#advanced" ) || drl.startsWith( "//advanced" )) ) {
            throw new DroolsParserException( "Using advanced editor" );
        }
        DrlParser parser = new DrlParser();
        PackageDescr pkg = parser.parse( drl );
        if ( parser.hasErrors() ) {
            throw new DroolsParserException( "The model drl " + drl + " is not valid" );
        }

        if ( pkg == null ) return new ArrayList<FactMetaModel>();
        List<TypeDeclarationDescr> types = pkg.getTypeDeclarations();
        List<FactMetaModel> list = new ArrayList<FactMetaModel>( types.size() );
        for ( TypeDeclarationDescr td : types ) {
            FactMetaModel mm = new FactMetaModel();
            mm.setName( td.getTypeName() );
            mm.setSuperType( td.getSuperTypeName() );

            Map<String, TypeFieldDescr> fields = td.getFields();
            for ( Map.Entry<String, TypeFieldDescr> en : fields.entrySet() ) {
                String fieldName = en.getKey();
                TypeFieldDescr descr = en.getValue();
                FieldMetaModel fm = new FieldMetaModel( fieldName,
                                                        descr.getPattern().getObjectType() );

                mm.getFields().add( fm );
            }

            Map<String, AnnotationDescr> annotations = td.getAnnotations();
            for ( Map.Entry<String, AnnotationDescr> en : annotations.entrySet() ) {
                String annotationName = en.getKey();
                AnnotationDescr descr = en.getValue();
                Map<String, String> values = descr.getValues();
                AnnotationMetaModel am = new AnnotationMetaModel( annotationName,
                                                                  values );

                mm.getAnnotations().add( am );
            }

            list.add( mm );
        }
        return list;
    }

    String toDRL(List<FactMetaModel> models) {
        StringBuilder sb = new StringBuilder();
        for ( FactMetaModel factMetaModel : models ) {
            String drl = toDRL( factMetaModel );
            sb.append( drl ).append( "\n\n" );
        }
        return sb.toString().trim();
    }

    private StringBuilder buildAnnotationDRL(AnnotationMetaModel a) {
        StringBuilder sb = new StringBuilder();
        sb.append( "@" );
        sb.append( a.name );
        sb.append( "(" );
        for ( Map.Entry<String, String> e : a.getValues().entrySet() ) {
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

}
