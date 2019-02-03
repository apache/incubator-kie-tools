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
package org.kie.workbench.common.stunner.cm.definition;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertTrue;

public class AdHocSubProcessTest {

    private static final String NAME_VALID = "My New Process";
    private static final double WIDTH_VALID = 250d;
    private static final double HEIGHT_VALID = 50d;
    private Validator validator;
    private AdHocSubprocess tested;

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Before
    public void setup() {
        tested = new AdHocSubprocess();
        BPMNGeneralSet generalSet = tested.getGeneral();
        generalSet.setName(new Name(NAME_VALID));
        RectangleDimensionsSet rectangleDimensionsSet = tested.getDimensionsSet();
        rectangleDimensionsSet.setWidth(new Width(WIDTH_VALID));
        rectangleDimensionsSet.setHeight(new Height(HEIGHT_VALID));
    }

    @Test
    public void testAllValid() {
        Set<ConstraintViolation<AdHocSubprocess>> violations = this.validator.validate(tested);
        assertTrue(violations.isEmpty());
    }
}
