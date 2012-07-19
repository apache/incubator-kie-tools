/*
 * Copyright 2012 JBoss Inc
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

package org.drools.repository.utils;

import org.drools.repository.AssetItem;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class AssetValidator {
    private Validator defaultValidator;

    @Inject @Any
    private Instance<Validator> validators;

    public boolean validate(AssetItem assetItem){
        if (validators != null) {
            for(Validator validator: validators){
                if (validator.getFormat().equals("default")) {
                    defaultValidator = validator;
                }
                if(assetItem.getFormat().equals(validator.getFormat())) {
                    return validator.validate(assetItem);
                }
            }
            if (defaultValidator != null) {
                return defaultValidator.validate(assetItem);
            }
        }
        return true;
    }


}

