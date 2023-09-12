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

package org.eclipse.emf.ecore.xmi.resource.xml;

import org.eclipse.emf.ecore.resource.Resource;

public class XMIException extends Exception implements Resource.Diagnostic {

    private static final long serialVersionUID = 1L;

    protected String location;
    protected int line;
    protected int column;

    public XMIException(String message) {
        super(message);
    }

    public XMIException(Exception exception) {
        super(exception);
    }

    public XMIException(String message, Exception exception) {
        super(message, exception);
    }

    public XMIException(String message, String location, int line, int column) {
        super(message);
        this.location = location;
        this.line = line;
        this.column = column;
    }

    public XMIException(String message, Exception exception, String location, int line, int column) {
        super(message, exception);
        this.location = location;
        this.line = line;
        this.column = column;
    }

    public XMIException(Exception exception, String location, int line, int column) {
        super(exception);
        this.location = location;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        String result = super.getMessage();
        if (line != 0) {
            result += " (" + location + ", " + line + ", " + column + ")";
        }
        return result;
    }

    public String getLocation() {
        return location;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * @deprecated in 2.2.  Use {@link #getCause()} instead.  Given that
     * the constructors of this class take {@link Exception}s as arguments, it is
     * save to do this cast <pre>(Exception)getCause()</pre>.
     */
    @Deprecated
    public Exception getWrappedException() {
        return (Exception) getCause();
    }
}
