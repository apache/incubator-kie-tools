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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TableColumnMetaTest {

    private static final String COLUMN = "column";

    @Test
    public void testValidate() {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        TableColumnMeta emptyColumnMeta = new TableColumnMeta();

        Set<ConstraintViolation<TableColumnMeta>> violations = validator.validate(emptyColumnMeta);

        Assertions.assertThat(violations)
                .isNotNull()
                .isNotEmpty();


        TableColumnMeta columnMeta = new TableColumnMeta(COLUMN, COLUMN);

        violations = validator.validate(columnMeta);

        Assertions.assertThat(violations)
                .isNotNull()
                .isEmpty();
    }
}
