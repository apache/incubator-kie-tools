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

package org.uberfire.java.nio.file;

public class InvalidPathException extends IllegalArgumentException {

    private String input;
    private String reason;
    private int index;

    public InvalidPathException() {
    }

    public InvalidPathException( String input,
                                 String reason,
                                 int index ) {
        this( input,
              reason );
        this.index = index;
    }

    public InvalidPathException( String input,
                                 String reason ) {
        this.input = input;
        this.reason = reason;
    }

    public String getInput() {
        return this.input;
    }

    public String getReason() {
        return this.reason;
    }

    public int getIndex() {
        return this.index;
    }
}