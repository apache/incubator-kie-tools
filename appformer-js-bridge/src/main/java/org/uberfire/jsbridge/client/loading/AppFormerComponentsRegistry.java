/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.loading;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class AppFormerComponentsRegistry {

    public native String[] keys() /*-{
        if (typeof $wnd.AppFormerComponentsRegistry === "undefined") {
            return [];
        }
        return Object.keys($wnd.AppFormerComponentsRegistry);
    }-*/;

    public native JavaScriptObject get(final String key) /*-{
        if (typeof $wnd.AppFormerComponentsRegistry[key] === "undefined") {
            return null;
        }
        return $wnd.AppFormerComponentsRegistry[key];
    }-*/;

    public static class Entry {

        private final String componentId;

        private final JavaScriptObject self;

        public Entry(final String componentId,
                     final JavaScriptObject self) {

            checkNotNull(componentId);
            checkNotNull(self);

            this.componentId = componentId;
            this.self = self;
        }

        JavaScriptObject getSelf() {
            return self;
        }

        public String getComponentId() {
            return this.componentId;
        }

        public Entry.Type getType() {
            return Entry.Type.valueOf(((String) get("type")).toUpperCase());
        }

        public String getSource() {
            return (String) get("source");
        }

        public Map<String, String> getParams() {

            final JavaScriptObject jsParams = (JavaScriptObject) get("params");
            if (jsParams == null) {
                return new HashMap<>();
            }

            final JSONObject json = new JSONObject(jsParams);

            return json.keySet().stream()
                    .filter(k -> json.get(k) != null)
                    .collect(toMap(identity(), k -> json.get(k).toString()));
        }

        private native Object get(final String key) /*-{
            return this.@org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry.Entry::self[key];
        }-*/;

        public boolean matches(final String uri) {
            final String matches = getParams().get("matches");
            final String regex = matches.substring(1, matches.length() - 1); //FIXME: Temporary workaround to remove extra quotes
            return uri.matches(regex);
        }

        public enum Type {
            PERSPECTIVE,
            SCREEN,
            EDITOR,
        }

        public static class PerspectiveParams {

            private final Map<String, String> params;

            public PerspectiveParams(final Map<String, String> params) {
                this.params = params;
            }

            public Optional<Boolean> isDefault() {
                return ofNullable(this.params.get("is_default")).map(Boolean::valueOf);
            }
        }

        public static class EditorParams {

            private final Map<String, String> params;

            public EditorParams(final Map<String, String> params) {
                this.params = params;
            }

            public Optional<String> matches() {
                return ofNullable(this.params.get("matches"));
            }
        }
    }
}
