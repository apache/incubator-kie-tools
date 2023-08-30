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


package org.kie.workbench.common.forms.processing.engine.handling;

public class ValidationResult {

    public enum State {
        VALID(true, false),
        WARNING(true, true),
        ERROR(false, true);

        private boolean valid;
        private boolean hasMessage;

        State(boolean valid, boolean hasMessage) {
            this.valid = valid;
            this.hasMessage = hasMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public boolean hasMessage() {
            return hasMessage;
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

    public static ValidationResult warning(String message) {
        return new ValidationResult(State.WARNING, message);
    }

    public static ValidationResult valid() {
        return new ValidationResult(State.VALID, "");
    }
}
