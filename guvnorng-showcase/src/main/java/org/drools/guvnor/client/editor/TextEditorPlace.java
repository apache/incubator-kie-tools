/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.editor;

import com.google.gwt.place.shared.PlaceTokenizer;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TextEditorPlace extends PlaceRequest implements IPlaceRequest {

    private final String helloName;
    private final String fileName;

    public TextEditorPlace() {
        super("TextEditor");
        this.helloName = "TextEditor";
        this.fileName = "myfile.hack";
    }

    public TextEditorPlace(final String token) {
        super("TextEditor");
        String[] parts = token.split("\\|");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token");
        }
        this.helloName = parts[0];
        this.fileName = parts[1];
    }


    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return helloName + '|' + fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TextEditorPlace that = (TextEditorPlace) o;

        if (!helloName.equals(that.helloName)) {
            return false;
        }
        if (!fileName.equals(that.fileName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = helloName.hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }

    public static class Tokenizer implements PlaceTokenizer<TextEditorPlace> {

        public String getToken(final TextEditorPlace place) {
            return place.toString();
        }

        public TextEditorPlace getPlace(final String token) {
            return new TextEditorPlace(token);
        }
    }

}
