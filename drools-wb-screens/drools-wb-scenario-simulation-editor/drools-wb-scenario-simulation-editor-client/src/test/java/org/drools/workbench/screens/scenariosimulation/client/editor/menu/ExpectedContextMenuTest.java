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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(GwtMockitoTestRunner.class)
public class ExpectedContextMenuTest extends AbstractMenuTest {

    @Before
    public void setup() {
        abstractColumnMenuPresenter = new ExpectedContextMenu();
        super.setup();
    }

    @Test
    public void initMenu() {
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_COLUMN);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_COLUMN);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_INSTANCE);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DUPLICATE_INSTANCE);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_LABEL);
        assertNull(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_I18N);
        abstractColumnMenuPresenter.initMenu();
        assertEquals(ExpectedContextMenu.EXPECTCONTEXTMENU_EXPECT, abstractColumnMenuPresenter.COLUMNCONTEXTMENU_COLUMN);
        assertEquals(ExpectedContextMenu.EXPECTCONTEXTMENU_INSERT_COLUMN_LEFT, abstractColumnMenuPresenter.COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT);
        assertEquals(ExpectedContextMenu.EXPECTCONTEXTMENU_INSERT_COLUMN_RIGHT, abstractColumnMenuPresenter.COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT);
        assertEquals(ExpectedContextMenu.EXPECTCONTEXTMENU_DELETE_COLUMN, abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_COLUMN);
        assertEquals(ExpectedContextMenu.EXPECTCONTEXTMENU_DELETE_INSTANCE, abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_INSTANCE);
        assertEquals(abstractColumnMenuPresenter.constants.expect().toUpperCase(), abstractColumnMenuPresenter.COLUMNCONTEXTMENU_LABEL);
        assertEquals("expect", abstractColumnMenuPresenter.COLUMNCONTEXTMENU_I18N);
    }
}