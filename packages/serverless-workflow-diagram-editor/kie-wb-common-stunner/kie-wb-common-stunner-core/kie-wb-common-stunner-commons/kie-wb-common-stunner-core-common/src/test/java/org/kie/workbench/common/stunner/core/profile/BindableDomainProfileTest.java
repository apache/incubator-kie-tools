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

package org.kie.workbench.common.stunner.core.profile;

import java.util.function.Predicate;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class BindableDomainProfileTest {

    @Test
    public void testProfile() {
        BindableDomainProfile instance = BindableDomainProfile.build(BeanType1.class,
                                                                     BeanType2.class,
                                                                     BeanType3.class);
        Predicate<String> predicate = instance.definitionAllowedFilter();
        assertTrue(predicate.test(getDefinitionId(BeanType1.class)));
        assertTrue(predicate.test(getDefinitionId(BeanType2.class)));
        assertTrue(predicate.test(getDefinitionId(BeanType3.class)));
        assertFalse(predicate.test(getDefinitionId(BeanType4.class)));
    }

    private static final class BeanType1 {

    }

    private static final class BeanType2 {

    }

    private static final class BeanType3 {

    }

    private static final class BeanType4 {

    }
}
