/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.project.migration.cli.maven;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.maven.model.Model;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PomEditorWithGitTest {

    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();
    private IOService ioService;
    private PomMigrationEditor editor;
    private WeldContainer weldContainer;
    private MigrationServicesCDIWrapper cdiWrapper;

    @Before
    public void setUp() throws Exception {
        weldContainer = new Weld().initialize();
        cdiWrapper = weldContainer.instance().select(MigrationServicesCDIWrapper.class).get();
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
        editor = new PomMigrationEditor();
    }

    @After
    public void tearDown() throws IOException {
        fileSystemTestingUtils.cleanup();
        if (weldContainer != null) {
            weldContainer.close();
        }
    }

    @Test
    public void testPomEditor() throws Exception {
        final String repoName = "myrepoxxxx";
        HashMap<String, Object> env = new HashMap<>();
        env.put("init", Boolean.TRUE);
        env.put("internal", Boolean.TRUE);
        final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(URI.create("git://" + repoName), env);

        ioService.startBatch(fs);
        ioService.write(fs.getPath("/pom.xml"), new String(java.nio.file.Files.readAllBytes(new File("src/test/projects/generic/pom.xml").toPath())));
        ioService.endBatch();

        Path tmpCloned = Files.createTempDirectory("cloned");
        final File gitClonedFolder = new File(tmpCloned.toFile(), ".clone.git");

        final Git cloned = Git.cloneRepository().setURI(fs.getGit().getRepository().getDirectory().toURI().toString()).setBare(false).setDirectory(gitClonedFolder).call();

        assertNotNull(cloned);

        Path pomPath = Paths.get("file://" + gitClonedFolder.toString() + "/pom.xml");
        byte[] encoded = Files.readAllBytes(pomPath);
        String pomOriginal = new String(encoded, StandardCharsets.UTF_8);

        Model model = editor.updatePom(pomPath, cdiWrapper);
        assertNotNull(model);

        PullCommand pc = cloned.pull().setRemote("origin").setRebase(Boolean.TRUE);
        PullResult pullRes = pc.call();
        assertEquals(pullRes.getRebaseResult().getStatus(), RebaseResult.Status.UP_TO_DATE);

        RebaseCommand rb = cloned.rebase().setUpstream("origin/master");
        RebaseResult rbResult = rb.setPreserveMerges(true).call();
        assertTrue(rbResult.getStatus().isSuccessful());

        pomPath = Paths.get("file://" + gitClonedFolder.toString() + "/pom.xml");
        encoded = Files.readAllBytes(pomPath);
        String pomUpdated = new String(encoded, StandardCharsets.UTF_8);

        assertFalse(pomOriginal.equals(pomUpdated));
    }
}
