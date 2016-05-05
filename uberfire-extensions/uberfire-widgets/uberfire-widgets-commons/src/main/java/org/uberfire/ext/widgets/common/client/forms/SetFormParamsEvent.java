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

package org.uberfire.ext.widgets.common.client.forms;

import java.util.Map;

//This class is not @Portable since we only want it client-side
public class SetFormParamsEvent {

    private Map<String, String> params;
    private boolean readOnly;

    public SetFormParamsEvent() {
    }

    public SetFormParamsEvent( Map<String, String> params,
                               boolean readOnly ) {
        this.params = params;
        this.readOnly = readOnly;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

}
