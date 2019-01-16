/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFieldInspector.FieldInfo;

import static org.junit.Assert.assertEquals;

public class ClassFieldInspectorTest {

    private ClassFieldInspector bean1Inspector;

    private ClassFieldInspector bean2Inspector;

    private Map<String, FieldInfo> bean1Fields;

    private Map<String, FieldInfo> bean2Fields;

    @Before
    public void setUp() {
        bean1Inspector = new ClassFieldInspector(Bean1.class);
        bean2Inspector = new ClassFieldInspector(Bean2.class);
        bean1Fields = buildBean1ExpectedFields(ModelField.FIELD_ORIGIN.DECLARED);
        bean2Fields = buildBean2ExpectedFields();
    }

    @Test
    public void testGetFieldTypesInfo() {
        Map<String, FieldInfo> result = bean1Inspector.getFieldTypesFieldInfo();
        assertEquals(bean1Fields, result);
        result = bean2Inspector.getFieldTypesFieldInfo();
        assertEquals(bean2Fields, result);
    }

    private Map<String, FieldInfo> buildBean1ExpectedFields(ModelField.FIELD_ORIGIN origin) {
        Map<String, FieldInfo> result = new HashMap<>();
        Annotation annotation1 = new Annotation(Annotation1.class.getName());
        Annotation annotation2 = new Annotation(Annotation2.class.getName());
        annotation2.addParameter("param1", "value1");
        annotation2.addParameter("param2", "value2");
        Set<Annotation> annotations = new HashSet<>();
        annotations.add(annotation1);
        annotations.add(annotation2);

        result.put("fieldBean1_1", new FieldInfo(FieldAccessorsAndMutators.BOTH, String.class, String.class, origin, annotations));
        result.put("fieldBean1_2", new FieldInfo(FieldAccessorsAndMutators.ACCESSOR, Integer.TYPE, Integer.TYPE, origin, new HashSet<>()));
        result.put("fieldBean1_3", new FieldInfo(FieldAccessorsAndMutators.BOTH, Boolean.TYPE, Boolean.TYPE, origin, new HashSet<>()));
        result.put("fieldBean1_4", new FieldInfo(FieldAccessorsAndMutators.MUTATOR, Integer.class, Integer.class, origin, new HashSet<>()));
        return result;
    }

    private Map<String, FieldInfo> buildBean2ExpectedFields() {
        Map<String, FieldInfo> result = new HashMap<>();
        Annotation annotation2 = new Annotation(Annotation2.class.getName());
        annotation2.addParameter("param1", "value3");
        annotation2.addParameter("param2", "value4");
        Set<Annotation> annotations = new HashSet<>();
        annotations.add(annotation2);

        result.putAll(buildBean1ExpectedFields(ModelField.FIELD_ORIGIN.INHERITED));
        result.put("fieldBean2_1", new FieldInfo(FieldAccessorsAndMutators.BOTH, String.class, String.class, ModelField.FIELD_ORIGIN.DECLARED, new HashSet<>()));
        result.put("fieldBean2_2", new FieldInfo(FieldAccessorsAndMutators.BOTH, Object.class, Object.class, ModelField.FIELD_ORIGIN.DECLARED, annotations));
        return result;
    }

    private class Bean1 {

        @Annotation1
        @Annotation2(param1 = "value1", param2 = "value2")
        public String fieldBean1_1;

        private int fieldBean1_2;

        private boolean fieldBean1_3;

        private Integer fieldBean1_4;

        @Annotation1
        public String getFieldBean1_1() {
            return fieldBean1_1;
        }

        public void setFieldBean1_1(String fieldBean1_1) {
            this.fieldBean1_1 = fieldBean1_1;
        }

        public int getFieldBean1_2() {
            return fieldBean1_2;
        }

        public boolean isFieldBean1_3() {
            return fieldBean1_3;
        }

        public void setFieldBean1_3(boolean fieldBean1_3) {
            this.fieldBean1_3 = fieldBean1_3;
        }

        public void setFieldBean1_4(Integer fieldBean1_4) {
            this.fieldBean1_4 = fieldBean1_4;
        }
    }

    private class Bean2 extends Bean1 {

        private String fieldBean2_1;

        @Annotation2(param1 = "value3", param2 = "value4")
        private Object fieldBean2_2;

        public String getFieldBean2_1() {
            return fieldBean2_1;
        }

        public void setFieldBean2_1(String fieldBean2_1) {
            this.fieldBean2_1 = fieldBean2_1;
        }

        public Object getFieldBean2_2() {
            return fieldBean2_2;
        }

        public void setFieldBean2_2(Object fieldBean2_2) {
            this.fieldBean2_2 = fieldBean2_2;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Annotation1 {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Annotation2 {

        String param1();

        String param2();
    }
}
