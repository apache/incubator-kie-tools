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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.dashbuilder.external.model.ComponentParameter;
import org.dashbuilder.external.model.ExternalComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lesscss.deps.org.apache.commons.io.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExternalComponentLoaderImplTest {

    private static final String C1_ID = "c1_id";
    private static final String C2_ID = "c2_id";

    private static final String TEXT = "text";

    private static final String C2P1_LABEL = "c2p1 label";
    private static final String C2P1_DEFAULT = "c2p1 default";
    private static final String C2P1 = "c2p1";
    private static final String C2_ICON = "c2 icon";
    private static final String C2 = "c2";

    private static final String C1P1_LABEL = "c1p1 label";
    private static final String C1P1_DEFAULT = "c1p1 default";
    private static final String C1P1 = "c1p1";
    private static final String C1_ICON = "c1 icon";
    private static final String C1 = "c1";

    final String C1_MANIFEST = "{\n" +
                               "    \"name\": \"" + C1 + "\",\n" +
                               "    \"icon\": \"" + C1_ICON + "\",\n" +
                               "    \"parameters\": [\n" +
                               "        {\n" +
                               "            \"name\": \"" + C1P1 + "\",\n" +
                               "            \"type\": \"" + TEXT + "\",\n" +
                               "            \"defaultValue\": \"" + C1P1_DEFAULT + "\",\n" +
                               "            \"label\": \"" + C1P1_LABEL + "\"\n" +
                               "        }\n" +
                               "\n" +
                               "    ]\n" +
                               "}";

    final String C2_MANIFEST = "{\n" +
                               "    \"name\": \"" + C2 + "\",\n" +
                               "    \"icon\": \"" + C2_ICON + "\",\n" +
                               "    \"parameters\": [\n" +
                               "        {\n" +
                               "            \"name\": \"" + C2P1 + "\",\n" +
                               "            \"type\": \"text\",\n" +
                               "            \"defaultValue\": \"" + C2P1_DEFAULT + "\",\n" +
                               "            \"label\": \"" + C2P1_LABEL + "\"\n" +
                               "        }\n" +
                               "\n" +
                               "    ]\n" +
                               "}";

    private Path componentPath;

    ExternalComponentLoaderImpl externalComponentLoaderImpl;

    @Before
    public void init() throws URISyntaxException {
        String rootPath = ExternalComponentLoaderImplTest.class.getResource("/")
                                                               .getFile();
        externalComponentLoaderImpl = new ExternalComponentLoaderImpl();
        componentPath = Paths.get(rootPath, "components");

        System.setProperty(ExternalComponentLoaderImpl.EXTERNAL_COMP_DIR_PROP, componentPath.toString());
        System.setProperty(ExternalComponentLoaderImpl.EXTERNAL_COMP_ENABLE_PROP, Boolean.TRUE.toString());
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.deleteQuietly(componentPath.toFile());

    }

    @Test
    public void testBaseDirCreated() {
        assertFalse(Files.exists(componentPath));
        externalComponentLoaderImpl.init();
        assertTrue(Files.exists(componentPath));
    }

    @Test
    public void testLoad() {
        externalComponentLoaderImpl.init();
        createComponentsFiles();
        List<ExternalComponent> components = externalComponentLoaderImpl.load();
        assertEquals(2, components.size());
        ExternalComponent c1 = getComponent(components, C1_ID);
        ExternalComponent c2 = getComponent(components, C2_ID);

        assertEquals(1, c1.getParameters().size());
        assertEquals(C1, c1.getName());
        assertEquals(C1_ICON, c1.getIcon());

        ComponentParameter cp1 = c1.getParameters().get(0);
        assertEquals(C1P1, cp1.getName());
        assertEquals(C1P1_DEFAULT, cp1.getDefaultValue());
        assertEquals(C1P1_LABEL, cp1.getLabel());

        assertEquals(1, c2.getParameters().size());
        assertEquals(C2, c2.getName());
        assertEquals(C2_ICON, c2.getIcon());

        ComponentParameter cp2 = c2.getParameters().get(0);
        assertEquals(C2P1, cp2.getName());
        assertEquals(C2P1_DEFAULT, cp2.getDefaultValue());
        assertEquals(C2P1_LABEL, cp2.getLabel());
    }

    @Test
    public void testLoadWhenDisabled() throws IOException {
        System.setProperty(ExternalComponentLoaderImpl.EXTERNAL_COMP_ENABLE_PROP, Boolean.FALSE.toString());
        externalComponentLoaderImpl.init();
        assertFalse(Files.exists(componentPath));

        Files.createDirectory(componentPath);
        createComponentsFiles();

        assertTrue(externalComponentLoaderImpl.load().isEmpty());
    }

    private ExternalComponent getComponent(List<ExternalComponent> components, String id) {
        return components.stream().filter(c -> c.getId().equals(id)).findFirst().get();
    }

    private void createComponentsFiles() {
        try {
            Path c1 = Paths.get(componentPath.toString(), C1_ID, ExternalComponentLoaderImpl.DESCRIPTOR_FILE);
            c1.toFile().getParentFile().mkdirs();
            c1.toFile().createNewFile();
            Files.write(c1, C1_MANIFEST.getBytes());

            Path c2 = Paths.get(componentPath.toString(), C2_ID, ExternalComponentLoaderImpl.DESCRIPTOR_FILE);
            c2.toFile().getParentFile().mkdirs();
            c2.toFile().createNewFile();
            Files.write(c2, C2_MANIFEST.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}