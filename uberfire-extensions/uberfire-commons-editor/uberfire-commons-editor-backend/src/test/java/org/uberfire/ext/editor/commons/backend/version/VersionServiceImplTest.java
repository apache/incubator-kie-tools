/*
 * Copyright 2019 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.editor.commons.backend.version;

import java.net.URISyntaxException;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.uberfire.java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@RunWith(MockitoJUnitRunner.class)
public class VersionServiceImplTest {

    @Mock
    private IOService ioService;
    @Mock
    private SessionInfo sessionInfo;
    @Mock
    private PathResolver pathResolver;
    @Mock
    private VersionUtil versionUtil;
    @InjectMocks
    private VersionServiceImpl versionService;

    @Before
    public void setUp() throws Exception {
        final User user = mock(User.class);
        doReturn("user id").when(user).getIdentifier();
        doReturn("session id").when(sessionInfo).getId();
        doReturn(user).when(sessionInfo).getIdentity();
    }

    @Test
    public void restore() throws URISyntaxException {
        final Path path = new PathFactory.PathImpl("foo.txt", "default://foo.txt");
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);

        doReturn(nioPath).when(pathResolver).resolveMainFilePath(any());

        final InOrder order = inOrder(ioService);

        versionService.restore(path,
                               "Restore comment",
                               "master");

        order.verify(ioService).startBatch(nioPath.getFileSystem());

        order.verify(ioService).copy(eq(nioPath),
                                     any(),
                                     eq(REPLACE_EXISTING),
                                     any(CommentedOption.class));

        order.verify(ioService).endBatch();
    }
}