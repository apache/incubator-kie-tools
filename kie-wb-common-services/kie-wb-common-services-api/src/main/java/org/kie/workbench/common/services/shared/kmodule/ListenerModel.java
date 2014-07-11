/*
 * Copyright 2012 JBoss Inc
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

import org.jboss.errai.common.client.api.annotations.Portable;

import static java.util.EnumSet.allOf;

@Portable
public class ListenerModel {

    @Portable
    public enum Kind {
        AGENDA_EVENT_LISTENER("agendaEventListener"),
        WORKING_MEMORY_EVENT_LISTENER("workingMemoryEventListener"),
        PROCESS_EVENT_LISTENER("processEventListener");

        private final String name;

        private Kind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Kind fromString(String name) {
            for (Kind kind : allOf(Kind.class)) {
                if (kind.toString().equals(name)) {
                    return kind;
                }
            }
            return null;
        }

    }

    private Kind kind = Kind.WORKING_MEMORY_EVENT_LISTENER;

    private String type = "";

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
