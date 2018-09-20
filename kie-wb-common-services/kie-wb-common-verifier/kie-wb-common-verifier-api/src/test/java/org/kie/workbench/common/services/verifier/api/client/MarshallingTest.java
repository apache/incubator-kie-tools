/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.verifier.api.client;

import java.util.Collections;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.ImpossibleMatchIssue;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.MultipleValuesForOneActionIssue;
import org.drools.verifier.api.reporting.RedundantConditionsIssue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.SingleHitLostIssue;
import org.drools.verifier.api.reporting.ValueForActionIsSetTwiceIssue;
import org.drools.verifier.api.reporting.ValueForFactFieldIsSetTwiceIssue;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMarshalling;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MarshallingTest {

    @Before
    public void prepare() throws Exception {
        MappingContextSingleton.get();
    }

    @Test
    public void testImpossibleMatchIssue() throws Exception {
        check(new ImpossibleMatchIssue(Severity.WARNING,
                                       CheckType.IMPOSSIBLE_MATCH,
                                       "1",
                                       "String",
                                       "name",
                                       "1",
                                       "2",
                                       Collections.singleton(1)),
              new ImpossibleMatchIssue(Severity.ERROR,
                                       CheckType.IMPOSSIBLE_MATCH,
                                       "1",
                                       "Person",
                                       "name",
                                       "1",
                                       "2",
                                       Collections.singleton(1)));
    }

    @Test
    public void testMultipleValuesForOneActionIssue() throws Exception {
        check(new MultipleValuesForOneActionIssue(Severity.ERROR,
                                                  CheckType.IMPOSSIBLE_MATCH,
                                                  "1",
                                                  "2",
                                                  Collections.emptySet()),
              new MultipleValuesForOneActionIssue(Severity.WARNING,
                                                  CheckType.IMPOSSIBLE_MATCH,
                                                  "1",
                                                  "2",
                                                  Collections.emptySet()));
    }

    @Test
    public void testRedundantConditionsIssue() throws Exception {
        check(new RedundantConditionsIssue(Severity.ERROR,
                                           CheckType.IMPOSSIBLE_MATCH,
                                           "Person",
                                           "name",
                                           "1",
                                           "2",
                                           Collections.emptySet()),
              new RedundantConditionsIssue(Severity.WARNING,
                                           CheckType.IMPOSSIBLE_MATCH,
                                           "Person",
                                           "name",
                                           "1",
                                           "2",
                                           Collections.emptySet()));
    }

    @Test
    public void testSingleHitLostIssue() throws Exception {
        check(new SingleHitLostIssue(Severity.ERROR,
                                     CheckType.IMPOSSIBLE_MATCH,
                                     "1",
                                     "2"),
              new SingleHitLostIssue(Severity.ERROR,
                                     CheckType.IMPOSSIBLE_MATCH,
                                     "2",
                                     "3"));
    }

    @Test
    public void testValueForActionIsSetTwiceIssue() throws Exception {
        check(new ValueForActionIsSetTwiceIssue(Severity.ERROR,
                                                CheckType.IMPOSSIBLE_MATCH,
                                                "1",
                                                "2",
                                                Collections.emptySet()),
              new ValueForActionIsSetTwiceIssue(Severity.WARNING,
                                                CheckType.IMPOSSIBLE_MATCH,
                                                "1",
                                                "2",
                                                Collections.emptySet()));
    }

    @Test
    public void testValueForFactFieldIsSetTwiceIssue() throws Exception {
        check(new ValueForFactFieldIsSetTwiceIssue(Severity.ERROR,
                                                   CheckType.IMPOSSIBLE_MATCH,
                                                   "boundName",
                                                   "name",
                                                   "1",
                                                   "2",
                                                   Collections.emptySet()),
              new ValueForFactFieldIsSetTwiceIssue(Severity.WARNING,
                                                   CheckType.IMPOSSIBLE_MATCH,
                                                   "boundName",
                                                   "name",
                                                   "1",
                                                   "2",
                                                   Collections.emptySet()));
    }

    private void check(final Issue original,
                       final Issue secondOne) {

        final String json = ServerMarshalling.toJSON(original);

        final Issue newVersion = (Issue) ServerMarshalling.fromJSON(json);

        assertEquals(original.hashCode(),
                     newVersion.hashCode());
        assertNotEquals(secondOne.hashCode(),
                        newVersion.hashCode());
    }
}