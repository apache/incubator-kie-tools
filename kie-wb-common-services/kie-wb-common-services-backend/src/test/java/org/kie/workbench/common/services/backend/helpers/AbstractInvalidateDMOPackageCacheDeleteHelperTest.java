/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.helpers;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractInvalidateDMOPackageCacheDeleteHelperTest {

    @Mock
    private EventSourceMock<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache;

    private ResourceTypeDefinition resourceType = new ResourceTypeDefinition() {
        @Override
        public String getShortName() {
            return "name";
        }

        @Override
        public String getDescription() {
            return "description";
        }

        @Override
        public String getPrefix() {
            return "prefix";
        }

        @Override
        public String getSuffix() {
            return "suffix";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getSimpleWildcardPattern() {
            return "*." + getSuffix();
        }

        @Override
        public boolean accept(final Path path) {
            return path.getFileName().endsWith("." + getSuffix());
        }

        @Override
        public Category getCategory() {
            return new Others();
        }
    };

    private AbstractInvalidateDMOPackageCacheDeleteHelper helper;

    private static class MockInvalidateDMOPackageCacheDeleteHelper extends AbstractInvalidateDMOPackageCacheDeleteHelper<ResourceTypeDefinition> {

        public MockInvalidateDMOPackageCacheDeleteHelper(final ResourceTypeDefinition resourceType,
                                                         final Event<InvalidateDMOPackageCacheEvent> invalidateDMOPackageCache) {
            super(resourceType,
                  invalidateDMOPackageCache);
        }
    }

    @Before
    public void setup() {
        helper = new MockInvalidateDMOPackageCacheDeleteHelper(resourceType,
                                                               invalidateDMOPackageCache);
    }

    @Test
    public void checkMatchesResourceType() {
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("file." + resourceType.getSuffix());
        assertTrue(helper.supports(path));
    }

    @Test
    public void checkDoesNotMatchOtherResourceTypes() {
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("file.smurf");
        assertFalse(helper.supports(path));
    }

    @Test
    public void checkEventFiredWhenMatchesResourceType() {
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("file." + resourceType.getSuffix());

        helper.postProcess(path);

        verify(invalidateDMOPackageCache,
               times(1)).fire(any(InvalidateDMOPackageCacheEvent.class));
    }

    @Test
    public void checkEventNotFiredWhenNotMatchOtherResourceTypes() {
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("file.smurf");

        helper.postProcess(path);

        verify(invalidateDMOPackageCache,
               never()).fire(any(InvalidateDMOPackageCacheEvent.class));
    }
}
