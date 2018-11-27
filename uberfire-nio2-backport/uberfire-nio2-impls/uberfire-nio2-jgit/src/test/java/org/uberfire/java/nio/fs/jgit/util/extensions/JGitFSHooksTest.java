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

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.extensions.FileSystemHookExecutionContext;
import org.uberfire.java.nio.file.extensions.FileSystemHooks;
import org.uberfire.java.nio.file.extensions.FileSystemHooksConstants;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JGitFSHooksTest {

    private static final String FS_NAME = "dora";
    private static final Integer EXIT_CODE = 0;

    @Captor
    private ArgumentCaptor<FileSystemHookExecutionContext> contextArgumentCaptor;

    @Test
    public void executeFSHooksTest() {

        FileSystemHookExecutionContext ctx = new FileSystemHookExecutionContext(FS_NAME);

        testExecuteFSHooks(ctx, FileSystemHooks.ExternalUpdate);

        ctx.addParam(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE, EXIT_CODE);

        testExecuteFSHooks(ctx, FileSystemHooks.PostCommit);
    }

    private void testExecuteFSHooks(FileSystemHookExecutionContext ctx, FileSystemHooks hookType) {
        AtomicBoolean executedWithLambda = new AtomicBoolean(false);

        FileSystemHooks.FileSystemHook hook = spy(new FileSystemHooks.FileSystemHook() {
            @Override
            public void execute(FileSystemHookExecutionContext context) {
                assertEquals(FS_NAME, context.getFsName());
            }
        });

        FileSystemHooks.FileSystemHook lambdaHook = context -> {
            assertEquals(FS_NAME, context.getFsName());
            executedWithLambda.set(true);
        };

        JGitFSHooks.executeFSHooks(hook, hookType, ctx);
        JGitFSHooks.executeFSHooks(lambdaHook, hookType, ctx);

        verifyFSHook(hook, hookType);

        assertTrue(executedWithLambda.get());
    }

    @Test
    public void executeFSHooksArrayTest() {

        FileSystemHookExecutionContext ctx = new FileSystemHookExecutionContext(FS_NAME);

        testExecuteFSHooksArray(ctx, FileSystemHooks.ExternalUpdate);

        ctx.addParam(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE, EXIT_CODE);

        testExecuteFSHooksArray(ctx, FileSystemHooks.PostCommit);
    }

    private void testExecuteFSHooksArray(FileSystemHookExecutionContext ctx, FileSystemHooks hookType) {

        AtomicBoolean executedWithLambda = new AtomicBoolean(false);

        FileSystemHooks.FileSystemHook hook = spy(new FileSystemHooks.FileSystemHook() {
            @Override
            public void execute(FileSystemHookExecutionContext context) {
                assertEquals(FS_NAME, context.getFsName());
            }
        });

        FileSystemHooks.FileSystemHook lambdaHook = context -> {
            assertEquals(FS_NAME, context.getFsName());
            executedWithLambda.set(true);
        };

        JGitFSHooks.executeFSHooks(Arrays.asList(hook, lambdaHook), hookType, ctx);

        verifyFSHook(hook, hookType);

        assertTrue(executedWithLambda.get());
    }

    private void verifyFSHook(FileSystemHooks.FileSystemHook hook, FileSystemHooks hookType) {
        verify(hook).execute(contextArgumentCaptor.capture());

        FileSystemHookExecutionContext ctx = contextArgumentCaptor.getValue();

        Assertions.assertThat(ctx)
                .isNotNull()
                .hasFieldOrPropertyWithValue("fsName", FS_NAME);

        if (hookType.equals(FileSystemHooks.PostCommit)) {
            Assertions.assertThat(ctx.getParamValue(FileSystemHooksConstants.POST_COMMIT_EXIT_CODE))
                    .isNotNull()
                    .isEqualTo(EXIT_CODE);
        }
    }
}