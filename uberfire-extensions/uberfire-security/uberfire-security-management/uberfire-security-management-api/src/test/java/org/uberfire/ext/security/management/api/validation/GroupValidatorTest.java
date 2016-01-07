/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api.validation;

import org.jboss.errai.security.shared.api.Group;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupValidatorTest {

    @Test
    public void testValid() {
        final Group group = mock(Group.class);
        when(group.getName()).thenReturn("group1");
        Set<ConstraintViolation<Group>> violations = new GroupValidatorTestImpl().validate(group);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testBlankGroupName() {
        final Group blankGroup = mock(Group.class);
        when(blankGroup.getName()).thenReturn("");
        final Set<ConstraintViolation<Group>> violations1 = new GroupValidatorTestImpl().validate(blankGroup);
        assertTrue(violations1.size() == 1);
        final ConstraintViolation<Group> violation = violations1.iterator().next();
        assertEquals(violation.getPropertyPath().iterator().next().getName(), "name");
        assertEquals(violation.getMessage(), GroupValidator.KEY_NAME_NOT_EMPTY);
    }
    
    public static class GroupValidatorTestImpl extends GroupValidator {
        @Override
        public String getMessage(String key) {
            return key;
        }
    }
}
