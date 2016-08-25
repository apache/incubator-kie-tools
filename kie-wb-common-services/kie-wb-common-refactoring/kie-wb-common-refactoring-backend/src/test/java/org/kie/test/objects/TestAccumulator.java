/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.test.objects;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.kie.api.runtime.rule.AccumulateFunction;


/**
 * Used in PackageDescrIndexVisitorIndexingTest
 */
public class TestAccumulator implements AccumulateFunction {

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // for compilation/show, not for use
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // for compilation/show, not for use
    }

    @Override
    public Serializable createContext() {
        // for compilation/show, not for use
        return null;
    }

    @Override
    public void init(Serializable context) throws Exception {
        // for compilation/show, not for use
    }

    @Override
    public void accumulate(Serializable context, Object value) {
        // for compilation/show, not for use
    }

    @Override
    public void reverse(Serializable context, Object value) throws Exception {
        // for compilation/show, not for use
    }

    @Override
    public Object getResult(Serializable context) throws Exception {
        // for compilation/show, not for use
        return null;
    }

    @Override
    public boolean supportsReverse() {
        // for compilation/show, not for use
        return false;
    }

    @Override
    public Class<?> getResultType() {
        // for compilation/show, not for use
        return null;
    }

}
