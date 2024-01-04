/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.yard.validator;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import org.yard.validator.checks.Check;
import org.yard.validator.checks.CheckProducer;
import org.yard.validator.key.Location;
import org.yard.validator.key.RowLocation;
import org.yard.validator.runner.Runner;
import org.yard.validator.util.Callback;
import org.yard.validator.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class YardValidator {

    private final ArrayList<String> log = new ArrayList<>();
    private ParserResult parse;

    @JsConstructor
    public YardValidator() {
    }

    @JsMethod
    public void setLogger(final Callback callback) {
        Logger.startLogging(callback);
    }

    private void init(final String yaml) {

        Logger.startLogging(s -> log.add("\"" + s + "\""));
        Logger.log("Starting validator");

        parse = new Parser().parse(yaml);
    }

    @JsMethod
    public String validate(final String yaml) {
        final ArrayList<String> issues = new ArrayList<>();
        validate(yaml, issue -> {
            final StringBuilder builder = new StringBuilder();
            builder.append("{\"issue\":\"" + issue.getMessage() + "\"");
            builder.append(",");
            builder.append("\"locations\":[");

            builder.append(Arrays.stream(issue.getLocations()).map(i -> {
                final StringBuilder innerBuilder = new StringBuilder();
                innerBuilder.append("{");
                innerBuilder.append("\"rowInFile\":" + ((RowLocation) i).getActualRowNumberInFile() + ",");
                innerBuilder.append("\"rowInTable\":" + ((RowLocation) i).getTableRowNumber());
                innerBuilder.append("}");
                return innerBuilder.toString();
            }).collect(Collectors.joining(", ")));

            builder.append("]}");
            issues.add(builder.toString());
        });
        return "{\"log\":[" + log.toArray().toString() + "],\"result\":[" + issues.stream().collect(Collectors.joining(",")) + "]}";
    }

    public void validate(final String yaml, final ReportBus bus) {
        try {
            init(yaml);
        } catch (final Exception e) {
            Logger.log("Failed to initialize: " + e.getMessage());
        }

        new Runner(bus).run(getChecks());
    }

    private List<Check> getChecks() {
        if (Objects.equals("COLLECT", parse.getHitPolicy())) {
            return Collections.emptyList();
        } else {
            return CheckProducer.getChecks(parse);
        }
    }
}
