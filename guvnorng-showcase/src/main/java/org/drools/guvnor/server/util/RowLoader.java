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

package org.drools.guvnor.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepositoryException;
/**
 * @deprecated in favour of {@link AbstractPagedTable}
 */
public class RowLoader {
    private String[] headers;
    private String[] headerTypes;
    List<Method>             extractors;

    public String[] getHeaders() {
        return headers;
    }

    public String[] getHeaderTypes() {
        return headerTypes;
    }

    public String[] getRow(AssetItem item) {
        String[] row = new String[headers.length];
        for ( int i = 0; i < row.length; i++ ) {
            Method meth = extractors.get( i );
            try {
                Object val = meth.invoke( item,
                                          (Object[]) null );
                if ( val instanceof String ) {
                    String s = (String) val;
                    if ( s.length() > 64 ) {
                        s = s.substring( 0,
                                         61 ) + "...";
                    }
                    row[i] = s;

                } else if ( val instanceof Calendar ) {
                    row[i] = Long.toString(((Calendar) val).getTime().getTime());//DF.format( ((Calendar) val).getTime() );
                } else {
                    row[i] = val.toString();
                }
            } catch ( Exception e ) {
                if ( e instanceof RuntimeException ) throw (RuntimeException) e;
                throw new RulesRepositoryException( e );
            }
        }
        return row;
    }


    public RowLoader(String resourcename) {

        InputStream in = RowLoader.class.getResourceAsStream( "/" + resourcename + ".properties" );
        if (in == null) {
            throw new IllegalStateException("can't find resource name: /" + resourcename + ".properties");
        }
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        List<String> fields = new ArrayList<String>();
        List<String> fieldTypes = new ArrayList<String>();
        extractors = new ArrayList<Method>();
        String line;

        try {

            while ( (line = reader.readLine()) != null ) {
                if ( !line.startsWith( "#" ) && !line.trim().equals( "" ) ) {
                    StringTokenizer tok = new StringTokenizer( line,
                                                               "=" );
                    String field = tok.nextToken();
                    String method = tok.nextToken();
                    fields.add( field );

                    final Method meth = AssetItem.class.getMethod( method,
                                                                   new Class[]{} );

                    extractors.add( meth );

                    fieldTypes.add( meth.getGenericReturnType().toString() );

                }
            }
        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            }
            throw new RulesRepositoryException( e );
        } finally {
            closeStream( reader );
        }
        headers = fields.toArray( new String[fields.size()] );
        headerTypes = fieldTypes.toArray( new String[fieldTypes.size()] );
    }

    private void closeStream(BufferedReader reader) {
        try {
            reader.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
