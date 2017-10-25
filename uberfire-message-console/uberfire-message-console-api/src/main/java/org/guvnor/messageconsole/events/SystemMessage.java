/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.events;

import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class SystemMessage {

    private String userId;
    private String messageType;
    private long id;
    private Level level;
    private Path path;
    private int line;
    private int column;
    private String text;

    public SystemMessage() {
        //needed for marshalling.
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Check whether two Messages are equivalent. Properties "id", and userId are not used in the comparison as
     * it is inconsistent for identical error messages generated in a different sequence during
     * validation by the underlying KieBuilder.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemMessage)) {
            return false;
        }

        SystemMessage that = (SystemMessage) o;

        if (messageType != null ? !messageType.equals(that.messageType) : that.messageType != null) {
            return false;
        }
        if (column != that.column) {
            return false;
        }
        if (line != that.line) {
            return false;
        }
        if (level != that.level) {
            return false;
        }
        if (path != null ? !path.equals(that.path) : that.path != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }

        return true;
    }

    /**
     * HashCode implementation fo Messages. Properties "id" and userId are not used in the generation as
     * it is inconsistent for identical error messages generated in a different sequence during
     * validation by the underlying KieBuilder.
     */
    @Override
    public int hashCode() {
        int result = level != null ? level.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (messageType != null ? messageType.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = ~~result;
        result = 31 * result + line;
        result = ~~result;
        result = 31 * result + column;
        result = ~~result;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
