/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.authz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeMap;

/**
 * A {@link TreeMap} implementation that mimics the behaviour of the JDK's {@link Properties} class
 * preventing the '/' or ':' chars from being escaped.
 */
public class NonEscapedProperties extends TreeMap<String, String> {

    private static final long serialVersionUID = 1L;

    public NonEscapedProperties() {
    }

    public NonEscapedProperties(Comparator<? super String> comparator) {
        super(comparator);
    }

    public void load(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            putLine(line);
        }
    }

    public void load(Path file) throws IOException {
        for (String line : Files.readAllLines(file)) {
            putLine(line);
        }
    }

    public void store(Writer writer,
                      String... comments) throws IOException {
        StringBuilder out = new StringBuilder();
        if (comments != null) {
            for (String comment : comments) {
                out.append("# ").append(comment).append("\n");
            }
            out.append("\n");
        }
        this.forEach((key, value) -> {
            out.append(key).append("=").append(value).append("\n");
        });

        writer.write(out.toString());
        writer.flush();
    }

    private void putLine(String line) {
        if (line != null) {
            String _line = line.trim();
            if (!_line.isEmpty() && !_line.startsWith("#")) {
                String[] tokens = _line.split("=");
                if (tokens.length == 1) {
                    super.put(tokens[0],
                              "");
                }
                if (tokens.length == 2) {
                    super.put(tokens[0],
                              tokens[1]);
                }
            }
        }
    }
}
