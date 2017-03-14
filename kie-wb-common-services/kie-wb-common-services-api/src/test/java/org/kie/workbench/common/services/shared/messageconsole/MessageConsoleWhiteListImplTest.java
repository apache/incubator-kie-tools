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
package org.kie.workbench.common.services.shared.messageconsole;

import org.guvnor.messageconsole.whitelist.MessageConsoleWhiteList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageConsoleWhiteListImplTest {

    private MessageConsoleWhiteList list;

    @Before
    public void setUp() throws Exception {
        list = new MessageConsoleWhiteListImpl();
    }

    @Test
    public void denial() throws Exception {
        assertFalse(list.contains("Not white listed"));
    }

    @Test
    public void library() throws Exception {
        assertTrue(list.contains("LibraryPerspective"));
    }

    @Test
    public void authoring() throws Exception {
        assertTrue(list.contains("AuthoringPerspective"));
    }

    @Test
    public void authoringNoContext() throws Exception {
        assertTrue(list.contains("AuthoringPerspectiveNoContext"));
    }
}