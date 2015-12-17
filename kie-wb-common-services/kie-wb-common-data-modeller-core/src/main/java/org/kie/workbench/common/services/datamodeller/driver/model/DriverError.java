/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver.model;

import org.uberfire.backend.vfs.Path;

public class DriverError {

    private long id;

    private String message;

    private Path file;

    private int line;

    private int column;

    public DriverError() {

    }

    public DriverError( long id, String message, Path file, int line, int column ) {
        this.id = id;
        this.message = message;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public DriverError( String message, Path file ) {
        this.message = message;
        this.file = file;
    }

    public DriverError( String message, int line, int column ) {
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public DriverError( String message ) {
        this.message = message;
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

}
