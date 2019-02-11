/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.lucene.index.directory;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.model.KCluster;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryFactoryTest {

    DirectoryFactory factory;

    @Mock
    DirectoryType type;
    @Mock
    Analyzer analyzer;
    @Mock
    File hostingDir;
    @Mock
    File spaceDir;
    @Mock
    File projectDir;
    @Mock
    File masterBranchDir;
    @Mock
    File developBranchDir;

    @Before
    public void setup() {
        when(hostingDir.isDirectory()).thenReturn(true);
        when(hostingDir.listFiles()).thenReturn(new File[]{spaceDir});

        when(spaceDir.isDirectory()).thenReturn(true);
        when(spaceDir.listFiles()).thenReturn(new File[]{projectDir});
        when(spaceDir.getName()).thenReturn("myteam");

        when(projectDir.isDirectory()).thenReturn(true);
        when(projectDir.listFiles()).thenReturn(new File[]{masterBranchDir, developBranchDir});
        when(projectDir.getName()).thenReturn("myproject");
        when(projectDir.getParentFile()).thenReturn(spaceDir);

        when(masterBranchDir.isDirectory()).thenReturn(true);
        when(masterBranchDir.getName()).thenReturn("master");
        when(masterBranchDir.getParentFile()).thenReturn(projectDir);

        when(developBranchDir.isDirectory()).thenReturn(true);
        when(developBranchDir.getName()).thenReturn("develop");
        when(developBranchDir.getParentFile()).thenReturn(projectDir);

        when(type.newIndex(any(),
                           any())).thenReturn(mock(LuceneIndex.class));

        factory = new DirectoryFactory(type,
                                       analyzer,
                                       hostingDir);
    }

    @Test(expected = IllegalStateException.class)
    public void loadsExistingIndicesOnStartup() throws Exception {
        KCluster kcluster = new KClusterImpl("myteam/myproject/master");
        // Should throw error from index existing already.
        factory.newCluster(kcluster);
    }

    @Test
    public void testClusterIdOf() {
        String clusterId = DirectoryFactory.clusterIdOf(masterBranchDir);
        assertThat(clusterId).isEqualTo("myteam/myproject/master");
    }

    @Test
    public void testLoadIndexes() {
        this.factory.loadIndexes(type,
                                 analyzer,
                                 hostingDir);
        List<String> indexes = this.factory.getIndexes().keySet().stream().map(KCluster::getClusterId).collect(Collectors.toList());
        assertThat(indexes).containsExactly("myteam/myproject/master",
                                            "myteam/myproject/develop");
    }
}
