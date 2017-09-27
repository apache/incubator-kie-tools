/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.security.management.wildfly.properties;

import java.util.Properties;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLineWriterPredicateTest {

    @Mock
    private Function<String, String> keyParser;

    private final Properties properties = new Properties();
    private PropertiesLineWriterPredicate tested;

    @Before
    public void setup() throws Exception {
        properties.put("user1", "value1");
        properties.put("user2", "value2");
        properties.put("user3", "value3");
        doAnswer((Answer<String>) invocationOnMock -> (String) invocationOnMock.getArguments()[0])
                .when(keyParser).apply(anyString());
    }

    @Test
    public void testDeleteEntry() {
        PropertiesLineWriterPredicate predicate =
                build(false)
                        .begin(properties);
        assertTrue(predicate.test("user1=user1"));
        assertTrue(predicate.test("user2=user2"));
        assertTrue(predicate.test("user3=user3"));
        assertTrue(predicate.test("user4="));
        assertFalse(predicate.test("user4=value4"));
        predicate.end();
    }

    @Test
    public void testDeleteEmptyValueEntry() {
        PropertiesLineWriterPredicate predicate =
                build(true)
                        .begin(properties);
        assertTrue(predicate.test("user1=user1"));
        assertTrue(predicate.test("user2=user2"));
        assertTrue(predicate.test("user3=user3"));
        properties.put("user4", "");
        assertTrue(predicate.test("user4="));
        properties.put("user4", "value4");
        assertTrue(predicate.test("user4=value4"));
        assertFalse(predicate.test("user4="));
        predicate.end();
    }

    private PropertiesLineWriterPredicate build(final boolean allowEmptyEntryValue) {
        tested = new PropertiesLineWriterPredicate(keyParser,
                                                   allowEmptyEntryValue);
        return tested;
    }
}
