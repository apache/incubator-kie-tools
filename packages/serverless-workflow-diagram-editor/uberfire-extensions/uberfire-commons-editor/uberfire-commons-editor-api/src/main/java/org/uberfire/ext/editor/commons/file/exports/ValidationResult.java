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


package org.uberfire.ext.editor.commons.file.exports;

import java.util.List;

public class ValidationResult {

    private boolean valid;

    private List<String> messagesBundleKeys;

    public ValidationResult() {
    }

    public ValidationResult(final boolean valid,
                            final List<String> messagesBundleKeys) {
        this.valid = valid;
        this.messagesBundleKeys = messagesBundleKeys;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getMessagesBundleKeys() {
        return messagesBundleKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationResult)) {
            return false;
        }

        ValidationResult that = (ValidationResult) o;

        if (isValid() != that.isValid()) {
            return false;
        }
        return !(getMessagesBundleKeys() != null ? !getMessagesBundleKeys().equals(that.getMessagesBundleKeys()) : that.getMessagesBundleKeys() != null);
    }

    @Override
    public int hashCode() {
        int result = (isValid() ? 1 : 0);
        result = ~~result;
        result = 31 * result + (getMessagesBundleKeys() != null ? getMessagesBundleKeys().hashCode() : 0);
        result = ~~result;
        return result;
    }
}
