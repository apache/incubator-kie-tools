/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.core.util.EqualsAndHashCodeTestUtils;

import static org.junit.Assert.assertEquals;

public class NotificationValueTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new NotificationValue(),
                             new NotificationValue())
                .addTrueCase(new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Collections.EMPTY_LIST,
                                                   Collections.EMPTY_LIST),
                             new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Collections.EMPTY_LIST,
                                                   Collections.EMPTY_LIST))

                .addTrueCase(new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Collections.EMPTY_LIST),
                             new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Collections.EMPTY_LIST))
                .addTrueCase(new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})),
                             new NotificationValue("AAA",
                                                   "1h",
                                                   "Subj",
                                                   "me",
                                                   "NotStartedNotify",
                                                   "me",
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                   Arrays.asList(new String[]{"foo", "bar", "baz"})))

                .addFalseCase(new NotificationValue("AAA1",
                                                    "1h1",
                                                    "Subj1",
                                                    "me1",
                                                    "NotStartedNotify1",
                                                    "me1",
                                                    Collections.EMPTY_LIST,
                                                    Collections.EMPTY_LIST),
                              new NotificationValue("AAA",
                                                    "1h",
                                                    "Subj",
                                                    "me",
                                                    "NotStartedNotify",
                                                    "me",
                                                    Collections.EMPTY_LIST,
                                                    Collections.EMPTY_LIST))

                .addFalseCase(new NotificationValue("AAA",
                                                    "1h",
                                                    "Subj",
                                                    "me",
                                                    "NotStartedNotify",
                                                    "me",
                                                    Arrays.asList(new String[]{"foo1", "bar", "baz"}),
                                                    Collections.EMPTY_LIST),
                              new NotificationValue("AAA",
                                                    "1h",
                                                    "Subj",
                                                    "me",
                                                    "NotStartedNotify",
                                                    "me",
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                    Collections.EMPTY_LIST))
                .addFalseCase(new NotificationValue("AAA",
                                                    "1h",
                                                    "Subj",
                                                    "me",
                                                    "NotStartedNotify",
                                                    "me",
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                    Arrays.asList(new String[]{"foo1", "bar", "baz"})),
                              new NotificationValue("AAA",
                                                    "1h",
                                                    "Subj",
                                                    "me",
                                                    "NotStartedNotify",
                                                    "me",
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"}),
                                                    Arrays.asList(new String[]{"foo", "bar", "baz"})))
                .test();
    }

    @Test
    public void testNotificationPartialVerticalBar() {
        NotificationValue test = new NotificationValue("a||||||dssf||sdf|Sdf|sdf|Sdf|SDf",
                              "1h",
                              "z|asd|ASd||asd|Asd|asd|",
                              "me",
                              "NotStartedNotify",
                              "me",
                              Collections.EMPTY_LIST,
                              Collections.EMPTY_LIST);
        assertEquals("[from:me|tousers:|togroups:|replyTo:me|subject:z&#124;asd&#124;ASd&#124;&#124;asd&#124;Asd&#124;asd&#124;|body:a&#124;&#124;&#124;&#124;&#124;&#124;dssf&#124;&#124;sdf&#124;Sdf&#124;sdf&#124;Sdf&#124;SDf]@[1h]", test.toCDATAFormat());
    }
}
