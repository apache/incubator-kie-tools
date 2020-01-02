/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Composite AssertionsError to report individual test failures.
 */
public class MultipleAssertionsError extends AssertionError {

    /**
     * Constructor
     * @param errors Map of individual AssertionError's. Key is class name of the test, value is the AssertionError.
     */
    public MultipleAssertionsError(final Map<Class<?>, ? extends AssertionError> errors) {
        super(createMessage(errors));
    }

    private static String createMessage(final Map<Class<?>, ? extends AssertionError> errors) {
        final Map<String, String> errorsMessage = errors.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue().getMessage()));
        return aggregateErrorMessages(errorsMessage);
    }

    private static String aggregateErrorMessages(final Map<String, String> errors) {
        final StringBuilder msg = new StringBuilder("\nThe following assertion(s) failed:\n");
        int i = 1;
        for (Map.Entry<String, String> e : errors.entrySet()) {
            msg.append(i++).append(") ").append(e.getKey()).append("\n").append(" - ").append(e.getValue()).append("\n");
        }

        return msg.toString();
    }
}
