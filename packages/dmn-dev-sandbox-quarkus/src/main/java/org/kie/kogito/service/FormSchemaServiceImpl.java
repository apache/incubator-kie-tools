/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.openapi.DMNOASGeneratorFactory;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.internal.io.ResourceFactory;
import org.kie.kogito.model.Form;

@ApplicationScoped
public class FormSchemaServiceImpl implements FormSchemaService {

    @Override
    public List<Form> generate(final String resourcesFolderPath, final List<String> filePaths) throws FileNotFoundException {
        var dmnRuntime = buildRuntime(filePaths);
        var dmnModels = dmnRuntime.getModels().stream()
                .sorted(Comparator.comparing(DMNModel::getName))
                .collect(Collectors.toList());
        var oasResult = DMNOASGeneratorFactory.generator(dmnModels).build();
        return dmnModels
                .stream()
                .map(dmnModel -> {
                    var resourcesFolder = Paths.get(resourcesFolderPath).toAbsolutePath();
                    var modelFullPath = Paths.get(dmnModel.getResource().getSourcePath());
                    return new Form(modelFullPath.toString()
                                            .replace(resourcesFolder.toString(), "")
                                            .replace("\\", "/"),
                                    dmnModel.getName(),
                                    formSchema(dmnModel, oasResult));
                })
                .collect(Collectors.toList());
    }

    private ObjectNode formSchema(final DMNModel dmnModel, final DMNOASResult oasResult) {
        ObjectNode jsNode = oasResult.getJsonSchemaNode().deepCopy();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        jsNode.put("$ref", isRef);

        return jsNode;
    }

    private DMNRuntime buildRuntime(final List<String> filePaths) throws FileNotFoundException {
        Map<String, Resource> resources = new HashMap<>();
        for (String filePath : filePaths) {
            var dmnFile = new File(filePath);
            var readerResource = ResourceFactory.newReaderResource(new FileReader(dmnFile), "UTF-8");
            readerResource.setSourcePath(filePath);
            resources.put(filePath, readerResource);
        }

        var rbk = new ResolveByKey(resources);
        return DMNRuntimeBuilder.fromDefaults()
                .setRelativeImportResolver((namespace, modelName, uri) -> rbk.readerByKey(uri))
                .buildConfiguration()
                .fromResources(resources.values())
                .getOrElseThrow(RuntimeException::new);
    }
}
