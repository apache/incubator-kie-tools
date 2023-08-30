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


package org.kie.workbench.common.stunner.bpmn.forms.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils.TestCaseBuilder;
import static org.mockito.Mockito.mock;

public class HashCodeAndEqualityTest {

    @Test
    public void testConditionEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new Condition(), new Condition())
                .addTrueCase(new Condition(null), new Condition(null))
                .addTrueCase(new Condition(null, null), new Condition(null, null))
                .addTrueCase(new Condition("function"), new Condition("function"))
                .addTrueCase(new Condition("function", Arrays.asList("param1", "param2")), new Condition("function", Arrays.asList("param1", "param2")))
                .addFalseCase(new Condition("function"), new Condition())
                .addFalseCase(new Condition("function"), new Condition("function1"))
                .addFalseCase(new Condition("function", Arrays.asList("param1", "param2")), new Condition("function"))
                .addFalseCase(new Condition("function", Arrays.asList("param1", "param2")), new Condition("function", Arrays.asList("param11", "param2")))
                .test();
    }

    @Test
    public void testFieldMetadataEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new FieldMetadata(null, null, null, null), new FieldMetadata(null, null, null, null))
                .addTrueCase(new FieldMetadata("name", null, null, null), new FieldMetadata("name", null, null, null))
                .addTrueCase(new FieldMetadata("name", "type", null, null), new FieldMetadata("name", "type", null, null))
                .addTrueCase(new FieldMetadata("name", "type", "accessor", null), new FieldMetadata("name", "type", "accessor", null))
                .addTrueCase(new FieldMetadata("name", "type", "accessor", "mutator"), new FieldMetadata("name", "type", "accessor", "mutator"))
                .addFalseCase(new FieldMetadata(null, null, null, null), new FieldMetadata("name", null, null, null))
                .addFalseCase(new FieldMetadata(null, null, null, null), new FieldMetadata("name", "type", null, null))
                .addFalseCase(new FieldMetadata(null, null, null, null), new FieldMetadata("name", "type", "accessor", "mutator"))
                .addFalseCase(new FieldMetadata("name", null, null, null), new FieldMetadata("name1", null, null, null))
                .addFalseCase(new FieldMetadata("name", "type", null, null), new FieldMetadata("name", "type1", null, null))
                .addFalseCase(new FieldMetadata("name", "type", "accessor", null), new FieldMetadata("name", "type", "accessor1", null))
                .addFalseCase(new FieldMetadata("name", "type", "accessor", "mutator"), new FieldMetadata("name", "type", "accessor", "mutator1"))
                .test();
    }

    @Test
    public void testFunctionDefEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new FunctionDef(null), new FunctionDef(null))
                .addTrueCase(new FunctionDef(null, null), new FunctionDef(null, null))
                .addTrueCase(new FunctionDef("name"), new FunctionDef("name"))
                .addTrueCase(new FunctionDef("name", null), new FunctionDef("name", null))
                .addTrueCase(new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type1"))), new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type1"))))
                .addFalseCase(new FunctionDef("name"), new FunctionDef(null))
                .addFalseCase(new FunctionDef("name", null), new FunctionDef(null, null))
                .addFalseCase(new FunctionDef("name", new ArrayList<>()), new FunctionDef("name", null))
                .addFalseCase(new FunctionDef("name"), new FunctionDef("name1"))
                .addFalseCase(new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type1"))), new FunctionDef("name1", new ArrayList<>()))
                .addFalseCase(new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type1"))), new FunctionDef("name", Arrays.asList(new ParamDef("param2", "type1"))))
                .addFalseCase(new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type1"))), new FunctionDef("name", Arrays.asList(new ParamDef("param1", "type2"))))
                .test();
    }

    @Test
    public void testGenerateConditionResultEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new GenerateConditionResult(null), new GenerateConditionResult(null))
                .addTrueCase(new GenerateConditionResult(null, null), new GenerateConditionResult(null, null))
                .addTrueCase(new GenerateConditionResult("expr"), new GenerateConditionResult("expr"))
                .addTrueCase(new GenerateConditionResult("expr", "error"), new GenerateConditionResult("expr", "error"))
                .addFalseCase(new GenerateConditionResult("expr"), new GenerateConditionResult(null))
                .addFalseCase(new GenerateConditionResult("expr", "error"), new GenerateConditionResult("expr", null))
                .addFalseCase(new GenerateConditionResult("expr"), new GenerateConditionResult("expr1"))
                .addFalseCase(new GenerateConditionResult("expr", "error"), new GenerateConditionResult("expr", "error1"))
                .addFalseCase(new GenerateConditionResult("expr", "error"), new GenerateConditionResult("expr1", "error1"))
                .test();
    }

    @Test
    public void testParamDefEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ParamDef(null, null), new ParamDef(null, null))
                .addTrueCase(new ParamDef("name", null), new ParamDef("name", null))
                .addTrueCase(new ParamDef("name", "type"), new ParamDef("name", "type"))
                .addFalseCase(new ParamDef("name", "type"), new ParamDef("name", null))
                .addFalseCase(new ParamDef("name", "type"), new ParamDef(null, "type"))
                .addFalseCase(new ParamDef("name", null), new ParamDef("name1", null))
                .addFalseCase(new ParamDef("name", "type"), new ParamDef("name", "type1"))
                .addFalseCase(new ParamDef("name", "type"), new ParamDef("name1", "type1"))
                .test();
    }

    @Test
    public void testParseConditionResultEqualsAndHashCode() {
        TestCaseBuilder.newTestCase()
                .addTrueCase(new ParseConditionResult(null, null), new ParseConditionResult(null, null))
                .addTrueCase(new ParseConditionResult(new Condition(null, null)), new ParseConditionResult(new Condition(null, null)))
                .addTrueCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1"))), new ParseConditionResult(new Condition("function", Arrays.asList("param1"))))
                .addTrueCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error"), new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error"))
                .addFalseCase(new ParseConditionResult(new Condition(null, null), "error"), new ParseConditionResult(new Condition(null, null), null))
                .addFalseCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1"))), new ParseConditionResult(new Condition("function", null)))
                .addFalseCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error"), new ParseConditionResult(new Condition("function1", Arrays.asList("param1")), "error"))
                .addFalseCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error"), new ParseConditionResult(new Condition("function", Arrays.asList("param2")), "error"))
                .addFalseCase(new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error"), new ParseConditionResult(new Condition("function", Arrays.asList("param1")), "error1"))
                .test();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypeMetadataQueryEqualsAndHashCode() {
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        Set<String> set1 = mock(Set.class);
        Set<String> set2 = mock(Set.class);

        TestCaseBuilder.newTestCase()
                .addTrueCase(new TypeMetadataQuery(null, null), new TypeMetadataQuery(null, null))
                .addTrueCase(new TypeMetadataQuery(path1, null), new TypeMetadataQuery(path1, null))
                .addTrueCase(new TypeMetadataQuery(path1, set1), new TypeMetadataQuery(path1, set1))
                .addFalseCase(new TypeMetadataQuery(path1, null), new TypeMetadataQuery(null, null))
                .addFalseCase(new TypeMetadataQuery(path1, null), new TypeMetadataQuery(path2, null))
                .addFalseCase(new TypeMetadataQuery(path1, set1), new TypeMetadataQuery(path1, null))
                .addFalseCase(new TypeMetadataQuery(path1, set1), new TypeMetadataQuery(path1, set2))
                .addFalseCase(new TypeMetadataQuery(path1, set1), new TypeMetadataQuery(path2, set2))
                .test();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypeMetadataQueryResultEqualsAndHashCode() {
        Set<TypeMetadata> metadatas1 = mock(Set.class);
        Set<String> missingTypes1 = mock(Set.class);
        Set<TypeMetadata> metadatas2 = mock(Set.class);
        Set<String> missingTypes2 = mock(Set.class);
        TestCaseBuilder.newTestCase()
                .addTrueCase(new TypeMetadataQueryResult(null, null), new TypeMetadataQueryResult(null, null))
                .addTrueCase(new TypeMetadataQueryResult(metadatas1, null), new TypeMetadataQueryResult(metadatas1, null))
                .addTrueCase(new TypeMetadataQueryResult(metadatas1, missingTypes1), new TypeMetadataQueryResult(metadatas1, missingTypes1))
                .addFalseCase(new TypeMetadataQueryResult(metadatas1, null), new TypeMetadataQueryResult(null, null))
                .addFalseCase(new TypeMetadataQueryResult(metadatas1, null), new TypeMetadataQueryResult(metadatas2, null))
                .addFalseCase(new TypeMetadataQueryResult(metadatas1, missingTypes1), new TypeMetadataQueryResult(metadatas1, null))
                .addFalseCase(new TypeMetadataQueryResult(metadatas1, missingTypes1), new TypeMetadataQueryResult(metadatas1, missingTypes2))
                .addFalseCase(new TypeMetadataQueryResult(metadatas1, missingTypes1), new TypeMetadataQueryResult(metadatas2, missingTypes2))
                .test();
    }

    @Test
    public void testTypeMetadataEqualsAndHashCode() {
        FieldMetadata fieldMetadata1 = mock(FieldMetadata.class);
        FieldMetadata fieldMetadata2 = mock(FieldMetadata.class);
        TestCaseBuilder.newTestCase()
                .addTrueCase(new TypeMetadata(null), new TypeMetadata(null))
                .addTrueCase(new TypeMetadata(null, null), new TypeMetadata(null, null))
                .addTrueCase(new TypeMetadata("type"), new TypeMetadata("type"))
                .addTrueCase(new TypeMetadata("type", null), new TypeMetadata("type", null))
                .addTrueCase(new TypeMetadata("type", Arrays.asList(fieldMetadata1)), new TypeMetadata("type", Arrays.asList(fieldMetadata1)))
                .addFalseCase(new TypeMetadata("type"), new TypeMetadata(null))
                .addFalseCase(new TypeMetadata("type", null), new TypeMetadata(null, null))
                .addFalseCase(new TypeMetadata("type", new ArrayList<>()), new TypeMetadata("type", null))
                .addFalseCase(new TypeMetadata("type"), new TypeMetadata("type1"))
                .addFalseCase(new TypeMetadata("type", Arrays.asList(fieldMetadata1)), new TypeMetadata("type1", new ArrayList<>()))
                .addFalseCase(new TypeMetadata("type", Arrays.asList(fieldMetadata1)), new TypeMetadata("type", Arrays.asList(fieldMetadata2)))
                .addFalseCase(new TypeMetadata("type", new ArrayList<>()), new TypeMetadata("type", Arrays.asList(fieldMetadata1)))
                .test();
    }
}
