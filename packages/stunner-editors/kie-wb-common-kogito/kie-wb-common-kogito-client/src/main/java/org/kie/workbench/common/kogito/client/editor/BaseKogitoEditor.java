/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.kogito.client.editor;

import elemental2.promise.Promise;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.mvp.PlaceRequest;

/**
 * This is a trimmed down {@code org.uberfire.ext.editor.commons.client.BaseEditor} for Kogito.
 * @param <CONTENT> The domain model of the editor
 */
public abstract class BaseKogitoEditor<CONTENT> {

    private boolean isReadOnly;

    private BaseEditorView baseEditorView;
    private PlaceRequest place;

    protected BaseKogitoEditor() {
        //CDI proxy
    }

    protected BaseKogitoEditor(final BaseEditorView baseView) {
        this.baseEditorView = baseView;
    }

    protected void init(final PlaceRequest place) {
        this.place = place;
        this.isReadOnly = this.place.getParameter("readOnly", null) != null;
    }

    protected PlaceRequest getPlaceRequest() {
        return place;
    }

    protected BaseEditorView getBaseEditorView() {
        return baseEditorView;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Used by Kogito to set the XML content of the editor.
     * @param path
     * Relative path to the content resource
     * @param value
     * Editor's content
     */
    public abstract Promise<Void> setContent(final String path, final String value);

    /**
     * Used by Kogito to get the XML content of the editor. This should return a {@link String}
     * representation of the editors content to persist to an underlying persistent store.
     */
    public abstract Promise<String> getContent();
}
