/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.driver;


import org.kie.workbench.common.services.datamodeller.codegen.GenerationListener;
import org.uberfire.java.nio.file.OpenOption;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import java.util.List;

public interface ModelDriver {

    List<AnnotationDefinition> getConfiguredAnnotations();

    AnnotationDefinition getConfiguredAnnotation(String annotationClass);

    AnnotationDriver getAnnotationDriver(String annotationClass);

    void generateModel(DataModel dataModel, ModelDriverListener generationListener) throws Exception;

    ModelDriverResult loadModel() throws ModelDriverException;

    DataModel createModel();
}
