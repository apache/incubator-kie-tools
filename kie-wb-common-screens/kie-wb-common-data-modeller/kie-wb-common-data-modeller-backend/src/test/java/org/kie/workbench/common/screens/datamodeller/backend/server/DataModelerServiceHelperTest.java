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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.forge.roaster.model.SyntaxError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataModelerServiceHelperTest {

    private static final String JAVA_FILE_1_CLASS = "com.myspace.myproject.Pojo1";
    private static final String JAVA_FILE_1_MAIN_PATH = "myProject/src/main/java/com/myspace/myproject/Pojo1.java";
    private static final String JAVA_FILE_1_TEST_PATH = "myProject/src/test/java/com/myspace/myproject/Pojo1.java";

    private static final String JAVA_FILE_2_CLASS = "com.myspace.myproject.Pojo2";
    private static final String JAVA_FILE_2_MAIN_PATH = "myProject/src/main/java/com/myspace/myproject/Pojo2.java";
    private static final String JAVA_FILE_2_TEST_PATH = "myProject/src/test/java/com/myspace/myproject/Pojo2.java";

    private static final String JAVA_FILE_3_CLASS = "com.myspace.myproject.Pojo背景色3";
    private static final String JAVA_FILE_3_MAIN_PATH = "myProject/src/main/java/com/myspace/myproject/Pojo背景色3.java";
    private static final String JAVA_FILE_3_TEST_PATH = "myProject/src/test/java/com/myspace/myproject/Pojo背景色3.java";

    private static final String JAVA_FILE_IN_OTHER_MODULE = "someOtherProject/src/main/java/com/myspace/myproject/Pojo1.java";

    private static final String JAVA_FILE_FOR_ERRORS_TEST = "errorsTestProject/src/main/java/com/myspace/myproject/SomeFile.java";

    private static final String NEW_PACKAGE_NAME = "one.two.three";

    @Mock
    private KieModuleService moduleService;

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private DataModelerServiceHelper serviceHelper;

    private org.uberfire.java.nio.file.Path testRootPath;

    private Module module;

    private Package defaultPackage;

    @Before
    public void setUp() throws Exception {
        SimpleFileSystemProvider fileSystemProvider = new SimpleFileSystemProvider();

        serviceHelper = new DataModelerServiceHelper(moduleService,
                                                     ioService,
                                                     commentedOptionFactory);

        String rootURI = this.getClass().getResource("DataModelerServiceHelperTest.txt").toURI().toString();
        URI rootPathURI = URI.create(rootURI.substring(0,
                                                         rootURI.length() - "DataModelerServiceHelperTest.txt".length()));
        testRootPath = fileSystemProvider.getPath(rootPathURI);
    }

    @Test
    public void testToDataModelerErrorFromDriverError() {
        ArrayList<DriverError> driverErrors = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            driverErrors.add(new DriverError(i, "message"+i, mock(Path.class), i, i));
        }
        List<DataModelerError> result = serviceHelper.toDataModelerError(driverErrors);
        assertEquals(driverErrors.size(), result.size());
        for (int i = 0; i < driverErrors.size(); i++) {
            assertErrorEquals(driverErrors.get(i), result.get(i));
        }
    }

    private void assertErrorEquals(DriverError driverError,
                                   DataModelerError dataModelerError) {
        assertEquals(driverError.getId(), dataModelerError.getId());
        assertEquals(driverError.getMessage(), dataModelerError.getMessage());
        assertEquals(Level.ERROR, dataModelerError.getLevel());
        assertEquals(driverError.getFile(), dataModelerError.getFile());
        assertEquals(driverError.getLine(), dataModelerError.getLine());
        assertEquals(driverError.getColumn(), dataModelerError.getColumn());
    }

    @Test
    public void testToDataModelerErrorFromSyntaxError() {
        ArrayList<SyntaxError> syntaxErrors = new ArrayList<>();
        SyntaxError syntaxError;
        org.uberfire.java.nio.file.Path path = testRootPath.resolve(JAVA_FILE_FOR_ERRORS_TEST);

        for(int i = 0; i < 10; i++) {
            syntaxError = mock(SyntaxError.class);
            when(syntaxError.getDescription()).thenReturn("message"+i);
            if (i % 2 == 0) {
                when(syntaxError.isError()).thenReturn(true);
            } else {
                when(syntaxError.isError()).thenReturn(false);
            }
            when(syntaxError.getLine()).thenReturn(i);
            when(syntaxError.getColumn()).thenReturn(i);
            syntaxErrors.add(syntaxError);
        }
        List<DataModelerError> result = serviceHelper.toDataModelerError(syntaxErrors, path);
        assertEquals(syntaxErrors.size(), result.size());
        for (int i = 0; i < syntaxErrors.size(); i++) {
            assertErrorEquals(syntaxErrors.get(i), result.get(i), Paths.convert(path));
        }
    }

    private void assertErrorEquals(SyntaxError syntaxError,
                                   DataModelerError dataModelerError,
                                   Path expectedPath) {
        assertEquals(syntaxError.getDescription(), dataModelerError.getMessage());
        assertEquals(syntaxError.getColumn(), dataModelerError.getColumn());
        assertEquals(syntaxError.getLine(), dataModelerError.getLine());
        assertEquals(syntaxError.isError() ? Level.ERROR : Level.WARNING, dataModelerError.getLevel());
        assertEquals(expectedPath, dataModelerError.getFile());
    }

    @Test
    public void testToValidationMessageFromDataModelerError() {
        ArrayList<DataModelerError> dataModelerErrors = new ArrayList<>();
        DataModelerError dataModelerError;
        Level level;

        for(int i = 0; i < 10; i++) {
            level = Level.values()[i % 3];
            dataModelerError = new DataModelerError(i, "message"+i,level, mock(Path.class), i, i);
            dataModelerErrors.add(dataModelerError);
        }
        List<ValidationMessage> result = serviceHelper.toValidationMessage(dataModelerErrors);
        assertEquals(dataModelerErrors.size(), result.size());
        for (int i = 0; i < dataModelerErrors.size(); i++) {
            assertErrorEquals(dataModelerErrors.get(i), result.get(i));
        }
    }

   private void assertErrorEquals(DataModelerError dataModelerError,
                                  ValidationMessage validationMessage) {
        assertEquals(dataModelerError.getMessage(), validationMessage.getText());
        assertEquals(dataModelerError.getColumn(), validationMessage.getColumn());
        assertEquals(dataModelerError.getLine(), validationMessage.getLine());
        assertEquals(dataModelerError.getId(), validationMessage.getId());
        assertEquals(dataModelerError.getFile(), validationMessage.getPath());
    }

    @Test
    public void testCalculateClassName() {
        Module module = mock(Module.class);
        Package defaultPackage = mock(Package.class);

        Path javaFile1Main = Paths.convert(testRootPath.resolve(JAVA_FILE_1_MAIN_PATH));
        Path javaFile1Test = Paths.convert(testRootPath.resolve(JAVA_FILE_1_TEST_PATH));

        Path javaFile2Main = Paths.convert(testRootPath.resolve(JAVA_FILE_2_MAIN_PATH));
        Path javaFile2Test = Paths.convert(testRootPath.resolve(JAVA_FILE_2_TEST_PATH));

        Path javaFile3Main = Paths.convert(testRootPath.resolve(JAVA_FILE_3_MAIN_PATH));
        Path javaFile3Test = Paths.convert(testRootPath.resolve(JAVA_FILE_3_TEST_PATH));

        Path javaFileInOtherModule = Paths.convert(testRootPath.resolve(JAVA_FILE_IN_OTHER_MODULE));

        //no default package case
        assertNull(serviceHelper.calculateClassName(module, null));
        assertNull(serviceHelper.calculateClassName(module, javaFile1Main));
        assertNull(serviceHelper.calculateClassName(module, javaFile1Test));
        assertNull(serviceHelper.calculateClassName(module, javaFile2Main));
        assertNull(serviceHelper.calculateClassName(module, javaFile2Test));
        assertNull(serviceHelper.calculateClassName(module, javaFile3Main));
        assertNull(serviceHelper.calculateClassName(module, javaFile3Test));
        assertNull(serviceHelper.calculateClassName(module, javaFileInOtherModule));

        //default package exists
        when(moduleService.resolveDefaultPackage(module)).thenReturn(defaultPackage);

        Path mainSrcPath = Paths.convert(testRootPath.resolve("myProject/src/main/java"));
        Path testSrcPath = Paths.convert(testRootPath.resolve("myProject/src/test/java"));

        when(defaultPackage.getPackageMainSrcPath()).thenReturn(mainSrcPath);
        when(defaultPackage.getPackageTestSrcPath()).thenReturn(testSrcPath);

        assertNull(serviceHelper.calculateClassName(module, null));
        assertEquals(JAVA_FILE_1_CLASS, serviceHelper.calculateClassName(module, javaFile1Main));
        assertEquals(JAVA_FILE_1_CLASS, serviceHelper.calculateClassName(module, javaFile1Test));
        assertEquals(JAVA_FILE_2_CLASS, serviceHelper.calculateClassName(module, javaFile2Main));
        assertEquals(JAVA_FILE_2_CLASS, serviceHelper.calculateClassName(module, javaFile2Test));
        assertEquals(JAVA_FILE_3_CLASS, serviceHelper.calculateClassName(module, javaFile3Main));
        assertEquals(JAVA_FILE_3_CLASS, serviceHelper.calculateClassName(module, javaFile3Test));
        assertNull(serviceHelper.calculateClassName(module, javaFileInOtherModule));
    }

    @Test
    public void testMakeCommentedOption() {
        String message = "message";
        CommentedOption commentedOption = mock(CommentedOption.class);
        when(commentedOptionFactory.makeCommentedOption(message)).thenReturn(commentedOption);
        assertEquals(commentedOption, serviceHelper.makeCommentedOption(message));
    }

    @Test
    public void testEnsurePackageStructureWhenNoValuesProvided() {
        prepareEnsurePackageTest();

        assertNull(serviceHelper.ensurePackageStructure(module, null));
        assertNull(serviceHelper.ensurePackageStructure(module, ""));
    }

    @Test
    public void testEnsurePackageStructureWhenProvidedPackageAlreadyExists() {
        prepareEnsurePackageTest();

        Package existingPackage = mock(Package.class);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(moduleService.resolvePackage(any(Path.class))).thenReturn(existingPackage);
        Package result = serviceHelper.ensurePackageStructure(module, NEW_PACKAGE_NAME);
        assertEquals(existingPackage, result);
        verify(moduleService, never()).newPackage(any(Package.class), anyString());
    }

    @Test
    public void testEnsurePackageStructureWhenProvidedPackageDoNotExists() {
        prepareEnsurePackageTest();

        Package createdPackage = mock(Package.class);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        when(moduleService.newPackage(defaultPackage, NEW_PACKAGE_NAME)).thenReturn(createdPackage);
        Package result = serviceHelper.ensurePackageStructure(module, NEW_PACKAGE_NAME);
        assertEquals(createdPackage, result);
        verify(moduleService).newPackage(defaultPackage, NEW_PACKAGE_NAME);
    }

    private void prepareEnsurePackageTest() {
        module = mock(Module.class);
        defaultPackage = mock(Package.class);
        Path mainSrcPath = Paths.convert(testRootPath.resolve("myProject/src/main/java"));
        when(moduleService.resolveDefaultPackage(module)).thenReturn(defaultPackage);
        when(defaultPackage.getPackageMainSrcPath()).thenReturn(mainSrcPath);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvePackages() {
        Module module = mock(Module.class);
        Set<Package> packages = new HashSet();
        Package pkg;
        for (int i = 0; i < 10; i++) {
            pkg = mock(Package.class);
            when(pkg.getPackageName()).thenReturn("package"+i);
            packages.add(pkg);
        }
        when(moduleService.resolvePackages(module)).thenReturn(packages);
        Set<String> result = serviceHelper.resolvePackages(module);
        assertEquals(packages.size(), result.size());
        packages.forEach(existingPkg -> assertTrue(result.contains(existingPkg.getPackageName())));
    }

    @Test
    public void testCalculateFilePath() {
        org.uberfire.java.nio.file.Path javaPath = testRootPath;
        String className = "PojoWithNoPackage背景色";
        String expectedFile = testRootPath.toString() + "/PojoWithNoPackage背景色.java";
        org.uberfire.java.nio.file.Path result = serviceHelper.calculateFilePath(className, javaPath);
        assertEquals(expectedFile, result.toString());

        className = "com.myspace.PojoWithPackage背景色";
        expectedFile = testRootPath.toString() + "/com/myspace/PojoWithPackage背景色.java";
        result = serviceHelper.calculateFilePath(className, javaPath);
        assertEquals(expectedFile, result.toString());
    }
}
