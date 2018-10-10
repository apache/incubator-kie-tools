/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.BaseExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.junit.runner.Runner;

public abstract class AbstractScenarioRunner extends Runner {

    private ClassLoader classLoader = AbstractScenarioRunner.class.getClassLoader();
    private Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory = BaseExpressionEvaluator::new;

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ExpressionEvaluator createExpressionEvaluator() {
        return expressionEvaluatorFactory.apply(getClassLoader());
    }

    public void setExpressionEvaluatorFactory(Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory) {
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }
}
