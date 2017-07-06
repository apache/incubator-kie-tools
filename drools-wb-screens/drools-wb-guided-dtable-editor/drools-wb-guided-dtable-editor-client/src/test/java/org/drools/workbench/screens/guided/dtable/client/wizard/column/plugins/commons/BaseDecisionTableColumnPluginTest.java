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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.junit.Test;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BaseDecisionTableColumnPluginTest {

    @Test
    public void testIsNewColumnWhenColumnIsNull() {
        final BaseDecisionTableColumnPlugin plugin = makeBasePlugin();

        plugin.setOriginalColumnConfig52(null);

        assertTrue(plugin.isNewColumn());
    }

    @Test
    public void testIsNewColumnWhenColumnIsNotNull() {
        final BaseDecisionTableColumnPlugin plugin = makeBasePlugin();

        plugin.setOriginalColumnConfig52(mock(DTColumnConfig52.class));

        assertFalse(plugin.isNewColumn());
    }

    BaseDecisionTableColumnPlugin makeBasePlugin() {
        return new FakeBaseDecisionTableColumnPlugin();
    }

    private class FakeBaseDecisionTableColumnPlugin extends BaseDecisionTableColumnPlugin {

        FakeBaseDecisionTableColumnPlugin() {
            super(null,
                  null);
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public List<WizardPage> getPages() {
            return null;
        }

        @Override
        public Boolean generateColumn() {
            return null;
        }

        @Override
        public Type getType() {
            return null;
        }
    }
}
