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
package org.dashbuilder.displayer.client.widgets.sourcecode;

import java.util.Collection;

public interface JsValidator {

    /**
     * Check if the the given javascript fragment has no syntax errors.
     *
     * @param jsTemplate The javascript template to validate.
     * @param allowedVariables The only set of variables that can be referenced from the JS body
     * @return An error message in case an error exists, or null if everything is ok.
     */
    String validate(String jsTemplate, Collection<String> allowedVariables);
}
