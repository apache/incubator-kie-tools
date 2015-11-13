/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.Framework;
import org.uberfire.ext.plugin.model.PluginSimpleContent;
import org.uberfire.ext.plugin.model.PluginType;

public class JSRegistry {

    public static String convertToJSRegistry( PluginSimpleContent plugin ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        final StringBuilder sb = new StringBuilder();

        if ( plugin.getCodeMap().containsKey( CodeType.MAIN ) ) {
            sb.append( plugin.getCodeMap().get( CodeType.MAIN ) );
        }

        if ( plugin.getType().equals( PluginType.SCREEN ) ) {
            sb.append( "$registerPlugin({" );
        } else if ( plugin.getType().equals( PluginType.SPLASH ) ) {
            sb.append( "$registerSplashScreen({" );
        } else if ( plugin.getType().equals( PluginType.EDITOR ) ) {
            sb.append( "$registerEditor({" );
        } else if ( plugin.getType().equals( PluginType.PERSPECTIVE ) ) {
            sb.append( "$registerPerspective({" );
        }

        sb.append( "id:" ).append( '"' ).append( plugin.getName() ).append( '"' ).append( "," );

        if ( plugin.getCodeMap().size() > 1 ) {
            //Order Code fragments in the same sequence as defined in the CodeType enum, to aid repeatable testing
            for ( CodeType ct : CodeType.values() ) {
                if ( ct.equals( CodeType.MAIN ) ) {
                    continue;
                }
                if ( plugin.getCodeMap().containsKey( ct ) ) {
                    sb.append( ct.toString().toLowerCase() ).append( ": " );
                    sb.append( prepareEntryValue( ct, plugin.getCodeMap().get( ct ) ) ).append( "," );
                }
            }
        }

        if ( plugin.getFrameworks() != null && !plugin.getFrameworks().isEmpty() ) {
            final Framework fm = plugin.getFrameworks().iterator().next();
            sb.append( "type: " ).append( '"' ).append( fm.getType() ).append( '"' ).append( ',' );
        }

        if ( !plugin.getType().equals( PluginType.PERSPECTIVE ) ) {
            sb.append( "template: " );

            gson.toJson( plugin.getTemplate(), sb );
        } else {
            sb.append( "view: {" ).append( plugin.getTemplate() ).append( "}" );
        }

        sb.append( "});" );
        return sb.toString();
    }

    protected static String prepareEntryValue( CodeType key,
                                               String value ) {
        if ( isAFunction( value ) ) {
            return value;
        } else {
            return prepareStringValue( key, value );
        }
    }

    private static boolean isAFunction( String value ) {
        return value != null && value.contains( "function" );
    }

    private static String prepareStringValue( CodeType key,
                                              String value ) {

        return wrapWithFunctionDeclaration( key, value );
    }

    private static String wrapWithFunctionDeclaration( CodeType key,
                                                       String value ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "function (){" );
        sb.append( createFunctionBody( key, value ) );
        sb.append( ";}" );
        return sb.toString();
    }

    private static String createFunctionBody( CodeType key,
                                              String value ) {
        StringBuilder sb = new StringBuilder();
        if ( JSFunctionNeedsToReturnValue( key ) ) {
            sb.append( "return " );
            sb.append( wrapValueWithCommas( value ) );
        } else {
            sb.append( value );
        }
        return sb.toString();
    }

    private static String wrapValueWithCommas( String value ) {
        StringBuilder sb = new StringBuilder();
        if ( value == null || value.isEmpty() ) {
            sb.append( "\"\"" );
        } else {
            if ( value.charAt( 0 ) != '"' ) {
                sb.append( "\"" );
            }
            sb.append( value );
            if ( value.charAt( value.length() - 1 ) != '"' ) {
                sb.append( "\"" );
            }
        }
        return sb.toString();
    }

    public static boolean JSFunctionNeedsToReturnValue( CodeType codeType ) {
        return codeType.equals( CodeType.TITLE ) ||
                codeType.equals( CodeType.RESOURCE_TYPE ) ||
                codeType.equals( CodeType.PRIORITY ) ||
                codeType.equals( CodeType.BODY_HEIGHT ) ||
                codeType.equals( CodeType.INTERCEPTION_POINTS ) ||
                codeType.equals( CodeType.PANEL_TYPE );
    }

}
