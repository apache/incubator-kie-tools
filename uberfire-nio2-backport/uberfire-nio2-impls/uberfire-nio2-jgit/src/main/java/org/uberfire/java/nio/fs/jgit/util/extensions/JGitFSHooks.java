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

package org.uberfire.java.nio.fs.jgit.util.extensions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.extensions.FileSystemHookExecutionContext;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;

public class JGitFSHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFileSystemImpl.class);

    public static void executeFSHooks(Object fsHook, FileSystemHooks hookType, FileSystemHookExecutionContext ctx) {
        if(fsHook == null){
            return;
        }
        if (fsHook instanceof List) {
            List hooks = (List) fsHook;
            hooks.forEach(h -> executeHook(h, hookType, ctx));
        } else {
            executeHook(fsHook, hookType, ctx);
        }
    }

    private static void executeHook(Object hook, FileSystemHooks hookType, FileSystemHookExecutionContext ctx) {
        if (hook instanceof FileSystemHooks.FileSystemHook) {
            FileSystemHooks.FileSystemHook fsHook = (FileSystemHooks.FileSystemHook) hook;
            fsHook.execute(ctx);
        } else {
            LOGGER.error("Error executing FS Hook FS " + hookType + " on " + ctx.getFsName() +
                                 ". Callback methods should implement FileSystemHooks.FileSystemHook. ");
        }
    }
}
