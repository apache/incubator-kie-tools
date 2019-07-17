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

package org.guvnor.structure.backend.repositories.git;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.structure.repositories.impl.GitMetadataImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;

import static org.jgroups.util.Util.assertEquals;
import static org.jgroups.util.Util.assertFalse;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GitMetadataImplStoreTest {

    private GitMetadataStoreImpl metadataStore;

    @Mock
    private ObjectStorage storage;
    private Map<String, GitMetadataImpl> metadatas;
    private SpacesAPIImpl spaces = new SpacesAPIImpl();

    @Before
    public void setUp() throws Exception {
        metadataStore = new GitMetadataStoreImpl(storage,
                                                 spaces);

        metadatas = new HashMap<>();

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            GitMetadataImpl metadata = invocationOnMock.getArgumentAt(1,
                                                                      GitMetadataImpl.class);
            storage.write(key, metadata, true);

            return null;
        }).when(storage).write(anyString(), any());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            GitMetadataImpl metadata = invocationOnMock.getArgumentAt(1,
                                                                      GitMetadataImpl.class);
            metadatas.put(key,
                          metadata);
            return null;
        }).when(storage).write(anyString(),
                               any(),
                               anyBoolean());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            return metadatas.get(key);
        }).when(storage).read(anyString());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            return metadatas.remove(key);
        }).when(storage).delete(anyString());
    }

    @Test
    public void testStorageInitialization() {
        metadataStore.init();
        verify(storage).init(eq(metadataStore.getMetadataFS()));
    }

    @Test
    public void testCreatesRightPathToSave() {
        metadataStore.write("test/repo",
                            "");
        verify(storage).write(eq("/test/repo.metadata"),
                              anyObject(),
                              anyBoolean());
    }

    @Test
    public void testFixRightPathToSave() {
        metadataStore.write("/test/repo",
                            "");
        verify(storage).write(eq("/test/repo.metadata"),
                              anyObject(),
                              anyBoolean());
    }

    @Test
    public void testWriteNewMetadataWithoutOrigin() {

        metadataStore.write("test/repo");
        assertEquals("test/repo",
                     metadatas.get("/test/repo.metadata").getName());
    }

    @Test
    public void testWriteNewMetadataWithOrigin() {

        metadataStore.write("test/repo",
                            "other/repo");

        assertEquals("test/repo",
                     metadatas.get("/test/repo.metadata").getName());
        assertEquals("other/repo",
                     metadatas.get("/other/repo.metadata").getName());
    }

    @Test
    public void testChangeOriginFromMetedata() {

        metadataStore.write("test/repo",
                            "other/repo");
        assertEquals("test/repo",
                     metadatas.get("/test/repo.metadata").getName());
        assertEquals("other/repo",
                     metadatas.get("/test/repo.metadata").getOrigin());
        assertEquals("other/repo",
                     metadatas.get("/other/repo.metadata").getName());

        metadataStore.write("test/repo",
                            "other/otherOrigin");
        assertEquals("other/otherOrigin",
                     metadatas.get("/test/repo.metadata").getOrigin());
        assertEquals(0,
                     metadatas.get("/other/repo.metadata").getForks().size());
    }

    @Test
    public void testWriteWithNullOrigin() {

        String origin = null;
        metadataStore.write("test/repo",
                            origin);
        assertEquals("test/repo",
                     metadatas.get("/test/repo.metadata").getName());
    }

    @Test
    public void testWriteTwoForks() {

        metadataStore.write("test/repo",
                            "origin/repo");
        metadataStore.write("fork/repo",
                            "origin/repo");

        assertEquals(3,
                     metadatas.size());
        assertEquals("test/repo",
                     metadatas.get("/test/repo.metadata").getName());
        assertEquals("fork/repo",
                     metadatas.get("/fork/repo.metadata").getName());
        assertEquals("origin/repo",
                     metadatas.get("/origin/repo.metadata").getName());

        assertEquals(2,
                     metadatas.get("/origin/repo.metadata").getForks().size());
    }

    @Test
    public void testComplexForkTracking() {

        metadataStore.write("b/repo",
                            "a/repo");
        metadataStore.write("c/repo",
                            "b/repo");
        metadataStore.write("d/repo",
                            "c/repo");

        assertEquals("c/repo",
                     metadataStore.read("d/repo").get().getOrigin());
        assertEquals("b/repo",
                     metadataStore.read("c/repo").get().getOrigin());
        assertEquals("a/repo",
                     metadataStore.read("b/repo").get().getOrigin());

        assertEquals("b/repo",
                     metadataStore.read("a/repo").get().getForks().get(0));
        assertEquals("c/repo",
                     metadataStore.read("b/repo").get().getForks().get(0));
        assertEquals("d/repo",
                     metadataStore.read("c/repo").get().getForks().get(0));
    }

    @Test
    public void testSimpleDelete() {

        metadataStore.write("a/repo",
                            "");
        assertEquals("",
                     metadataStore.read("a/repo").get().getOrigin());

        metadataStore.delete("a/repo");
        assertFalse(metadataStore.read("a/repo").isPresent());
    }

    @Test
    public void testComplexDelete() {

        metadataStore.write("b/repo",
                            "a/repo");
        metadataStore.write("c/repo",
                            "b/repo");
        metadataStore.write("d/repo",
                            "c/repo");

        metadataStore.delete("c/repo");

        assertEquals("",
                     metadataStore.read("d/repo").get().getOrigin());
        assertEquals("a/repo",
                     metadataStore.read("b/repo").get().getOrigin());

        assertEquals("b/repo",
                     metadataStore.read("a/repo").get().getForks().get(0));
        assertEquals(0,
                     metadataStore.read("b/repo").get().getForks().size());
        assertFalse(metadataStore.read("c/repo").isPresent());
    }
}
