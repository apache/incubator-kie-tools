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

package org.kie.kogito.dmn.form;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.logging.Logger;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.openapi.DMNOASGeneratorFactory;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.internal.io.ResourceFactory;

public class FormSchemaGenerator {

    private static final Logger LOGGER = Logger.getLogger(FormSchemaGenerator.class);

    public Form execute(final String dmnPath,
                        final String formUrl,
                        final String modelUrl,
                        final String swaggerUIUrl) throws FileNotFoundException {
        var dmnFile = new File(dmnPath);
        var modelResource = ResourceFactory.newReaderResource(new FileReader(dmnFile), "UTF-8");
        var dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(modelResource))
                .getOrElseThrow(RuntimeException::new);
        var dmnModel = dmnRuntime.getModels().get(0);

        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singletonList(dmnModel)).build();
        ObjectNode jsNode = oasResult.getJsonSchemaNode();

        DMNType is = oasResult.lookupIOSetsByModel(dmnModel).getInputSet();
        String isRef = oasResult.getNamingPolicy().getRef(is);
        jsNode.put("$ref", isRef);

        return new Form(dmnFile.getName(),
                        dmnModel.getName(),
                        jsNode,
                        formUrl,
                        modelUrl,
                        swaggerUIUrl);
    }

    public static void main(final String[] args) {
        if (args.length != 5) {
            LOGGER.error("5 mandatory args must be specified: dmnPath, outputPath, formUrl, modelUrl and swaggerUIUrl");
            return;
        }

        var mapper = new ObjectMapper();
        try (var writer = new BufferedWriter(new FileWriter(args[1]))) {
            var form = new FormSchemaGenerator().execute(args[0], args[2], args[3], args[4]);
            writer.write(mapper.writeValueAsString(form));
        } catch (Exception e) {
            LOGGER.error("Error when generating the form schema.", e);
        }
    }
}
