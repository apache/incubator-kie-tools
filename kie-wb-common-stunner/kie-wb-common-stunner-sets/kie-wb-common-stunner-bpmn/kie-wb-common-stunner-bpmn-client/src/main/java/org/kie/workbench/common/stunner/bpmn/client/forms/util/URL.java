/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

/**
 * This class is a proxy for a GWT static class {@link com.google.gwt.http.client.URL()} with native method to make it
 * testable without PowerMock dependency.
 */
public class URL {

    /**
     * Proxy method for {@link com.google.gwt.http.client.URL#encodeQueryString(String s)} ()}
     * @param s - string to encode
     * * @return encoded string value
     */
    public String encodeQueryString(String s) {
        return com.google.gwt.http.client.URL.encodeQueryString(s);
    }

    /**
     * Proxy method for {@link com.google.gwt.http.client.URL#decodeQueryString(String s)}
     * @param s - string to decode
     * @return decoded string value
     */
    public String decodeQueryString(String s) {
        return com.google.gwt.http.client.URL.decodeQueryString(s);
    }
}
