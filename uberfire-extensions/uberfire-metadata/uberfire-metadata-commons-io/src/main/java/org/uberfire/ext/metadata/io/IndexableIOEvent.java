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

package org.uberfire.ext.metadata.io;

import java.util.function.Consumer;
import java.util.function.Function;

import org.uberfire.java.nio.file.Path;

public abstract class IndexableIOEvent {

    private final Kind kind;

    private IndexableIOEvent(Kind kind) {
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    public abstract void consume(Consumer<NewFileEvent> newFileConsumer,
                                 Consumer<RenamedFileEvent> renameConsumer,
                                 Consumer<DeletedFileEvent> deleteConsumer);

    public abstract <T> T apply(Function<NewFileEvent, T> newHandler,
                                Function<RenamedFileEvent, T> renameHandler,
                                Function<DeletedFileEvent, T> deleteHandler);

    public static class NewFileEvent extends IndexableIOEvent {
        private final Path file;

        public NewFileEvent(Path file) {
            super(Kind.NewFile);
            this.file = file;
        }

        public Path getFile() {
            return file;
        }

        @Override
        public void consume(Consumer<NewFileEvent> newFileConsumer, Consumer<RenamedFileEvent> renameConsumer, Consumer<DeletedFileEvent> deleteConsumer) {
            newFileConsumer.accept(this);
        }

        @Override
        public <T> T apply(Function<NewFileEvent, T> newHandler, Function<RenamedFileEvent, T> renameHandler, Function<DeletedFileEvent, T> deleteHandler) {
            return newHandler.apply(this);
        }

        @Override
        public String toString() {
            return "NewFileEvent [file=" + file + "]";
        }
    }

    public static class RenamedFileEvent extends IndexableIOEvent {
        private final Path newPath;

        public RenamedFileEvent(Path oldPath, Path newPath) {
            super(Kind.RenamedFile);
            this.oldPath = oldPath;
            this.newPath = newPath;
        }

        private final Path oldPath;

        public Path getOldPath() {
            return oldPath;
        }

        public Path getNewPath() {
            return newPath;
        }

        @Override
        public void consume(Consumer<NewFileEvent> newFileConsumer, Consumer<RenamedFileEvent> renameConsumer, Consumer<DeletedFileEvent> deleteConsumer) {
            renameConsumer.accept(this);
        }

        @Override
        public <T> T apply(Function<NewFileEvent, T> newHandler, Function<RenamedFileEvent, T> renameHandler, Function<DeletedFileEvent, T> deleteHandler) {
            return renameHandler.apply(this);
        }

        @Override
        public String toString() {
            return "RenamedFileEvent [newPath=" + newPath + ", oldPath=" + oldPath + "]";
        }
    }

    public static class DeletedFileEvent extends IndexableIOEvent {
        private final Path file;

        public DeletedFileEvent(Path file) {
            super(Kind.DeletedFile);
            this.file = file;
        }

        public Path getFile() {
            return file;
        }

        @Override
        public void consume(Consumer<NewFileEvent> newFileConsumer, Consumer<RenamedFileEvent> renameConsumer, Consumer<DeletedFileEvent> deleteConsumer) {
            deleteConsumer.accept(this);
        }

        @Override
        public <T> T apply(Function<NewFileEvent, T> newHandler, Function<RenamedFileEvent, T> renameHandler, Function<DeletedFileEvent, T> deleteHandler) {
            return deleteHandler.apply(this);
        }

        @Override
        public String toString() {
            return "DeletedFileEvent [file=" + file + "]";
        }
    }

    public static enum Kind {
        NewFile, RenamedFile, DeletedFile
    }

}
