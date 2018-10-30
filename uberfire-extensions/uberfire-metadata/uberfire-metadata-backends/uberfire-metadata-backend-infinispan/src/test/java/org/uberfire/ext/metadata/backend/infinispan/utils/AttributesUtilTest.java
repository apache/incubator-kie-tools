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
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class AttributesUtilTest {

    @Test
    public void toProtobufFormat_replaceInvalidCharactersWithTwoUnderscores() {
        assertThat(AttributesUtil.toProtobufFormat("abcdEFGH")).isEqualTo("abcdEFGH");
        assertThat(AttributesUtil.toProtobufFormat("a.b")).isEqualTo("a__b");
        assertThat(AttributesUtil.toProtobufFormat("c/d")).isEqualTo("c__d");
        assertThat(AttributesUtil.toProtobufFormat("e:f")).isEqualTo("e__f");
        assertThat(AttributesUtil.toProtobufFormat("g[h]")).isEqualTo("g__h__");
        assertThat(AttributesUtil.toProtobufFormat("i-j")).isEqualTo("i_j");
        assertThat(AttributesUtil.toProtobufFormat("./:[]-")).isEqualTo("___________");
    }

    @Test
    public void toKPropertyFormat_replacesDoubleUnderscoreWithSlashDot() {
        assertThat(AttributesUtil.toKPropertyFormat("a__b_c__d")).isEqualTo("a.b_c.d");
    }
}