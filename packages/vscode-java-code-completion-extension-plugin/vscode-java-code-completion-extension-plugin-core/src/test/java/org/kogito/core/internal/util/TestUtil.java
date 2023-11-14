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
package org.kogito.core.internal.util;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

/**
 * Shared Util class for Testing purposes
 */
public class TestUtil {

    public static String COMMON_RESOURCE_PATH = "src" + File.separator + "test" + File.separator + "resources" + File.separator;

    public static void mockWorkspace(String workspacePath, Runnable functionToTest) {
        try (MockedStatic<WorkspaceUtil> utilities = Mockito.mockStatic(WorkspaceUtil.class)) {
            utilities.when(WorkspaceUtil::getProjectLocation).thenReturn(workspacePath);
            functionToTest.run();
        }
    }
}
