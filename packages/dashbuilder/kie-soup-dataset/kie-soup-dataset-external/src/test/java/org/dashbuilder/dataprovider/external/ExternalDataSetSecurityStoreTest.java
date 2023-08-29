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

package org.dashbuilder.dataprovider.external;

import org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.SecurityType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.PASSWORD_PROP;
import static org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.TOKEN_PROP;
import static org.dashbuilder.dataprovider.external.ExternalDataSetSecurityStore.USER_PROP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalDataSetSecurityStoreTest {

    static final String DEF_UUID = "defUUID";
    static final String DEF_NAME = "defName";

    static final String USER_DATASET_NAME_PROP = String.format(USER_PROP, DEF_NAME);
    static final String PASSWORD_DATASET_NAME_PROP = String.format(PASSWORD_PROP, DEF_NAME);
    static final String TOKEN_DATASET_NAME_PROP = String.format(TOKEN_PROP, DEF_NAME);

    static final String USER_DATASET_UUID_PROP = String.format(USER_PROP, DEF_UUID);
    static final String PASSWORD_DATASET_UUID_PROP = String.format(PASSWORD_PROP, DEF_UUID);
    static final String TOKEN_DATASET_UUID_PROP = String.format(TOKEN_PROP, DEF_UUID);

    @Before
    public void setup() {
        System.clearProperty(USER_DATASET_NAME_PROP);
        System.clearProperty(PASSWORD_DATASET_NAME_PROP);
        System.clearProperty(TOKEN_DATASET_NAME_PROP);
        System.clearProperty(USER_DATASET_UUID_PROP);
        System.clearProperty(PASSWORD_DATASET_UUID_PROP);
        System.clearProperty(TOKEN_DATASET_UUID_PROP);
    }
    
    @Test
    public void testNoSecurity() {
        var def = mock(DataSetDef.class);
        when(def.getName()).thenReturn("no security def name");
        when(def.getUUID()).thenReturn("no security def uuid");
        var op = ExternalDataSetSecurityStore.get(def);
        assertTrue(op.isEmpty());
    }

    @Test
    public void testGetUserPasswordByUUID() {
        final var user = "USER";
        final var password = "PASSWORD";
        System.setProperty(USER_DATASET_UUID_PROP, user);
        System.setProperty(PASSWORD_DATASET_UUID_PROP, password);
        
        var def = mock(DataSetDef.class);
        when(def.getName()).thenReturn("some name");
        when(def.getUUID()).thenReturn(DEF_UUID);
        
        var op = ExternalDataSetSecurityStore.get(def);
        var secInfo = op.get();
        assertEquals(user, secInfo.getUsername());
        assertEquals(password, secInfo.getPassword());
        assertEquals(SecurityType.BASIC, secInfo.getType());
        assertNull(secInfo.getToken());
    }
    
    @Test
    public void testGetUserPasswordByName() {
        final var user = "USER";
        final var password = "PASSWORD";
        System.setProperty(USER_DATASET_NAME_PROP, user);
        System.setProperty(PASSWORD_DATASET_NAME_PROP, password);
        
        var def = mock(DataSetDef.class);
        when(def.getName()).thenReturn(DEF_NAME);
        when(def.getUUID()).thenReturn("some UUID");
        
        var op = ExternalDataSetSecurityStore.get(def);
        var secInfo = op.get();
        assertEquals(user, secInfo.getUsername());
        assertEquals(password, secInfo.getPassword());
        assertEquals(SecurityType.BASIC, secInfo.getType());
        assertNull(secInfo.getToken());
    }
    
    
    @Test
    public void testGetTokenByUUID() {
        final var token = "TOKEN";
        System.setProperty(TOKEN_DATASET_UUID_PROP, token);
        
        var def = mock(DataSetDef.class);
        when(def.getName()).thenReturn("some name");
        when(def.getUUID()).thenReturn(DEF_UUID);
        
        var op = ExternalDataSetSecurityStore.get(def);
        var secInfo = op.get();
        assertEquals(token, secInfo.getToken());
        assertEquals(SecurityType.TOKEN, secInfo.getType());
        assertNull(secInfo.getUsername());
        assertNull(secInfo.getPassword());
    }
    
    @Test
    public void testGetTokenByNAME() {
        final var token = "TOKEN";
        System.setProperty(TOKEN_DATASET_NAME_PROP, token);
        
        var def = mock(DataSetDef.class);
        when(def.getName()).thenReturn(DEF_NAME);
        when(def.getUUID()).thenReturn("some uuid");
        
        var op = ExternalDataSetSecurityStore.get(def);
        var secInfo = op.get();
        assertEquals(token, secInfo.getToken());
        assertEquals(SecurityType.TOKEN, secInfo.getType());
        assertNull(secInfo.getUsername());
        assertNull(secInfo.getPassword());
    }

}
