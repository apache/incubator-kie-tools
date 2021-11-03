/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.kafka.mbean;

public interface ObjectNamePrototype {

    public static ObjectNamePrototype withDomainAndType(String domain, String type) {
        return new ObjectNameWithDomainAndTypePrototype(domain, type);
    }

    public static ObjectNamePrototype withDomainTypeAndName(String domain, String type, String name) {
        return new ObjectNameWithDomainTypeAndNamePrototype(domain, type, name);
    }

    public static class ObjectNameWithDomainAndTypePrototype implements ObjectNamePrototype {

        private String domain;
        private String type;

        public ObjectNameWithDomainAndTypePrototype(String domain, String type) {
            this.domain = domain;
            this.type = type;
        }

        @Override
        public ObjectNameBuilder copy() {
            return ObjectNameBuilder.create(this.domain).type(this.type);
        }
    }

    public static class ObjectNameWithDomainTypeAndNamePrototype extends ObjectNameWithDomainAndTypePrototype {

        private String name;

        public ObjectNameWithDomainTypeAndNamePrototype(String domain, String type, String name) {
            super(domain, type);
            this.name = name;
        }

        @Override
        public ObjectNameBuilder copy() {
            return super.copy().name(name);
        }
    }

    public abstract ObjectNameBuilder copy();

}
