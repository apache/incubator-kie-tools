/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.java.nio.file;

import org.uberfire.java.nio.IOException;

public class FileSystemException extends IOException {

    private String file;
    private String other;
    private String reason;

    public FileSystemException() {
    }

    public FileSystemException( String file ) {
        this.file = file;
    }

    public FileSystemException( String file,
                                String other,
                                String reason ) {
        this( file );
        this.other = other;
        this.reason = reason;
    }

    public String getFile() {
        return this.file;
    }

    public String getOtherFile() {
        return this.other;
    }

    public String getReason() {
        return this.reason;
    }
}
