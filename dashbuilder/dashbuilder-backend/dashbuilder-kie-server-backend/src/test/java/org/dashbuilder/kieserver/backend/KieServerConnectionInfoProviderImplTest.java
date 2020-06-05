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

package org.dashbuilder.kieserver.backend;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.kieserver.KieServerConnectionInfo;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.kieserver.backend.KieServerConnectionInfoProviderImpl.KieServerConfigurationKey;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.kieserver.backend.KieServerConnectionInfoProviderImpl.DATASET_PROP_PREFFIX;
import static org.dashbuilder.kieserver.backend.KieServerConnectionInfoProviderImpl.SERVER_TEMPLATE_PROP_PREFFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KieServerConnectionInfoProviderImplTest {

    private static final String SERVER_USER = "serveruser";
    private static final String SERVER_PASSWORD = "serverpassword";
    private static final String SERVER_TOKEN = "servertoken";
    private static final String SERVER_LOCATION = "serverlocation";
    private static final String DS_USER = "dsuser";
    private static final String DS_PASSWORD = "dspassword";
    private static final String DS_TOKEN = "dstoken";
    private static final String DS_LOCATION = "dslocation";

    private KieServerConnectionInfoProviderImpl kieServerConnectionInfoProvider;

    private final static String SERVER_ID = "server1";
    private final static String DATASET_NAME = "ds1";
    private final static String SERVER_TEMPLATE_PROP = SERVER_TEMPLATE_PROP_PREFFIX + "." + SERVER_ID + ".";
    private final static String DATASET_PROP = DATASET_PROP_PREFFIX + "." + DATASET_NAME + ".";

    private RemoteDataSetDef def;

    @Before
    public void init() {
        kieServerConnectionInfoProvider = new KieServerConnectionInfoProviderImpl();
        def = new RemoteDataSetDef();
        def.setName(DATASET_NAME);
        def.setServerTemplateId(SERVER_ID);
        clearProperties();
    }

    @Test
    public void serverListTest() {
        System.setProperty(KieServerConnectionInfoProviderImpl.SERVER_TEMPLATE_LIST_PROPERTY, "server1, server2, server3");
        List<String> serverTemplates = kieServerConnectionInfoProvider.serverTemplates();
        assertEquals(3, serverTemplates.size());
        List<String> expectedList = Arrays.asList("server1", "server2", "server3");
        assertEquals(expectedList, serverTemplates);
    }

    @Test
    public void emptyServerListTest() {
        System.setProperty(KieServerConnectionInfoProviderImpl.SERVER_TEMPLATE_LIST_PROPERTY, "");
        List<String> serverTemplates = kieServerConnectionInfoProvider.serverTemplates();
        assertTrue(serverTemplates.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void absentConfigurationErrorTest() {
        kieServerConnectionInfoProvider.verifiedConnectionInfo(def);
    }

    @Test(expected = RuntimeException.class)
    public void testServerMissingAuth() {
        setServerProp(KieServerConfigurationKey.LOCATION, "somelocation");
        kieServerConnectionInfoProvider.verifiedConnectionInfo(def);
    }

    @Test(expected = RuntimeException.class)
    public void testDataSetMissingAuth() {
        setDataSetProp(KieServerConfigurationKey.LOCATION, "somelocation");
        kieServerConnectionInfoProvider.verifiedConnectionInfo(def);
    }

    @Test
    public void testDsPropsPrecedence() {
        setDataSetProp(KieServerConfigurationKey.LOCATION, DS_LOCATION);
        setDataSetProp(KieServerConfigurationKey.USER, DS_USER);
        setDataSetProp(KieServerConfigurationKey.PASSWORD, DS_PASSWORD);

        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.USER, SERVER_USER);
        setServerProp(KieServerConfigurationKey.PASSWORD, SERVER_PASSWORD);
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);

        assertEquals(DS_LOCATION, connectionInfo.getLocation().get());
        assertEquals(DS_USER, connectionInfo.getUser().get());
        assertEquals(DS_PASSWORD, connectionInfo.getPassword().get());
        assertFalse(connectionInfo.isReplaceQuery());
    }

    @Test
    public void testServerTemplateProps() {
        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.USER, SERVER_USER);
        setServerProp(KieServerConfigurationKey.PASSWORD, SERVER_PASSWORD);
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);

        assertEquals(SERVER_LOCATION, connectionInfo.getLocation().get());
        assertEquals(SERVER_USER, connectionInfo.getUser().get());
        assertEquals(SERVER_PASSWORD, connectionInfo.getPassword().get());
        assertFalse(connectionInfo.isReplaceQuery());
    }

    @Test
    public void testDsTokenPropPrecedence() {
        setDataSetProp(KieServerConfigurationKey.LOCATION, DS_LOCATION);
        setDataSetProp(KieServerConfigurationKey.TOKEN, DS_TOKEN);

        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.TOKEN, SERVER_TOKEN);
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);

        assertEquals(DS_LOCATION, connectionInfo.getLocation().get());
        assertEquals(DS_TOKEN, connectionInfo.getToken().get());
    }

    @Test
    public void testServerToken() {

        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.TOKEN, SERVER_TOKEN);
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);

        assertEquals(SERVER_LOCATION, connectionInfo.getLocation().get());
        assertEquals(SERVER_TOKEN, connectionInfo.getToken().get());
    }
    
    @Test
    public void testReplaceQuery() {
        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.USER, SERVER_USER);
        setServerProp(KieServerConfigurationKey.PASSWORD, SERVER_PASSWORD);
        setServerProp(KieServerConfigurationKey.REPLACE_QUERY, "True");
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);
        assertTrue(connectionInfo.isReplaceQuery());
    }
    
    @Test
    public void testReplaceQueryFalse() {
        setServerProp(KieServerConfigurationKey.LOCATION, SERVER_LOCATION);
        setServerProp(KieServerConfigurationKey.USER, SERVER_USER);
        setServerProp(KieServerConfigurationKey.PASSWORD, SERVER_PASSWORD);
        setServerProp(KieServerConfigurationKey.REPLACE_QUERY, "false");
        KieServerConnectionInfo connectionInfo = kieServerConnectionInfoProvider.verifiedConnectionInfo(def);
        assertFalse(connectionInfo.isReplaceQuery());
    }

    private void setDataSetProp(KieServerConfigurationKey key, String value) {
        System.setProperty(DATASET_PROP + key.getValue(), value);
    }

    private void setServerProp(KieServerConfigurationKey key, String value) {
        System.setProperty(SERVER_TEMPLATE_PROP + key.getValue(), value);
    }

    private void clearProperties() {
        System.setProperty(KieServerConnectionInfoProviderImpl.SERVER_TEMPLATE_LIST_PROPERTY, "");
        for (KieServerConfigurationKey key : KieServerConfigurationKey.values()) {
            System.setProperty(DATASET_PROP + key.getValue(), "");
            System.setProperty(SERVER_TEMPLATE_PROP + key.getValue(), "");
        }
    }

}