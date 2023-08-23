/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class TimeZonesProviderTest {

    private List<DMNSimpleTimeZone> timeZones;

    @Before
    public void setup() {
        final TimeZonesProvider timeZonesProvider = spy(new TimeZonesProvider());

        doCallRealMethod().when(timeZonesProvider).getTimeZones();
        doReturn(new String[]{"A", "B"}).when(timeZonesProvider).getNames();
        doReturn(10.0).when(timeZonesProvider).getOffset("A");
        doReturn(-20.0).when(timeZonesProvider).getOffset("B");
        doReturn("+10.0").when(timeZonesProvider).getOffsetString("A");
        doReturn("-20.0").when(timeZonesProvider).getOffsetString("B");

        this.timeZones = timeZonesProvider.getTimeZones();
    }

    @Test
    public void testGetTimeZones() {
        assertThat(timeZones).hasSize(2);
        assertThat(timeZones.get(0).getId()).isEqualTo("A");
        assertThat(timeZones.get(1).getId()).isEqualTo("B");
    }

    @Test
    public void testGetOffset() {
        assertThat(timeZones).hasSize(2);
        assertThat(timeZones.get(0).getOffset()).isEqualTo(10.0);
        assertThat(timeZones.get(1).getOffset()).isEqualTo(-20.0);
    }

    @Test
    public void testGetOffsetString() {
        assertThat(timeZones).hasSize(2);
        assertThat(timeZones.get(0).getOffsetString()).isEqualTo("+10.0");
        assertThat(timeZones.get(1).getOffsetString()).isEqualTo("-20.0");
    }
}
