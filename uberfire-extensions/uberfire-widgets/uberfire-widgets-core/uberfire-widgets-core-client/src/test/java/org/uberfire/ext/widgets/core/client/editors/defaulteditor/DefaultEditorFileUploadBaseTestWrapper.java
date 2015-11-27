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

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import java.util.Collections;
import java.util.Map;

public class DefaultEditorFileUploadBaseTestWrapper extends DefaultEditorFileUploadBase {

    boolean initialized;
    boolean isValid;

    public DefaultEditorFileUploadBaseTestWrapper() {
        super( false );
    }

    @Override
    void initForm() {
        if ( initialized ) {
            super.initForm();
        }
    }

    void forceInitForm() {
        this.initialized = true;
        initForm();
    }

    void setValid( final boolean isValid ) {
        this.isValid = isValid;
    }

    @Override
    boolean isValid() {
        return isValid;
    }

    @Override
    protected Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

}
