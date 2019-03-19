/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gwt.validation.client.impl.ConstraintViolationImpl;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfValidation1;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations.SmurfValidation2;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Fact's field validation
 */
public class PackageDataModelFactFieldsValidationsTest {

    private static String NULL_CONSTRAINT_ERROR = "Value cannot be null.";

    private static String RANGE_CONSTRAINT_ERROR = "Value outside permitted range.";

    @SuppressWarnings("unchecked")
    private AsyncPackageDataModelOracle getOracle(final Class clazz,
                                                  final Instance<DynamicValidator> validatorInstance) throws Exception {
        //Build ModuleDMO
        final ModuleDataModelOracleBuilder moduleBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());
        final ModuleDataModelOracleImpl moduleLoader = new ModuleDataModelOracleImpl();

        final ClassFactBuilder cb = new ClassFactBuilder(moduleBuilder,
                                                         clazz,
                                                         false,
                                                         type -> TypeSource.JAVA_PROJECT);
        cb.build(moduleLoader);

        //Build PackageDMO
        final PackageDataModelOracleBuilder packageBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(),
                                                                                                                   "org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations");
        packageBuilder.setModuleOracle(moduleLoader);
        final PackageDataModelOracle packageLoader = packageBuilder.build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getModuleModelFields());
        dataModel.setTypeAnnotations(packageLoader.getModuleTypeAnnotations());
        dataModel.setTypeFieldsAnnotations(packageLoader.getModuleTypeFieldsAnnotations());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        return oracle;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkValidationWhenFactTypeIsNull() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(true);
        setupMockNotNullConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation1.class,
                                                             validatorInstance);

        oracle.validateField(null,
                             "name",
                             null,
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkValidationWhenFieldNameIsNull() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(true);
        setupMockNotNullConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation1.class,
                                                             validatorInstance);

        oracle.validateField("SmurfValidation1",
                             null,
                             null,
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkValidationWhenCallbackIsNull() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(true);
        setupMockNotNullConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation1.class,
                                                             validatorInstance);

        oracle.validateField("SmurfValidation1",
                             "name",
                             null,
                             null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkValidationWhenNoDynamicValidatorInstanceAvailable() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(true);
        setupMockNotNullConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation1.class,
                                                             validatorInstance);

        oracle.validateField("SmurfValidation1",
                             "name",
                             null,
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
        oracle.validateField("SmurfValidation1",
                             "name",
                             "Pupa",
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDynamicValidatorIsInvokedSingleConstraint() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(false);
        when(validatorInstance.get()).thenReturn(validator);
        setupMockNotNullConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation1.class,
                                                             validatorInstance);

        oracle.validateField("SmurfValidation1",
                             "name",
                             null,
                             (Set<ConstraintViolation<String>> violations) -> {
                                 assertEquals(1,
                                              violations.size());
                                 assertTrue(violations.iterator().next().getMessage().contains(NULL_CONSTRAINT_ERROR));
                             });
        oracle.validateField("SmurfValidation1",
                             "name",
                             "Pupa",
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDynamicValidatorIsInvokedMultipleConstraints() throws Exception {
        final Instance<DynamicValidator> validatorInstance = mock(Instance.class);
        final DynamicValidator validator = mock(DynamicValidator.class);
        when(validatorInstance.isUnsatisfied()).thenReturn(false);
        when(validatorInstance.get()).thenReturn(validator);
        setupMockNotNullConstraint(validator);
        setupMockSizeConstraint(validator);

        final AsyncPackageDataModelOracle oracle = getOracle(SmurfValidation2.class,
                                                             validatorInstance);

        oracle.validateField("SmurfValidation2",
                             "name",
                             null,
                             (Set<ConstraintViolation<String>> violations) -> {
                                 assertEquals(1,
                                              violations.size());
                                 assertTrue(violations.iterator().next().getMessage().contains(NULL_CONSTRAINT_ERROR));
                             });
        oracle.validateField("SmurfValidation2",
                             "name",
                             "Pupa",
                             (Set<ConstraintViolation<String>> violations) -> {
                                 assertEquals(1,
                                              violations.size());
                                 assertTrue(violations.iterator().next().getMessage().contains(RANGE_CONSTRAINT_ERROR));
                             });
        oracle.validateField("SmurfValidation2",
                             "name",
                             "Smurfette",
                             (Set<ConstraintViolation<String>> violations) -> {
                                 assertEquals(1,
                                              violations.size());
                                 assertTrue(violations.iterator().next().getMessage().contains(RANGE_CONSTRAINT_ERROR));
                             });
        oracle.validateField("SmurfValidation2",
                             "name",
                             "Brains",
                             (Set<ConstraintViolation<String>> violations) -> assertEquals(0,
                                                                                           violations.size()));
    }

    @SuppressWarnings("unchecked")
    private void setupMockNotNullConstraint(final DynamicValidator validator) {
        when(validator.validate(eq(NotNull.class.getName()),
                                any(Map.class),
                                any())).thenAnswer((InvocationOnMock invocation) -> {
            final String value = (String) invocation.getArguments()[2];
            final Set<ConstraintViolation<String>> violations = new HashSet();
            if (value == null) {
                violations.add(ConstraintViolationImpl.<String>builder().setMessage(NULL_CONSTRAINT_ERROR).build());
            }
            return violations;
        });
    }

    @SuppressWarnings("unchecked")
    private void setupMockSizeConstraint(final DynamicValidator validator) {
        when(validator.validate(eq(Size.class.getName()),
                                any(Map.class),
                                any())).thenAnswer((InvocationOnMock invocation) -> {
            final Map<String, Object> parameters = (Map) invocation.getArguments()[1];
            final int min = (Integer) parameters.get("min");
            final int max = (Integer) parameters.get("max");
            final String value = (String) invocation.getArguments()[2];
            final Set<ConstraintViolation<String>> violations = new HashSet();
            if (value != null && (value.length() < min || value.length() > max)) {
                violations.add(ConstraintViolationImpl.<String>builder().setMessage(RANGE_CONSTRAINT_ERROR).build());
            }
            return violations;
        });
    }
}
