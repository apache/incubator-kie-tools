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

package org.kie.workbench.common.workbench.client.error;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class GenericErrorTimeControllerTest {

    @Spy
    private GenericErrorTimeController genericErrorTimeController;

    @Test
    public void checkExpiredWhenNeverSetTest() {
        assertTrue(genericErrorTimeController.isExpired());
    }

    @Test
    public void checkExpiredWhenNotExpiredTest() {
        doReturn(1000L).when(genericErrorTimeController).getExpiresOn();
        doReturn(500L).when(genericErrorTimeController).now();

        assertFalse(genericErrorTimeController.isExpired());
    }

    @Test
    public void checkExpiredWhenExpiredTest() {
        doReturn(1000L).when(genericErrorTimeController).getExpiresOn();
        doReturn(2000L).when(genericErrorTimeController).now();

        assertTrue(genericErrorTimeController.isExpired());
    }

    @Test
    public void setTimeoutTest() {
        final TimeAmount tenMinutes = TimeAmount.TEN_MINUTES;

        doReturn(1234L).when(genericErrorTimeController).now();

        genericErrorTimeController.setTimeout(tenMinutes);

        assertEquals(1234L + tenMinutes.getTimeAmount(), genericErrorTimeController.getExpiresOn());
    }
}
