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

package org.drools.formatter;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * An {@code extends} clause too long for the header line continues on the
 * next line, placed like the header's metadata: one level in unless the
 * metadata is flush, and with inline metadata following it on that line.
 */
class FormatterRuleHeaderTest {

    private static final String NAME =
            "\"Shipping - Oversized parcels need a manual customs declaration outside the union\"";
    private static final String PARENT = "\"Shipping - Base handling for parcels leaving the union\"";

    private static String rule(String header) {
        return "package p;\n\n" + header + "\n  when\n    Parcel( weight > 30 )\n  then\nend\n";
    }

    private static FormatterOptions options(String json) {
        return FormatterOptions.fromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void aWrappedExtendsIsIndentedLikeTheMetadata() {
        String out = DRLFormatter.format(rule("rule " + NAME + " extends " + PARENT));

        assertThat(out).contains("rule " + NAME + "\n  extends " + PARENT + "\n  when\n");
        assertThat(DRLFormatter.format(out)).isEqualTo(out);
    }

    @Test
    void aWrappedExtendsIsFlushWhenTheMetadataIs() {
        FormatterOptions flush = options("{\"headerMetadata\":\"flush\"}");

        String out = DRLFormatter.format(rule("rule " + NAME + " extends " + PARENT + "\n@Tier(1)"), flush);

        assertThat(out).contains("rule " + NAME + "\nextends " + PARENT + "\n@Tier( 1 )\n  when\n");
        assertThat(DRLFormatter.format(out, flush)).isEqualTo(out);
    }

    @Test
    void inlineMetadataFollowsTheWrappedExtends() {
        FormatterOptions inline = options("{\"headerMetadata\":\"inline\"}");

        String out = DRLFormatter.format(rule("rule " + NAME + " extends " + PARENT + "\n  @Tier(1)"), inline);

        assertThat(out).contains("rule " + NAME + "\n  extends " + PARENT + " @Tier( 1 )\n  when\n");
        assertThat(DRLFormatter.format(out, inline)).isEqualTo(out);
    }
}
