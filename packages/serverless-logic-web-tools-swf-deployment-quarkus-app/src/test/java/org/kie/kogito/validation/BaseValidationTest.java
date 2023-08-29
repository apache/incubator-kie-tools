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

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.kie.kogito.TestConstants;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileValidationResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseValidationTest {

    protected final FileValidation validator;
    private final String basicValidFileName;
    private final String basicInvalidFileName;

    protected BaseValidationTest(FileValidation validator, String basicValidFileName, String basicInvalidFileName) {
        this.validator = validator;
        this.basicValidFileName = basicValidFileName;
        this.basicInvalidFileName = basicInvalidFileName;
    }

    @Test
    void testBasicValidFile() {
        final Path filePath = Path.of(TestConstants.ASSETS_FOLDER + this.basicValidFileName);
        final FileValidationResult result = validator.validate(filePath);
        assertTrue(result.isValid());
    }

    @Test
    void testBasicInvalidFile() {
        final Path filePath = Path.of(TestConstants.ASSETS_FOLDER + this.basicInvalidFileName);
        final FileValidationResult result = validator.validate(filePath);
        assertFalse(result.isValid());
    }
}
