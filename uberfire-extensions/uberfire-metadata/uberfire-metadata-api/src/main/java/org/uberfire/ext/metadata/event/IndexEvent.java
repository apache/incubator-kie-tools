/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.ext.metadata.event;

import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;

public abstract class IndexEvent {

    private final Kind kind;

    private IndexEvent(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public static enum Kind {
        NewlyIndexed, Renamed, Deleted
    }

    public static class NewlyIndexedEvent extends IndexEvent {
        private final KObject kObject;

        public NewlyIndexedEvent(KObject kObject) {
            super(Kind.NewlyIndexed);
            this.kObject = kObject;
        }

        public KObject getKObject() {
            return kObject;
        }
    }

    public static class RenamedEvent extends IndexEvent {
        private final KObjectKey source;
        private final KObject target;

        public RenamedEvent(KObjectKey source, KObject target) {
            super(Kind.Renamed);
            this.source = source;
            this.target = target;
        }

        public KObjectKey getSource() {
            return source;
        }

        public KObject getTarget() {
            return target;
        }
    }

    public static class DeletedEvent extends IndexEvent {
        private final KObjectKey deleted;

        public DeletedEvent(KObjectKey deleted) {
            super(Kind.Deleted);
            this.deleted = deleted;
        }

        public KObjectKey getDeleted() {
            return deleted;
        }
    }

}
