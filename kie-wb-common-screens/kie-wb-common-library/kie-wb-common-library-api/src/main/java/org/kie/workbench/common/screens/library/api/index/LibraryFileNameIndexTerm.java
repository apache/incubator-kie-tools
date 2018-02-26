/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.api.index;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;

@Portable
public class LibraryFileNameIndexTerm implements IndexTerm {

    public static final String TERM = "libraryFileName";

    @Override
    public String getTerm() {
        return TERM;
    }
}
