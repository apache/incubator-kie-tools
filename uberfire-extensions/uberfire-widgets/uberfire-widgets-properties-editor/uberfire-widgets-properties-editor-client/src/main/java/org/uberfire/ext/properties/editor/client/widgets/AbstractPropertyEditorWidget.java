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

package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.properties.editor.client.PropertyEditorItemsWidget;

public class AbstractPropertyEditorWidget extends Composite {

    PropertyEditorItemsWidget parent;

    public void setParent( PropertyEditorItemsWidget parent ) {
        this.parent = parent;
    }

    public void setValidationError( String errorMessage ) {
        parent.setError( errorMessage );
    }

    public void clearOldValidationErrors() {
        parent.clearError();
    }

}
