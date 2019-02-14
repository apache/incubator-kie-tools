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
 */

package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class General {

    String id;
    String pkg;
    String name;
    String isExecutable;
    String isAdhoc;
    String version;
    String documentation;
    String description;

    public General() {
    }

    public static class Builder {

        private String id;
        private String pkg;
        private String name;
        private String isExecutable;
        private String isAdhoc;
        private String version;
        private String documentation;
        private String description;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder pkg(String pkg) {
            this.pkg = pkg;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder isExecutable(String isExecutable) {
            this.isExecutable = isExecutable;
            return this;
        }

        public Builder isAdhoc(String isAdhoc) {
            this.isAdhoc = isAdhoc;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder documentation(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public General build() {
            General general = new General();
            general.id = id;
            general.pkg = pkg;
            general.name = name;
            general.isExecutable = isExecutable;
            general.isAdhoc = isAdhoc;
            general.version = version;
            general.documentation = documentation;
            general.description = description;
            return general;
        }
    }
}
