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
package org.kie.workbench.common.services.backend.compiler;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtilGit {

    private static Logger logger = LoggerFactory.getLogger(TestUtilGit.class);

    public static int findFreePort() {
        int port = 0;
        try {
            ServerSocket server = new ServerSocket(0);
            port = server.getLocalPort();
            server.close();
        } catch (IOException e) {
            Assert.fail("Can't find free port!");
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Found free port " + port);
        }
        return port;
    }
}
