/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.bridge;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EngineBridgeTest {

    private static final String RULES = "src/main/resources/rules/A.drl";
    private static final String TYPES = "src/main/resources/rules/Types.drl";

    private static final String VALID = """
            package com.example;
            declare Order
                total : int
            end
            rule "Large order"
                when
                    Order( total > 100 )
                then
                end
            """;

    private static final String UNRESOLVABLE_TYPE = """
            package com.example;
            rule "R"
                when
                    NoSuchType( field == 1 )
                then
                end
            """;

    private static final String MISSPELLED_FIELD = """
            package com.example;
            declare Order
                total : int
            end
            rule "R"
                when
                    Order( totl > 100 )
                then
                end
            """;

    private static final String BROKEN_CONSEQUENCE = """
            package com.example;
            declare Order
                total : int
            end
            rule "R"
                when
                    Order( total > 100 )
                then
                    int count = "many";
                end
            """;

    private static final String UNDECLARED_GLOBAL = """
            package com.example;
            declare Order
                total : int
            end
            rule "R"
                when
                    Order( total > 100 )
                then
                    log.add("large");
                end
            """;

    private static final String DUPLICATE_RULES = """
            package com.example;
            rule "R"
                when
                then
                end
            rule "R"
                when
                then
                end
            """;

    private static final String USES_ORDER = """
            package com.example;
            rule "R"
                when
                    Order( total > 1 )
                then
                end
            """;

    private static final String DECLARES_ORDER = """
            package com.example;
            declare Order
                total : int
            end
            """;

    @Test
    void validDrlYieldsNoMessages() {
        assertThat(EngineBridge.build(Map.of(RULES, VALID))).isEmpty();
    }

    @Test
    void unresolvableTypeYieldsAnErrorOnTheFile() {
        List<Map<String, Object>> messages = EngineBridge.build(Map.of(RULES, UNRESOLVABLE_TYPE));

        assertThat(messages).anySatisfy(m -> {
            assertThat(m.get(EngineBridge.LEVEL)).isEqualTo("ERROR");
            assertThat((String) m.get(EngineBridge.PATH)).endsWith("A.drl");
            assertThat((String) m.get(EngineBridge.TEXT)).contains("NoSuchType");
        });
    }

    @Test
    void misspelledFieldOnADeclaredTypeYieldsAnError() {
        assertThat(EngineBridge.build(Map.of(RULES, MISSPELLED_FIELD))).anySatisfy(m -> {
            assertThat(m.get(EngineBridge.LEVEL)).isEqualTo("ERROR");
            assertThat((String) m.get(EngineBridge.TEXT)).contains("totl");
        });
    }

    @Test
    void consequenceThatDoesNotCompileYieldsAnError() {
        assertThat(EngineBridge.build(Map.of(RULES, BROKEN_CONSEQUENCE)))
                .anySatisfy(m -> assertThat(m.get(EngineBridge.LEVEL)).isEqualTo("ERROR"));
    }

    @Test
    void consequenceUsingAnUndeclaredGlobalYieldsAnError() {
        assertThat(EngineBridge.build(Map.of(RULES, UNDECLARED_GLOBAL)))
                .anySatisfy(m -> assertThat(m.get(EngineBridge.LEVEL)).isEqualTo("ERROR"));
    }

    @Test
    void duplicateRuleNamesYieldAnError() {
        assertThat(EngineBridge.build(Map.of(RULES, DUPLICATE_RULES))).anySatisfy(m -> {
            assertThat(m.get(EngineBridge.LEVEL)).isEqualTo("ERROR");
            assertThat((String) m.get(EngineBridge.TEXT)).containsIgnoringCase("duplicate");
        });
    }

    @Test
    void aTypeDeclaredInAnotherFileOfTheSetResolves() {
        assertThat(EngineBridge.build(Map.of(TYPES, DECLARES_ORDER, RULES, USES_ORDER))).isEmpty();
    }

    @Test
    void messagesNameTheFileThatProducedThem() {
        List<Map<String, Object>> messages =
                EngineBridge.build(Map.of(TYPES, DECLARES_ORDER, RULES, UNRESOLVABLE_TYPE));

        assertThat(messages).anySatisfy(m -> assertThat((String) m.get(EngineBridge.PATH)).endsWith("A.drl"));
        assertThat(messages).noneSatisfy(m -> assertThat((String) m.get(EngineBridge.PATH)).endsWith("Types.drl"));
    }
}
