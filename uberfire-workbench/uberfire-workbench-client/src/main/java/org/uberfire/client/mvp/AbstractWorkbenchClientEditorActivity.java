/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.uberfire.client.annotations.WorkbenchClientEditor;

/**
 * Implementation of behaviour common to all workbench editor activities. Concrete implementations are typically not
 * written by hand; rather, they are generated from classes annotated with {@link WorkbenchClientEditor}.
 */
public abstract class AbstractWorkbenchClientEditorActivity extends AbstractWorkbenchActivity implements WorkbenchClientEditorActivity {

    public AbstractWorkbenchClientEditorActivity(final PlaceManager placeManager) {
        super(placeManager);
    }
}