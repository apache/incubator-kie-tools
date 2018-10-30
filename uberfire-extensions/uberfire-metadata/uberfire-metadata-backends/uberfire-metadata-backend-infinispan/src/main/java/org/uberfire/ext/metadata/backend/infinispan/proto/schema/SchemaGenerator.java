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

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class SchemaGenerator {

    private static final char NEW_LINE = '\n';

    public String generate(Schema schema) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("package ")
                .append(schema.getPkg())
                .append(";\n");

        stringBuilder.append(this.buildMessages(schema.getMessages()));

        return stringBuilder.toString();
    }

    private String buildMessages(Set<Message> messages) {
        StringBuilder stringBuilder = new StringBuilder();

        if (messages == null) {
            return "";
        }

        messages.stream()
                .map(this::buildMessage)
                .sorted()
                .forEachOrdered(message -> {
                    stringBuilder.append(message);
                    stringBuilder.append(NEW_LINE);
                });

        return stringBuilder.toString();
    }

    protected String buildMessage(Message message) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("/**\n" +
                                     "  * @Indexed\n" +
                                     "  */\n");
        stringBuilder.append("message");
        stringBuilder.append(" ");
        stringBuilder.append(message.getName());
        stringBuilder.append(" {");
        stringBuilder.append(NEW_LINE);

        message.getFields().stream()
                .sorted(Comparator.comparingInt(Field::getIndex))
                .map(this::buildField)
                .forEachOrdered(field -> {
                    stringBuilder.append(field);
                    stringBuilder.append(NEW_LINE);
                });

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    protected String buildField(Field field) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.buildIndex(field));
        stringBuilder.append(field.getScope().toString().toLowerCase());
        stringBuilder.append(" ");
        stringBuilder.append(field.getType());
        stringBuilder.append(" ");
        stringBuilder.append(field.getName());
        stringBuilder.append(" = ");
        stringBuilder.append(field.getIndex());
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    private String buildIndex(Field field) {
        return MessageFormat.format("/* @Field(index=Index.YES, analyze = Analyze.{0}, store = Store.YES) {1} {2}*/",
                                    this.getYesNo(field.isSearchable()),
                                    this.getSortable(field),
                                    this.getAnalyzer(field));
    }

    private String getSortable(Field field) {
        if (field.isSortable()) {
            return "@SortableField";
        } else {
            return "";
        }
    }

    private String getYesNo(boolean bool) {
        return bool ? "YES" : "NO";
    }

    private String getAnalyzer(Field field) {

        if (!field.isSearchable()) {
            return "";
        }

        if (StringUtils.isEmpty(field.getAnalyzer())) {
            return "@Analyzer(definition=\"standard\")";
        } else {
            return MessageFormat.format("@Analyzer(definition=\"{0}\")",
                                        field.getAnalyzer());
        }
    }
}
