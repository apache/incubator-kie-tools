/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.editors.types.query;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueFileExtensionIndexTerm.TERM;
import static org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType.REGEXP;

public class DMNValueFileExtensionIndexTermTest {

    private DMNValueFileExtensionIndexTerm indexTerm;

    @Before
    public void setup() {
        indexTerm = new DMNValueFileExtensionIndexTerm();
    }

    @Test
    public void testGetTerm() {
        assertEquals(TERM, indexTerm.getTerm());
    }

    @Test
    public void testGetValue() {
        assertEquals(".*(dmn)", indexTerm.getValue());
    }

    @Test
    public void testGetSearchType() {
        assertEquals(REGEXP, indexTerm.getSearchType());
    }
}
