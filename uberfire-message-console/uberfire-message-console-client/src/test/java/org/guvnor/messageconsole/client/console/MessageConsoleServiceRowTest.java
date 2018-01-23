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
package org.guvnor.messageconsole.client.console;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.guvnor.messageconsole.events.SystemMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MessageConsoleServiceRowTest {

    private static final String SESSION_ID = "sessionId";

    private static final String USER_ID = "userId";

    private static final int ROWS = 5;

    @Mock
    private SystemMessage message;

    protected List<MessageConsoleServiceRow> rows;

    @Before
    public void setup() {
        this.rows = new ArrayList<>();
        MessageConsoleServiceRow.resetSequence();
        IntStream.range(0, ROWS).forEach(i -> rows.add(new MessageConsoleServiceRow(SESSION_ID,
                                                                                    USER_ID,
                                                                                    message)));
    }

    @Test
    public void testSequence() {
        IntStream.range(0, ROWS).forEach(i -> assertEquals(i,
                                                           rows.get(i).getSequence()));
    }

    @Test
    public void testComparator() {
        rows.sort(MessageConsoleServiceRow.DESC_ORDER);

        IntStream.range(0, ROWS).forEach(i -> assertEquals(ROWS - i - 1,
                                                           rows.get(i).getSequence()));
    }
}
