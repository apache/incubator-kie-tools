/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.function.Function;
import java.util.function.Predicate;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.cm.client.palette.CaseManagementPaletteDefinitionBuilder.STAGES;
import static org.kie.workbench.common.stunner.cm.client.palette.CaseManagementPaletteDefinitionBuilder.SUBCASES;
import static org.kie.workbench.common.stunner.cm.client.palette.CaseManagementPaletteDefinitionBuilder.SUBPROCESSES;
import static org.kie.workbench.common.stunner.cm.client.palette.CaseManagementPaletteDefinitionBuilder.TASKS;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementPaletteDefinitionBuilderTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private StunnerTranslationService translationService;

    private CaseManagementPaletteDefinitionBuilder tested;
    private ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        paletteDefinitionBuilder = new ExpandedPaletteDefinitionBuilder(definitionUtils,
                                                                        definitionsRegistry,
                                                                        translationService);
        tested = new CaseManagementPaletteDefinitionBuilder(paletteDefinitionBuilder,
                                                            definitionManager);
    }

    @Test
    public void testDefinitionsAllowed() {
        tested.init();
        Predicate<String> itemFilter = tested.getPaletteDefinitionBuilder().getItemFilter();
        assertTrue(itemFilter.test(UserTask.class.getName()));
        assertTrue(itemFilter.test(BusinessRuleTask.class.getName()));
        assertTrue(itemFilter.test(ScriptTask.class.getName()));
        assertTrue(itemFilter.test(AdHocSubprocess.class.getName()));
        assertFalse(itemFilter.test(NoneTask.class.getName()));
        assertFalse(itemFilter.test(Lane.class.getName()));
        assertFalse(itemFilter.test(StartNoneEvent.class.getName()));
        assertFalse(itemFilter.test(EndNoneEvent.class.getName()));
    }

    @Test
    public void testNoGroupsByMorphing() {
        tested.init();
        Function<Object, MorphDefinition> morphDefinitionProvider =
                tested.getPaletteDefinitionBuilder().getMorphDefinitionProvider();
        assertNull(morphDefinitionProvider.apply(new UserTask()));
        assertNull(morphDefinitionProvider.apply(new BusinessRuleTask()));
        assertNull(morphDefinitionProvider.apply(new ScriptTask()));
        assertNull(morphDefinitionProvider.apply(new AdHocSubprocess()));
    }

    @Test
    public void testCategories() {
        tested.init();
        Function<Object, String> categoryProvider = tested.getPaletteDefinitionBuilder().getCategoryProvider();
        assertEquals(TASKS, categoryProvider.apply(new UserTask()));
        assertEquals(SUBPROCESSES, categoryProvider.apply(new EmbeddedSubprocess()));
        assertEquals(SUBCASES, categoryProvider.apply(new ReusableSubprocess()));
        assertEquals(STAGES, categoryProvider.apply(new AdHocSubprocess()));
    }
}
