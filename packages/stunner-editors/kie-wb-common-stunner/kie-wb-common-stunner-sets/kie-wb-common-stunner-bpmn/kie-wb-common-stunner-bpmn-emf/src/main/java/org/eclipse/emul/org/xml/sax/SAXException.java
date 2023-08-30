/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xml.sax;

public class SAXException extends Exception {

    static final long serialVersionUID = 583241635256073760L;

    public SAXException() {
    }

    public SAXException(String message) {
        super(message);
    }

    public SAXException(Exception e) {
        super(e);
    }

    public SAXException(String message, Exception e) {
        super(message, e);
    }

    public String getMessage() {
        String message = super.getMessage();
        Throwable cause = super.getCause();
        return message == null && cause != null ? cause.getMessage() : message;
    }

    public Exception getException() {
        return this.getExceptionInternal();
    }

    public Throwable getCause() {
        return super.getCause();
    }

    public String toString() {
        Throwable exception = super.getCause();
        if (exception != null) {
            String var10000 = super.toString();
            return var10000 + "\n" + exception.toString();
        } else {
            return super.toString();
        }
    }

    private Exception getExceptionInternal() {
        Throwable cause = super.getCause();
        return cause instanceof Exception ? (Exception) cause : null;
    }
}
