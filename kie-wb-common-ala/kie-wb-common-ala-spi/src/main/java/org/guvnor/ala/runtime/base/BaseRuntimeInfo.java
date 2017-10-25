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

package org.guvnor.ala.runtime.base;

import java.util.Objects;

import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.RuntimeInfo;

/**
 * BaseRuntimeInfo implementation to be extended by each Runtime Provider
 */
public class BaseRuntimeInfo
        implements RuntimeInfo {

    private RuntimeConfig config;

    /**
     * No-args constructor for enabling marshalling to work, please do not remove.
     */
    public BaseRuntimeInfo() {
    }

    public BaseRuntimeInfo(final RuntimeConfig config) {
        this.config = config;
    }

    @Override
    public RuntimeConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "BaseRuntimeInfo{" +
                "config=" + config +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.config);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseRuntimeInfo other = (BaseRuntimeInfo) obj;
        if (!Objects.equals(this.config,
                            other.config)) {
            return false;
        }
        return true;
    }
}
