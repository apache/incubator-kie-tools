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
package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.AliasFEELType;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.junit.Assert.assertEquals;

public class DMNUtilsTest {

    @Test
    public void getRootType() {
        SimpleTypeImpl simpleTypeAny = new SimpleTypeImpl(null, "tSimple", null, false, Collections.emptyList(), null, BuiltInType.UNKNOWN);
        assertEquals(BuiltInType.UNKNOWN, DMNUtils.getRootType(simpleTypeAny));

        Type aliasFeelType = new AliasFEELType("alias", BuiltInType.UNKNOWN);
        CompositeTypeImpl aliasType = new CompositeTypeImpl(null, "tSimple", null, false, Collections.emptyMap(), simpleTypeAny, aliasFeelType);
        assertEquals(BuiltInType.UNKNOWN, DMNUtils.getRootType(aliasType));

        Type notBuiltInType = new AliasFEELType("notBuiltIn", BuiltInType.UNKNOWN);
        SimpleTypeImpl notBuiltIn = new SimpleTypeImpl(null, "tSimple", null, false, Collections.emptyList(), null, notBuiltInType);
        assertEquals(notBuiltInType, DMNUtils.getRootType(notBuiltIn));
    }

    @Test
    public void getDMNTypeName() {
        SimpleTypeImpl simpleTypeAny = new SimpleTypeImpl(null, "tSimple", null, false, Collections.emptyList(), null, BuiltInType.UNKNOWN);
        assertEquals("tSimple", DMNUtils.getDMNTypeName(simpleTypeAny));

        Type aliasFeelType = new AliasFEELType("alias", BuiltInType.UNKNOWN);
        CompositeTypeImpl aliasType = new CompositeTypeImpl(null, "tComposite", null, false, Collections.emptyMap(), simpleTypeAny, aliasFeelType);
        assertEquals("tSimple", DMNUtils.getDMNTypeName(aliasType));

        Type notBuiltInType = new AliasFEELType("notBuiltIn", BuiltInType.UNKNOWN);
        SimpleTypeImpl notBuiltIn = new SimpleTypeImpl(null, "tSimple2", null, false, Collections.emptyList(), simpleTypeAny, notBuiltInType);
        assertEquals("tSimple", DMNUtils.getDMNTypeName(notBuiltIn));
    }
}