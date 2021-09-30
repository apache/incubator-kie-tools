/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.uuid;

import org.dashbuilder.DataSetCore;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UUIDs generator tool test
 */
public class UUIDGeneratorTest {

    private UUIDGenerator uuidGenerator;

    @Before
    public void setUp() throws Exception {
        uuidGenerator = DataSetCore.get().getUuidGenerator();
    }

    @Test
    public void testUUIDLength() {
        String uuid = uuidGenerator.newUuidBase64();
        assertThat(uuid.length()).isEqualTo(22);
    }

    @Test
    public void testURLSafe() {
        String uuid = uuidGenerator.newUuidBase64();
        assertThat(uuid.contains("\u003d")).isFalse();
        assertThat(uuid.contains("\u002f")).isFalse();
        assertThat(uuid.contains("\u002b")).isFalse();
        assertThat(uuid.contains("\u0026")).isFalse();
    }

    @Test
    public void testDecoding() {
        String uuid = uuidGenerator.newUuid();
        String base64 = uuidGenerator.uuidToBase64(uuid);
        String back = uuidGenerator.uuidFromBase64(base64);
        assertThat(back).isEqualTo(uuid);
    }
}

