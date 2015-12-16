/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dsltext.model;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.uberfire.commons.validation.PortablePreconditions;

public class DSLTextEditorContent {

    private String model;

    private Overview overview;

    public DSLTextEditorContent() {
    }

    public DSLTextEditorContent(String model, Overview overview) {
        this.model = PortablePreconditions.checkNotNull("model", model);
        this.overview = PortablePreconditions.checkNotNull("overview", overview);
    }

    public String getModel() {
        return model;
    }

    public Overview getOverview() {
        return overview;
    }

}
