/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads the {@code <kbase>} declarations from a {@code META-INF/kmodule.xml}.
 *
 * <p>Only the three attributes that determine which resources compile together
 * are read — {@code name}, {@code packages} and {@code includes}. Everything
 * else in the schema configures the runtime (session pools, event modes, belief
 * systems) and has no bearing on what an editor should treat as one scope.
 *
 * <p>Parsed with the JDK's own XML support, with external entity resolution
 * disabled: a language server reads whatever files a workspace happens to
 * contain, so an untrusted {@code kmodule.xml} must not be able to reach the
 * network or the wider filesystem.
 */
final class KModuleParser {

    private static final Logger logger = Logger.getLogger(KModuleParser.class.getName());

    private KModuleParser() {
    }

    /**
     * Returns the groups declared by {@code kmoduleFile}, or an empty list if it
     * cannot be read or contains no {@code <kbase>}. A malformed descriptor is
     * logged and skipped rather than failing the whole workspace.
     */
    static List<KieBaseDecl> parse(Path kmoduleFile) {
        List<KieBaseDecl> declarations = new ArrayList<>();
        try (InputStream in = Files.newInputStream(kmoduleFile)) {
            Document document = newSecureBuilder().parse(in);
            NodeList kbases = document.getElementsByTagNameNS("*", "kbase");
            for (int i = 0; i < kbases.getLength(); i++) {
                Node node = kbases.item(i);
                if (node instanceof Element element) {
                    toDeclaration(element, kmoduleFile).ifPresent(declarations::add);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not read kmodule descriptor, skipping: " + kmoduleFile, e);
            return List.of();
        }
        return declarations;
    }

    private static java.util.Optional<KieBaseDecl> toDeclaration(Element kbase, Path kmoduleFile) {
        String name = kbase.getAttribute("name").trim();
        if (name.isEmpty()) {
            // The schema allows an unnamed kbase, but a group the user cannot
            // name is a group they cannot select in the editor.
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new KieBaseDecl(
                name,
                splitAttribute(kbase.getAttribute("packages")),
                splitAttribute(kbase.getAttribute("includes")),
                List.of(),
                true,
                KieBaseDecl.Origin.kieBase(kmoduleFile)));
    }

    /** Splits a comma-separated kmodule attribute, dropping blank entries. */
    private static List<String> splitAttribute(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        List<String> parts = new ArrayList<>();
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                parts.add(trimmed);
            }
        }
        return parts;
    }

    private static DocumentBuilder newSecureBuilder() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }
}
