/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.api.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class LibraryValueFileExtensionIndexTermTest {

    @Test(expected = IllegalArgumentException.class)
    public void testZeroExtension() {
        List<String> extensions = Collections.emptyList();
        LibraryValueFileExtensionIndexTerm term = new LibraryValueFileExtensionIndexTerm(extensions);
    }

    @Test
    public void testOneExtension() {
        List<String> extensions = Arrays.asList("xml");
        LibraryValueFileExtensionIndexTerm term = new LibraryValueFileExtensionIndexTerm(extensions);
        assertEquals(".*(xml)",
                     term.getValue());
    }

    @Test
    public void testSeveralExtensions() {
        List<String> extensions = Arrays.asList("xml",
                                                "java",
                                                "drl");
        LibraryValueFileExtensionIndexTerm term = new LibraryValueFileExtensionIndexTerm(extensions);
        assertEquals(".*(xml|java|drl)",
                     term.getValue());
    }
}