/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.kogito.validation;

import java.io.FileInputStream;
import java.nio.file.Path;

import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileValidationResult;

public class JsonSchemaValidation implements FileValidation {

    @Override
    public FileValidationResult validate(final Path path) {
        try {
            FileInputStream schemaStream = new FileInputStream(path.toFile());
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
            SchemaLoader.load(rawSchema);
            return FileValidationResult.createValidResult(path);
        } catch (Exception e) {
            return FileValidationResult.createInvalidResult(path, "Cannot validate JSON file as JSON Schema");
        }
    }
}
