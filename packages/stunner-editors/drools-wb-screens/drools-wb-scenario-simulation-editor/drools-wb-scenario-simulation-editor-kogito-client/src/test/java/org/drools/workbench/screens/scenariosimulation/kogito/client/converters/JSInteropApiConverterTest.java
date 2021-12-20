/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.converters;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSISettingsType;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class JSInteropApiConverterTest {

    @Test
    public void getSettings() {
        JSISettingsType jsiSettingsTypeMock = mock(JSISettingsType.class);
        when(jsiSettingsTypeMock.getType()).thenReturn(ScenarioSimulationModel.Type.RULE.toString());
        assertNotNull(JSInteropApiConverter.getSettings(jsiSettingsTypeMock));
        checkAllRequiredMethodsAreCalled(JSISettingsType.class, Settings.class, jsiSettingsTypeMock);
    }

    @Test
    public void getImport() {
        JSIImportType jsiImportTypeMock = mock(JSIImportType.class);
        assertNotNull(JSInteropApiConverter.getImport(jsiImportTypeMock));
        checkAllRequiredMethodsAreCalled(JSIImportType.class, Import.class, jsiImportTypeMock);
    }

    @Test
    public void getImports() {
        JSIImportsType jsiImportsTypeMock = mock(JSIImportsType.class);
        assertNotNull(JSInteropApiConverter.getImports(jsiImportsTypeMock));
        checkAllRequiredMethodsAreCalled(JSIImportsType.class, Imports.class, jsiImportsTypeMock);
    }

    @Test
    public void getExpressionIdentifier() {
        JSIExpressionIdentifierType jsiExpressionIdentifierTypeMock = mock(JSIExpressionIdentifierType.class);
        when(jsiExpressionIdentifierTypeMock.getType()).thenReturn(FactMappingType.OTHER.toString());
        assertNotNull(JSInteropApiConverter.getExpressionIdentifier(jsiExpressionIdentifierTypeMock));
        checkAllRequiredMethodsAreCalled(JSIExpressionIdentifierType.class, ExpressionIdentifier.class, jsiExpressionIdentifierTypeMock);
    }

    @Test
    public void getFactIdentifier() {
        JSIFactIdentifierType jsiFactIdentifierTypeMock = mock(JSIFactIdentifierType.class);
        assertNotNull(JSInteropApiConverter.getFactIdentifier(jsiFactIdentifierTypeMock));
        checkAllRequiredMethodsAreCalled(JSIFactIdentifierType.class, FactIdentifier.class, jsiFactIdentifierTypeMock);
    }

    private void checkAllRequiredMethodsAreCalled(Class inputClazz, Class outputClazz, Object mock) {
        try {
            PropertyDescriptor[] inputPDs =
                    Introspector.getBeanInfo(inputClazz, Object.class).getPropertyDescriptors();
            int inputClassMethodsNumber = inputPDs.length - 1;
            Set<String> outputFieldNames = retrieveClassFieldNames(outputClazz);
            int outputClassMethodsNumber = outputFieldNames.size();
            assertSame("Not all fields are managed in the converter of class " + inputClazz.getSimpleName(),
                       inputClassMethodsNumber, outputClassMethodsNumber);
            for (PropertyDescriptor pd : inputPDs) {
                if ("TYPE_NAME".equals(pd.getName())) {
                    continue;
                }
                assertTrue("Field " + pd.getName() + " isn't correctly managed in the converter!",
                           outputFieldNames.contains(pd.getName()));
                Method method = pd.getReadMethod();
                method.invoke(verify(mock, times(1)));
            }
            verifyNoMoreInteractions(mock);
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * It retrieves all NOT STATIC fields of a class and its superclass
     * @param clazz
     * @return
     */
    private Set<String> retrieveClassFieldNames(Class clazz) {
        Set<String> returns = new HashSet<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                returns.add(f.getName());
            }
        }
        if (clazz.getSuperclass() != null) {
            returns.addAll(retrieveClassFieldNames(clazz.getSuperclass()));
        }
        return returns;
    }
}
