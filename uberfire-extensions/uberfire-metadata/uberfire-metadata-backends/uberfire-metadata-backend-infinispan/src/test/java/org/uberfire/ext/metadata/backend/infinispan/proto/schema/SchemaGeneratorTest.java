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

package org.uberfire.ext.metadata.backend.infinispan.proto.schema;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaGeneratorTest {

    private Logger logger = LoggerFactory.getLogger(SchemaGeneratorTest.class);

    private SchemaGenerator schemaGenerator;

    @Before
    public void setUp() {
        this.schemaGenerator = new SchemaGenerator();
    }

    @Test
    public void testBuildField() {
        {
            String generatedField = this.schemaGenerator.buildField(new Field(ProtobufScope.REQUIRED,
                                                                              "int32",
                                                                              "aField",
                                                                              1));

            String expected = "/* @Field(index=Index.YES, analyze = Analyze.NO, store = Store.YES) */required int32 aField = 1;";
            assertThat(generatedField).isEqualToIgnoringWhitespace(expected);
        }

        {
            String generatedField = this.schemaGenerator.buildField(new Field(ProtobufScope.OPTIONAL,
                                                                              "int32",
                                                                              "aField",
                                                                              1));

            String expected = "/* @Field(index=Index.YES, analyze = Analyze.NO, store = Store.YES) */optional int32 aField = 1;";
            assertThat(generatedField).isEqualToIgnoringWhitespace(expected);
        }

        {
            String generatedField = this.schemaGenerator.buildField(new Field(ProtobufScope.REPEATED,
                                                                              "int32",
                                                                              "aField",
                                                                              1));
            String expected = "/* @Field(index=Index.YES, analyze = Analyze.NO, store = Store.YES) */repeated int32 aField = 1;";
            assertThat(generatedField).isEqualToIgnoringWhitespace(expected);
        }
    }

    @Test
    public void testBuildMessageWithSingleField() {
        Message message = new Message("KObject",
                                      Collections.singleton(new Field(ProtobufScope.REQUIRED,
                                                                      "int32",
                                                                      "aField",
                                                                      1)));
        String generatedMessage = this.schemaGenerator.buildMessage(message);
        logger.debug(generatedMessage);

        assertThat(this.sanitize(generatedMessage))
                .isEqualToIgnoringWhitespace(this.read("proto/single-field-message.proto"));
    }

    @Test
    public void testBuildMessageWithMultipleFields() {
        Message message = new Message("KObject",
                                      new HashSet<>(Arrays.asList(new Field(ProtobufScope.REQUIRED,
                                                                            "int32",
                                                                            "aField",
                                                                            1),
                                                                  new Field(ProtobufScope.REQUIRED,
                                                                            "string",
                                                                            "anotherField",
                                                                            2))));
        String generatedMessage = this.schemaGenerator.buildMessage(message);

        assertThat(this.sanitize(generatedMessage))
                .isEqualToIgnoringWhitespace(this.read("proto/multi-field-message.proto"));
    }

    @Test
    public void testBuildSchema() {

        Message message = new Message("KObject",
                                      new HashSet<>(Arrays.asList(new Field(ProtobufScope.REQUIRED,
                                                                            "int32",
                                                                            "embedded",
                                                                            1),
                                                                  new Field(ProtobufScope.REQUIRED,
                                                                            "string",
                                                                            "anotherEmbedded",
                                                                            2))
                                      ));

        Schema schema = new Schema("KObjectSchema",
                                   "org.appformer",
                                   Collections.singleton(message));

        String generatedMessage = this.schemaGenerator.generate(schema);
        logger.debug(generatedMessage);

        assertThat(this.sanitize(generatedMessage))
                .isEqualToIgnoringWhitespace(this.read("proto/schema.proto"));
    }

    private String read(String file) {
        URL url = Resources.getResource(file);
        try {
            return sanitize(Resources.toString(url,
                                               Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sanitize(String value) {
        return value.replaceAll("\t",
                                "").replace("\n",
                                            "");
    }
}