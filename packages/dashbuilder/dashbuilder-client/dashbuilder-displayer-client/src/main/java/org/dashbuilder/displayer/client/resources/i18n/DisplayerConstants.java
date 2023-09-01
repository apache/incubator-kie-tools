/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.resources.i18n;

public interface DisplayerConstants {

    static String initializing() {
        return "Initializing ...";
    }

    static String error() {
        return "ERROR: ";
    }

    static String error_settings_unset() {
        return "DisplayerSettings property not set";
    }

    static String error_handler_unset() {
        return "DataSetHandler property not set";
    }

    static String displayer_keyword_not_allowed(String expr) {
        return "Not allowed ( " + expr + " )";
    }

    static String displayer_expr_invalid_syntax(String expr) {
        return "Invalid syntax ( " + expr + " )";
    }
}
