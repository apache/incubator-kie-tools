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
package org.kie.workbench.common.services.datamodeller.driver.package3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;

/**
 * This class is intended for different tests. In case you change it be sure the tests still works.
 */

@ClassAnnotation( classParam = java.util.List.class,
        classArrayParam = { List.class, Collection.class, Map.class, Set.class }
)
public class ClassAnnotationTest implements Serializable {

    @ClassAnnotation( classParam = java.util.List.class,
            classArrayParam = { List.class, Collection.class, Map.class, Set.class }
    )
    String field1;

}
