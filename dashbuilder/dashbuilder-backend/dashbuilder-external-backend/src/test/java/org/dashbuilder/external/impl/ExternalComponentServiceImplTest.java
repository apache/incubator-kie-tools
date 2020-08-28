/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.external.impl;

import java.util.Collections;

import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ExternalComponentLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ExternalComponentServiceImplTest {
    
    private static final String C1_ID = "c1";
    private static final String C2_ID = "c2";

    @Mock
    ExternalComponentLoader loader;
      
    @InjectMocks
    ExternalComponentServiceImpl externalComponentServiceImpl;
    
    @Test
    public void testById() {
        ExternalComponent c1 = new ExternalComponent(C1_ID, "c1 name", "c1 icon", false, Collections.emptyList());
        ExternalComponent c2 = new ExternalComponent(C2_ID, "c2 name", "c2 icon", false, Collections.emptyList());
        
        Mockito.when(loader.load()).thenReturn(asList(c1, c2));
        
        assertTrue(externalComponentServiceImpl.byId(C1_ID).isPresent());
        assertTrue(externalComponentServiceImpl.byId(C2_ID).isPresent());
        assertFalse(externalComponentServiceImpl.byId("do not exist").isPresent());
    }
    

}