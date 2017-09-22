/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Import;
import org.kie.workbench.common.services.datamodeller.core.JavaClass;
import org.kie.workbench.common.services.datamodeller.core.JavaEnum;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Parameter;
import org.kie.workbench.common.services.datamodeller.core.Type;

import static org.junit.Assert.*;

public class DataModelerAssert {

    public static void assertEqualsDataObject(DataObject obj1,
                                              DataObject obj2) {
        if (obj1 != null) {
            assertNotNull(obj2);

            assertEqualsJavaClass(obj1, obj2);
            assertEqualsProperties(obj1.getProperties(),
                                   obj2.getProperties());
        } else {
            assertNull(obj2);
        }
    }

    public static void assertEqualsJavaClass(JavaClass obj1,
                                             JavaClass obj2) {
        if (obj1 != null) {
            assertNotNull(obj2);

            assertEquals(obj1.getPackageName(),
                         obj2.getPackageName());
            assertEquals(obj1.getName(),
                         obj2.getName());
            assertEqualsInterfaces(obj1.getInterfaces(),
                                   obj2.getInterfaces());
            assertEqualsAnnotations(obj1.getAnnotations(),
                                    obj2.getAnnotations());
            assertEqualsMethods(obj1.getMethods(),
                                obj2.getMethods());
            assertEqualsNestedClasses(obj1.getNestedClasses(),
                                      obj2.getNestedClasses());
        } else {
            assertNull(obj2);
        }
    }

    public static void assertEqualsProperties(List<ObjectProperty> properties1,
                                              List<ObjectProperty> properties2) {
        if (properties1 != null) {

            assertNotNull(properties2);
            assertEquals(properties1.size(),
                         properties2.size());

            for (int i = 0; i < properties1.size(); i++) {
                assertEqualsProperty(properties1.get(i),
                                     properties2.get(i));
            }
        }
    }

    public static void assertEqualsProperty(ObjectProperty property1,
                                            ObjectProperty property2) {
        if (property1 != null) {
            assertNotNull(property2);
            assertEquals(property1.getName(),
                         property2.getName());
            assertEquals(property1.getClassName(),
                         property2.getClassName());
            assertEquals(property1.isMultiple(),
                         property2.isMultiple());
            if (property1.isMultiple()) {
                assertEquals(property1.getBag(),
                             property2.getBag());
            }
            assertEqualsAnnotations(property1.getAnnotations(),
                                    property2.getAnnotations());
        } else {
            assertNull(property2);
        }
    }

    public static void assertEqualsImports(List<Import> imports1,
                                           List<Import> imports2) {
        if (imports1 != null) {
            assertNotNull(imports2);
            assertEquals(imports1.size(),
                         imports2.size());

            Map<String, Import> importMap = new HashMap<>();
            for (Import _import : imports2) {
                importMap.put(_import.getName(),
                              _import);
            }

            for (Import _import : imports1) {
                assertEquals(_import,
                             importMap.get(_import.getName()));
            }
        } else {
            assertNull(imports2);
        }
    }

    public static void assertEqualsInterfaces(List<String> interfaces1,
                                              List<String> interfaces2) {
        if (interfaces1 != null) {
            assertNotNull(interfaces2);
            assertEquals(interfaces1.size(),
                         interfaces2.size());

            assertTrue(interfaces1.containsAll(interfaces2));
        } else {
            assertNull(interfaces2);
        }
    }

    public static void assertEqualsAnnotations(List<Annotation> annotations1,
                                               List<Annotation> annotations2) {
        if (annotations1 != null) {
            assertNotNull(annotations2);
            assertEquals(annotations1.size(),
                         annotations2.size());

            Map<String, Annotation> annotationMap = new HashMap<String, Annotation>();
            for (Annotation annotation : annotations2) {
                annotationMap.put(annotation.getClassName(),
                                  annotation);
            }

            for (Annotation annotation : annotations1) {
                assertEqualsAnnotation(annotation,
                                       annotationMap.get(annotation.getClassName()));
            }
        } else {
            assertNull(annotations2);
        }
    }

    public static void assertEqualsAnnotation(Annotation annotation1,
                                              Annotation annotation2) {
        if (annotation1 != null) {
            assertNotNull(annotation2);

            assertEquals(annotation1.getClassName(),
                         annotation2.getClassName());
            assertEquals(annotation1.getValues().size(),
                         annotation2.getValues().size());
            assertEqualsAnnotationDefinition(annotation1.getAnnotationDefinition(),
                                             annotation2.getAnnotationDefinition());
            for (String annotationKey : annotation1.getValues().keySet()) {
                if ((annotation1.getValue(annotationKey) instanceof List) && isAnnotationList((List) annotation1.getValue(annotationKey)) &&
                        (annotation2.getValue(annotationKey) instanceof List) && isAnnotationList((List) annotation2.getValue(annotationKey))) {
                    assertEqualsAnnotationList((List) annotation1.getValue(annotationKey),
                                               (List) annotation2.getValue(annotationKey));
                } else if (annotation1.getValue(annotationKey) instanceof Annotation &&
                        annotation2.getValue(annotationKey) instanceof Annotation) {
                    assertEqualsAnnotation((Annotation) annotation1.getValue(annotationKey),
                                           (Annotation) annotation2.getValue(annotationKey));
                } else {
                    assertEquals(annotation1.getValues().get(annotationKey),
                                 annotation2.getValues().get(annotationKey));
                }
            }
        } else {
            assertNull(annotation2);
        }
    }

    private static boolean isAnnotationList(List<?> list) {
        return list.size() > 0 && (list.get(0) instanceof Annotation);
    }

    private static void assertEqualsAnnotationList(List annotations1,
                                                   List annotations2) {
        if (annotations1 != null) {
            assertNotNull(annotations2);
            assertEquals(annotations1.size(),
                         annotations2.size());
            for (int i = 0; i < annotations1.size(); i++) {
                assertEqualsAnnotation((Annotation) annotations1.get(i),
                                       (Annotation) annotations2.get(i));
            }
        } else {
            assertNull(annotations2);
        }
    }

    private static void assertEqualsAnnotationDefinition(AnnotationDefinition annotationDefinition1,
                                                         AnnotationDefinition annotationDefinition2) {
        if (annotationDefinition1 != null) {
            assertNotNull(annotationDefinition2);

            assertEquals(annotationDefinition1.getClassName(),
                         annotationDefinition2.getClassName());
            assertEquals(annotationDefinition1.isTypeAnnotation(),
                         annotationDefinition2.isTypeAnnotation());
            assertEquals(annotationDefinition1.isFieldAnnotation(),
                         annotationDefinition2.isFieldAnnotation());

            assertEquals(annotationDefinition1.isMarker(),
                         annotationDefinition2.isMarker());
            assertEquals(annotationDefinition1.isNormal(),
                         annotationDefinition2.isNormal());
            assertEquals(annotationDefinition1.isSingleValue(),
                         annotationDefinition2.isSingleValue());

            assertEquals(annotationDefinition1.getRetention(),
                         annotationDefinition2.getRetention());
            assertArrayEquals(annotationDefinition1.getTarget().toArray(),
                              annotationDefinition2.getTarget().toArray());

            assertEquals(annotationDefinition1.getValuePairs().size(),
                         annotationDefinition1.getValuePairs().size());

            assertEquals(annotationDefinition1.getValuePairs().size(),
                         annotationDefinition2.getValuePairs().size());
        } else {
            assertNull(annotationDefinition2);
        }
    }

    public static void assertEqualsJavaEnum(JavaEnum javaEnum1,
                                            JavaEnum javaEnum2) {
        assertEquals(javaEnum1.getClassName(),
                     javaEnum2.getClassName());
    }

    public static void assertEqualsAnnotationValuePair(AnnotationValuePairDefinition valuePairDefinition1,
                                                       AnnotationValuePairDefinition valuePairDefinition2) {
        if (valuePairDefinition1 != null) {
            assertNotNull(valuePairDefinition2);

            assertEquals(valuePairDefinition1.getName(),
                         valuePairDefinition2.getName());
            assertEquals(valuePairDefinition1.getClassName(),
                         valuePairDefinition2.getClassName());

            assertEquals(valuePairDefinition1.isAnnotation(),
                         valuePairDefinition2.isAnnotation());
            assertEquals(valuePairDefinition1.isClass(),
                         valuePairDefinition2.isClass());
            assertEquals(valuePairDefinition1.isEnum(),
                         valuePairDefinition2.isEnum());
            assertEquals(valuePairDefinition1.isPrimitiveType(),
                         valuePairDefinition2.isPrimitiveType());
            assertEquals(valuePairDefinition1.isString(),
                         valuePairDefinition2.isString());
            assertEquals(valuePairDefinition1.isArray(),
                         valuePairDefinition2.isArray());

            assertEquals(valuePairDefinition1.hasDefaultValue(),
                         valuePairDefinition2.hasDefaultValue());
            assertEquals(valuePairDefinition1.getDefaultValue(),
                         valuePairDefinition2.getDefaultValue());
            assertEqualsAnnotationDefinition(valuePairDefinition1.getAnnotationDefinition(),
                                             valuePairDefinition2.getAnnotationDefinition());
        } else {
            assertNotNull(valuePairDefinition2);
        }
    }

    public static void assertName(String name,
                                  DataObject dataObject) {
        assertEquals(name,
                     dataObject.getName());
    }

    public static void assertPackageName(String packageName,
                                         DataObject dataObject) {
        assertEquals(packageName,
                     dataObject.getPackageName());
    }

    public static void assertClassName(String className,
                                       DataObject dataObject) {
        assertEquals(className,
                     dataObject.getClassName());
    }

    public static void assertEqualsMethods(List<Method> methods1,
                                           List<Method> methods2) {
        if (methods1 != null) {
            assertNotNull(methods2);
            assertEquals(methods1.size(),
                         methods2.size());

            Map<String, Method> methodMap = new HashMap<>();
            for (Method method : methods2) {
                methodMap.put(method.getName(),
                              method);
            }

            for (Method method : methods1) {
                assertEqualsMethod(method,
                                   methodMap.get(method.getName()));
            }
        } else {
            assertNull(methods2);
        }
    }

    public static void assertEqualsMethod(Method method1,
                                          Method method2) {
        if (method1 != null) {
            assertNotNull(method2);

            assertEquals(method1.getName(),
                         method2.getName());
            assertEquals(method1.getBody(),
                         method2.getBody());
            assertEquals(method1.getReturnType().getName(),
                         method2.getReturnType().getName());
            assertEqualsTypeArguments(method1.getReturnType().getTypeArguments(),
                                      method2.getReturnType().getTypeArguments());
            assertEqualsParameters(method1.getParameters(),
                                   method2.getParameters());
        } else {
            assertNull(method2);
        }
    }

    private static void assertEqualsParameters(List<Parameter> parameters1,
                                               List<Parameter> parameters2) {
        if (parameters1 != null) {
            assertNotNull(parameters2);
            assertEquals(parameters1.size(),
                         parameters2.size());

            for (int i = 0; i < parameters1.size(); i++) {
                Parameter parameter1 = parameters1.get(i);
                Parameter parameter2 = parameters2.get(i);

                assertEquals(parameter1.getName(),
                             parameter2.getName());
                assertEquals(parameter1.getType().getName(),
                             parameter2.getType().getName());
                assertEqualsTypeArguments(parameter1.getType().getTypeArguments(),
                                          parameter2.getType().getTypeArguments());
            }
        } else {
            assertNull(parameters2);
        }
    }

    private static void assertEqualsTypeArguments(List<Type> typeArguments1,
                                                  List<Type> typeArguments2) {
        if (typeArguments1 != null) {
            assertNotNull(typeArguments2);
            assertEquals(typeArguments1.size(),
                         typeArguments2.size());

            for (int i = 0; i < typeArguments1.size(); i++) {
                Type type1 = typeArguments1.get(i);
                Type type2 = typeArguments2.get(i);

                assertEquals(type1.getName(),
                             type2.getName());
                assertEqualsTypeArguments(type1.getTypeArguments(),
                                          type2.getTypeArguments());
            }
        } else {
            assertNull(typeArguments2);
        }
    }

    public static void assertEqualsNestedClasses(List<JavaClass> methods1,
                                                 List<JavaClass> methods2) {
        if (methods1 != null) {
            assertNotNull(methods2);
            assertEquals(methods1.size(),
                         methods2.size());

            Map<String, JavaClass> methodMap = new HashMap<>();
            for (JavaClass method : methods2) {
                methodMap.put(method.getName(),
                              method);
            }

            for (JavaClass method : methods1) {
                assertEqualsJavaClass(method,
                                      methodMap.get(method.getName()));
            }
        } else {
            assertNull(methods2);
        }
    }
}