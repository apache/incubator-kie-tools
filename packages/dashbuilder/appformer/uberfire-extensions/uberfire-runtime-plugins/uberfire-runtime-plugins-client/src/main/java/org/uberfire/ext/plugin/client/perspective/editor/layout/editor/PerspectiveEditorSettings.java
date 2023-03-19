/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PerspectiveEditorSettings {

    /**
     * The tags feature is disabled by default. In order to enabled it a explicit call to
     * {@link PerspectiveEditorSettings#setTagsEnabled(boolean)} is required.
     *
     * @since 2.0.0
     */
    private boolean tagsEnabled = false;

    public boolean isTagsEnabled() {
        return tagsEnabled;
    }

    public void setTagsEnabled(boolean tagsEnabled) {
        this.tagsEnabled = tagsEnabled;
    }
}

