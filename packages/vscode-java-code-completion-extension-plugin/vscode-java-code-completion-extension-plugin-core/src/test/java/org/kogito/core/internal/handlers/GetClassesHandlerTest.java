/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetClassesHandlerTest {

    private GetClassesHandler getClassesHandler;

    @BeforeEach
    public void setUp() {
        this.getClassesHandler = new GetClassesHandler(null, null, null);
    }

    @Test
    public void testEmptyGetFQCN() {
        String result = this.getClassesHandler.getFQCN("");
        assertThat(result).isEqualTo("");
    }

    @Test
    public void testCorrectGetFQCN() {
        String result = this.getClassesHandler.getFQCN("Class -  org.kogito");
        assertThat(result).isEqualTo("org.kogito.Class");
    }

    @Test
    public void testWrongGetFQCN() {
        String result = this.getClassesHandler.getFQCN("org.kogito");
        assertThat(result).isEqualTo("");
    }
}