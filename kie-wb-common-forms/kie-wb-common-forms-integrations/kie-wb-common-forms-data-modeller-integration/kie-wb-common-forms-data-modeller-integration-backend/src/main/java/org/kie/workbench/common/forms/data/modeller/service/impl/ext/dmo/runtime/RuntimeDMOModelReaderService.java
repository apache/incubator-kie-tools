/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.runtime;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReader;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;

@Dependent
public class RuntimeDMOModelReaderService implements ModelReaderService<ClassLoader> {

    private MVELEvaluator evaluator;

    @Inject
    public RuntimeDMOModelReaderService(MVELEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public ModelReader getModelReader(ClassLoader classloader) {
        return new RuntimeDMOModelReader(classloader, evaluator);
    }
}
