/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.editor.util;

import java.util.Iterator;
import java.util.List;

import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;

public class SuperTypeMatcher {

    private AsyncPackageDataModelOracle oracle;

    public SuperTypeMatcher(AsyncPackageDataModelOracle oracle) {
        this.oracle = oracle;
    }

    public void isThereAMatchingSuperType(
            final List<String> factTypes,
            final String parameterType,
            final Callback<Boolean> callback) {
        isThereAMatchingSuperType(
                factTypes.iterator(),
                parameterType,
                callback);
    }

    private void isThereAMatchingSuperType(
            final Iterator<String> factTypes,
            final String parameterType,
            final Callback<Boolean> callback) {
        if (factTypes.hasNext()) {
            isThereAMatchingSuperType(
                    factTypes.next(),
                    parameterType,
                    new Callback<Boolean>() {
                        @Override
                        public void callback(Boolean result) {
                            if (!result) {
                                isThereAMatchingSuperType(factTypes, parameterType, callback);
                            } else {
                                callback.callback(true);
                            }
                        }
                    });
        } else {
            callback.callback(false);
        }
    }

    public void isThereAMatchingSuperType(
            final String factType,
            final String parameterType,
            final Callback<Boolean> callback) {

        oracle.getSuperTypes(
                factType,
                new Callback<List<String>>() {
                    @Override
                    public void callback(List<String> superTypes) {
                        boolean foundIt = false;

                        if (superTypes != null) {
                            for (String superType : superTypes) {
                                if (superType.equals(parameterType)) {
                                    callback.callback(true);
                                    foundIt = true;
                                    break;
                                }
                            }
                        }

                        if (!foundIt) {
                            callback.callback(false);
                        }
                    }
                });
    }
}
