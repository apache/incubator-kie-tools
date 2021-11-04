/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.engine;

public class BuildInformation {

    private final String uri;
    private final String originalText;
    private final String text;
    private final int line;
    private final int position;

    public BuildInformation(String uri, String originalText, String text, int line, int position) {
        this.uri = uri;
        this.originalText = originalText;
        this.text = text;
        this.line = line;
        this.position = position;
    }

    public String getText() {
        return text;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }

    public String getUri() {
        return uri;
    }

    public String getOriginalText() {
        return originalText;
    }
}
