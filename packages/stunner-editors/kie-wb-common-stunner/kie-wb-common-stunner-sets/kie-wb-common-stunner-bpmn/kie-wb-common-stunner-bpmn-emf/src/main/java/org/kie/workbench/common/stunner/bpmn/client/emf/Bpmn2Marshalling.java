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


package org.kie.workbench.common.stunner.bpmn.client.emf;

import java.io.IOException;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jbpm.Bpmn2Resource;
import org.eclipse.jbpm.Bpmn2ResourceFactory;

public class Bpmn2Marshalling {

    private static final String XML_DECLARATION_START = "<?xml";
    private static final String XML_DECLARATION = XML_DECLARATION_START + " version=\"1.0\" encoding=\"UTF-8\"?>";
    private static Consumer<String> LOGGER = GWT::log;

    public static DocumentRoot unmarshall(final String raw) {
        Bpmn2Resource bpmn2Resource = Bpmn2ResourceFactory.getInstance().create();
        try {
            bpmn2Resource.load(raw);
        } catch (IOException e) {
            logError(e);
        }
        return getDocumentRoot(bpmn2Resource);
    }

    public static String marshall(final DocumentRoot document) {
        return marshall(document.getDefinitions());
    }

    public static String marshall(final Definitions definitions) {
        Bpmn2Resource bpmn2Resource = Bpmn2ResourceFactory.getInstance().create();
        bpmn2Resource.getContents().add(definitions);
        String raw = "";
        try {
            raw = bpmn2Resource.toBPMN2();
        } catch (IOException e) {
            logError(e);
        }
        return appendXmlDeclaration(raw);
    }

    public static DocumentRoot getDocumentRoot(final XMLResource resource) {
        return (DocumentRoot) resource.getContents().get(0);
    }

    private static String appendXmlDeclaration(String raw) {
        if (!raw.startsWith(XML_DECLARATION_START)) {
            return XML_DECLARATION + raw;
        }
        return raw;
    }

    public static void setLogger(final Consumer<String> LOGGER) {
        Bpmn2Marshalling.LOGGER = LOGGER;
    }

    private static void log(String message) {
        LOGGER.accept(message);
    }

    public static void logError(String message) {
        log("[ERROR] " + message);
    }

    public static void logError(String message,
                                Exception e) {
        logError("'" + message + "'. Caused by: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
    }

    public static void logError(Exception e) {
        logError("Exception thrown", e);
    }
}
