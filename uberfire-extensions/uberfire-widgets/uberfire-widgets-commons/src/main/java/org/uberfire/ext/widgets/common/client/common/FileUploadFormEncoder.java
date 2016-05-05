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

package org.uberfire.ext.widgets.common.client.common;

import org.gwtbootstrap3.client.ui.Form;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.ui.Hidden;

public class FileUploadFormEncoder {

    /**
     * Sets the encoding of the provided form to UTF-8, see
     * https://code.google.com/p/google-web-toolkit/issues/detail?id=4682 for
     * details.
     * 
     * @param form
     */
    public void addUtf8Charset( final Form form ) {
        FormElement.as( form.getElement() ).setAcceptCharset( "UTF-8" );

        final Hidden field = new Hidden();
        field.setName( "utf8char" );
        field.setValue( "\u8482" );
        form.add( field );
    }
}
