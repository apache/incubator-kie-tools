/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.mvp;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * An Editor is an activity that is associated with a VFS path. It is expected that the editor will provide the end user
 * some means of editing and saving the resource represented by the VFS path.
 */
public interface WorkbenchEditorActivity extends WorkbenchActivity {

    public void onStartup(final ObservablePath path,
                          final PlaceRequest place);

    public void onSave();

    public boolean isDirty();

    @Override
    default ResourceType getResourceType() {
        return ActivityResourceType.EDITOR;
    }
}
