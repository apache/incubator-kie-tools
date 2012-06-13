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
package org.drools.guvnor.client.editors.factmodel;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * 
 */
@ApplicationScoped
public class FactModelEditorPlace extends PlaceRequest
    implements
    IPlaceRequest {

    private final String fileName;

    public FactModelEditorPlace() {
        super( "FactModelEditor" );
        fileName = "";
    }

    public FactModelEditorPlace(final String token) {
        super( "FactModelEditor" );
        String[] parts = token.split( "\\|" );
        if ( parts.length != 1 ) {
            throw new RuntimeException( "Invalid token" );
        }
        this.fileName = parts[0];
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        FactModelEditorPlace that = (FactModelEditorPlace) o;
        if ( this.fileName == null && that.fileName != null ) {
            return false;
        }
        return this.fileName.equals( that.fileName );
    }

    @Override
    public int hashCode() {
        int result = this.fileName.hashCode();
        return result;
    }

    public static class Tokenizer
        implements
        PlaceTokenizer<FactModelEditorPlace> {

        public String getToken(final FactModelEditorPlace place) {
            return place.toString();
        }

        public FactModelEditorPlace getPlace(final String token) {
            return new FactModelEditorPlace( token );
        }
    }

}
