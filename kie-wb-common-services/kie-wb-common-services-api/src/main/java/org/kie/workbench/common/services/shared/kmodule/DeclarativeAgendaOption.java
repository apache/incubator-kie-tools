/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.kmodule;

public enum DeclarativeAgendaOption {

    ENABLED("enabled"), DISABLED("disabled");

    private final String string;

    DeclarativeAgendaOption(String mode) {
        this.string = mode;
    }

    @Override
    public String toString() {
        return string;
    }

    public static DeclarativeAgendaOption determineDeclarativeAgendaMode(String declarativeAgenda) {
        if (DISABLED.getMode().equalsIgnoreCase(declarativeAgenda)
                || "false".equalsIgnoreCase(declarativeAgenda)) {
            return DISABLED;
        } else if (ENABLED.getMode().equalsIgnoreCase(declarativeAgenda)
                || "true".equalsIgnoreCase(declarativeAgenda)) {
            return ENABLED;
        }
        throw new IllegalArgumentException("Illegal enum value '" + declarativeAgenda + "' for DeclarativeAgendaOption");
    }

    public String getMode() {
        return string;
    }
}
