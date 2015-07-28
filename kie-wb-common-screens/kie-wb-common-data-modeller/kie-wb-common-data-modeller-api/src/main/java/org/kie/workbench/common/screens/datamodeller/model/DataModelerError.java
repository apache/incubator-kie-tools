/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.model;

import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class DataModelerError {

    private long id;

    private String message;

    private Path file;

    private int line;

    private int column;

    private Level level;

    public DataModelerError() {
        //errai marshalling
    }

    public DataModelerError( long id, String message, Level level, Path file, int line, int column ) {
        this.id = id;
        this.message = message;
        this.level = level;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public DataModelerError( String message, Level level, Path file ) {
        this.message = message;
        this.level = level;
        this.file = file;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public Path getFile() {
        return file;
    }

    public void setFile( Path file ) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine( int line ) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn( int column ) {
        this.column = column;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel( Level level ) {
        this.level = level;
    }
}
