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
package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * 
 */
@Dependent
public class EnumEditorPlace extends PlaceRequest
    implements
    IPlaceRequest {

    public EnumEditorPlace() {
        super( "EnumEditor" );
    }

    public EnumEditorPlace(final String token) {
        super( "EnumEditor" );
        String[] parts = token.split( "\\|" );
        if ( parts.length != 1 ) {
            throw new RuntimeException( "Invalid token" );
        }
        addParameter( "path",
                      parts[0] );
    }

    @Override
    public String toString() {
        return getParameter( "path",
                             "" );
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        EnumEditorPlace that = (EnumEditorPlace) o;
        final String thisPath = this.getParameter( "path",
                                                   "" );
        final String thatPath = that.getParameter( "path",
                                                   "" );
        return thisPath.equals( thatPath );
    }

    @Override
    public int hashCode() {
        int result = this.getParameter( "path",
                                        "" ).hashCode();
        return result;
    }

    public static class Tokenizer
        implements
        PlaceTokenizer<EnumEditorPlace> {

        public String getToken(final EnumEditorPlace place) {
            return place.toString();
        }

        public EnumEditorPlace getPlace(final String token) {
            return new EnumEditorPlace( token );
        }
    }

}
