/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.validation;

import org.uberfire.ext.security.management.api.validation.UserValidator;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementClientConstants;

public class ClientUserValidator extends UserValidator {

    @Override
    public String getMessage(String key) {
        if (KEY_NAME_NOT_EMPTY.equals(key)) {
            return UsersManagementClientConstants.INSTANCE.user_validation_nameNotEmpty();
        }
        return null;
    }
}
