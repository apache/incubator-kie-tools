/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.processors.shape;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;

import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactoryWrapper;
import org.kie.workbench.common.stunner.core.processors.AbstractBindableAdapterGenerator;
import org.kie.workbench.common.stunner.core.processors.MainProcessor;
import org.kie.workbench.common.stunner.core.processors.ProcessingDefinitionAnnotations;
import org.kie.workbench.common.stunner.core.processors.ProcessingEntity;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class BindableShapeFactoryGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableShapeFactory.ftl";
    }

    public StringBuffer generate(final String packageName,
                                 final String className,
                                 final ProcessingDefinitionAnnotations processingDefinitionAnnotations,
                                 final Messager messager) throws GenerationException {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("generatedByClassName",
                 BindableShapeFactoryGenerator.class.getName());
        root.put("parentClassName",
                 ShapeFactoryWrapper.class.getName());
        Map<String, String[]> shapeDefs = processingDefinitionAnnotations.getShapeDefinitions();
        Set<String> definitionClasses = shapeDefs.keySet();
        Collection<String[]> values = shapeDefs.values();
        Collection<String> factoryClasses = getCollection(values,
                                                          0);
        Collection<String> shapeDefClasses = getCollection(values,
                                                           1);
        Collection<ProcessingEntity> shapeDefFactoryEntities = new LinkedList<>();
        for (String s : factoryClasses) {
            shapeDefFactoryEntities.add(new ProcessingEntity(s,
                                                             MainProcessor.toClassMemberId(s)));
        }
        root.put("shapeDefFactoryEntities",
                 shapeDefFactoryEntities);
        root.put("definitionClasses",
                 definitionClasses);
        root.put("shapeDefClasses",
                 shapeDefClasses);
        Collection<String> addProxySentences = new LinkedList<>();
        for (Map.Entry<String, String[]> entry : shapeDefs.entrySet()) {
            String classname = entry.getKey();
            String factoryClass = entry.getValue()[0];
            String factoryId = MainProcessor.toClassMemberId(factoryClass);
            String shapeDefClass = entry.getValue()[1];
            addProxySentences.add(factoryId + ".addShapeDef( " + classname + ".class, new " + shapeDefClass + "() );");
        }
        root.put("addProxySentences",
                 addProxySentences);

        //Generate code
        return writeTemplate(packageName,
                             className,
                             root,
                             messager);
    }

    private Collection<String> getCollection(final Collection<String[]> c,
                                             final int index) {
        Collection<String> result = new LinkedHashSet<>();
        for (String[] s : c) {
            result.add(s[index]);
        }
        return result;
    }
}
