/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.storage;

import org.junit.Test;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.backend.service.SpecManagementServiceCDI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SpecManagementServiceCDITest {

    @Test
    public void testIsContainerIdValid() {
        final SpecManagementServiceCDI specManagementService = spy( new SpecManagementServiceCDI() );

        final ServerTemplate serverTemplate = mock( ServerTemplate.class );
        when( serverTemplate.getContainerSpec( any() ) ).thenReturn( null );
        when( specManagementService.getServerTemplate( "templateId" ) ).thenReturn( serverTemplate );

        assertTrue( specManagementService.isContainerIdValid( "templateId", "111" ) );
        assertTrue( specManagementService.isContainerIdValid( "templateId", "xxx" ) );
        assertTrue( specManagementService.isContainerIdValid( "templateId", "aaa:bbb:ccc" ) );
        assertTrue( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0" ) );
        assertTrue( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0-SNAPSHOT" ) );

        assertFalse( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0/SNAPSHOT" ) );
        assertFalse( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0&SNAPSHOT" ) );
        assertFalse( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0+SNAPSHOT" ) );
        assertFalse( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0`SNAPSHOT" ) );
        assertFalse( specManagementService.isContainerIdValid( "templateId", "org.jbpm:Evaluation:1.0~SNAPSHOT" ) );
        assertFalse( specManagementService.isContainerIdValid( "templateId", "aa&&aa" ) );
    }

}
