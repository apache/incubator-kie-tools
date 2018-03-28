/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.processing.engine.handling;

public class ValidationResult {

    public enum State {
        VALID(true),
        ERROR(false);

        private boolean valid;

        State(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }
    }

    private State status;
    private String message;

    private ValidationResult(State status, String message) {
        this.status = status;
        this.message = message;
    }

    public State getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(State.ERROR, message);
    }

    public static ValidationResult valid() {
        return new ValidationResult(State.VALID, "");
    }
}
