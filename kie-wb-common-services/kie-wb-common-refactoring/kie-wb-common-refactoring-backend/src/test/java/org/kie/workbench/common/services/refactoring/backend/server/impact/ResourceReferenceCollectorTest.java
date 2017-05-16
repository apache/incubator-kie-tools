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
package org.kie.workbench.common.services.refactoring.backend.server.impact;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.*;

public class ResourceReferenceCollectorTest {

    private class TestResourceReferenceCollector extends ResourceReferenceCollector {

    }

    private TestResourceReferenceCollector collector;

    @Before
    public void setup() {
        collector = new TestResourceReferenceCollector();
    }

    @Test
    public void checkAddResourceReference() {
        whenCollectorHasResourceReference( collector,
                                           "f.q.c.n",
                                           ResourceType.JAVA );

        thenCollectorHasResourceReferences( collector,
                                            1 );
        thenResourceReferenceHasPartReferences( collector.getResourceReferences().stream().findFirst().get(),
                                                0 );
    }

    @Test
    public void checkAddResourceReferences() {
        final TestResourceReferenceCollector siblingCollector = new TestResourceReferenceCollector();

        whenCollectorHasResourceReference( collector,
                                           "f.q.c.n",
                                           ResourceType.JAVA );
        whenCollectorHasResourceReference( siblingCollector,
                                           "f.q.c.n",
                                           ResourceType.JAVA );
        whenCollectorHasSiblingCollectorsResourceReference( collector,
                                                            siblingCollector );

        thenCollectorHasResourceReferences( siblingCollector,
                                            1 );
        thenResourceReferenceHasPartReferences( siblingCollector.getResourceReferences().stream().findFirst().get(),
                                                0 );
    }

    private void whenCollectorHasResourceReference( final ResourceReferenceCollector collector,
                                                    final String fqcn,
                                                    final ResourceType type ) {
        collector.addResourceReference( fqcn,
                                        type );
    }

    private void whenCollectorHasSiblingCollectorsResourceReference( final ResourceReferenceCollector collector,
                                                                     final ResourceReferenceCollector sibling ) {
        collector.addResourceReferences( sibling );
    }

    private void thenCollectorHasResourceReferences( final ResourceReferenceCollector collector,
                                                     final int resourceReferencesCount ) {
        final Collection<ResourceReference> resourceReferences = collector.getResourceReferences();
        assertNotNull( resourceReferences );
        assertEquals( resourceReferencesCount,
                      resourceReferences.size() );
    }

    private void thenResourceReferenceHasPartReferences( final ResourceReference resourceReference,
                                                         final int partReferencesCount ) {
        assertNotNull( resourceReference );
        assertEquals( ResourceType.JAVA,
                      resourceReference.getResourceType() );
        assertEquals(
                partReferencesCount,
                resourceReference.getPartReferences().size() );
    }

}
