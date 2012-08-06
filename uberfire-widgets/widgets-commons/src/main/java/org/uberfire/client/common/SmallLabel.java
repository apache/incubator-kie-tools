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

package org.uberfire.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;

public class SmallLabel extends HTML {

    interface SmallLabelTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class='form-field'>{0}</div>")
        SafeHtml message(SafeHtml message);
    }

    private static final SmallLabelTemplate TEMPLATE = GWT.create( SmallLabelTemplate.class );

    public SmallLabel() {
    }

    public SmallLabel(String text) {
        setText( text );
    }

    public void setText(final String text) {
        setHTML( TEMPLATE.message( new SafeHtml() {

            private static final long serialVersionUID = 510L;

            public String asString() {
                return text;
            }
        } ) );
    }
}
