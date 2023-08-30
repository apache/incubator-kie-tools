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

package org.eclipse.emf.ecore.xmi.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.util.StreamHelper;

import static org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling.logError;

public abstract class AbstractMapper implements Mapper {

    public abstract void parse(Resource resource, String content, Map<?, ?> options, Callback<Resource> callback);

    public void parse(Resource resource, InputStream inputStream, Map<?, ?> options, Callback<Resource> callback) {
        parse(resource, StreamHelper.toString(inputStream), options, callback);
    }

    public abstract String write(Resource resource, Map<?, ?> options);

    public void write(Resource resource, OutputStream stream, Map<?, ?> options) {
        try {
            final String value = write(resource, options);
            final byte[] bytes = value.getBytes();
            stream.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            logError("Error while writing the Bpmn2Resource to the output stream.", e);
        }
    }
}
