/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.runtime.providers.base.BaseProviderType;

/**
 * OpenShift provider type implementation.
 */
public final class OpenShiftProviderType
        extends BaseProviderType {

    @JsonIgnore
    private static OpenShiftProviderType instance;

    /*
     * No-args constructor for enabling marshalling to work, please do not remove. 
    */
    public OpenShiftProviderType() {
        super("openshift", "3.6");
    }

    @JsonIgnore
    public static OpenShiftProviderType instance() {
        if (instance == null) {
            instance = new OpenShiftProviderType();
        }
        return instance;
    }
}
