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
package org.drools.workbench.screens.scenariosimulation.webapp.client.workarounds;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;

@ApplicationScoped
public class KogitoTestingAsyncPackageDataModelOracle extends KogitoAsyncPackageDataModelOracle {

    protected static final String AUTHOR = "Author";

    private final ModelField authorBooks = new ModelField("books",
                                                          List.class.getSimpleName(),
                                                          ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                          ModelField.FIELD_ORIGIN.DECLARED,
                                                          FieldAccessorsAndMutators.BOTH,
                                                          DataType.TYPE_COLLECTION);
    private final ModelField authorCurrentlyPrinted = new ModelField("currentlyPrinted",
                                                                     Map.class.getSimpleName(),
                                                                     ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                     ModelField.FIELD_ORIGIN.DECLARED,
                                                                     FieldAccessorsAndMutators.BOTH,
                                                                     Map.class.getSimpleName());
    private final ModelField authorFirstBook = new ModelField("firstBook",
                                                              "Book",
                                                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                              ModelField.FIELD_ORIGIN.DECLARED,
                                                              FieldAccessorsAndMutators.BOTH,
                                                              "Book");
    private final ModelField authorIsAlive = new ModelField("isAlive",
                                                            Boolean.class.getSimpleName(),
                                                            ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                            ModelField.FIELD_ORIGIN.DECLARED,
                                                            FieldAccessorsAndMutators.BOTH,
                                                            DataType.TYPE_BOOLEAN);
    private final ModelField authorName = new ModelField("name",
                                                         String.class.getSimpleName(),
                                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                         ModelField.FIELD_ORIGIN.DECLARED,
                                                         FieldAccessorsAndMutators.BOTH,
                                                         DataType.TYPE_STRING);
    private final ModelField authorThis = new ModelField("this",
                                                         AUTHOR,
                                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                         ModelField.FIELD_ORIGIN.SELF,
                                                         FieldAccessorsAndMutators.ACCESSOR,
                                                         DataType.TYPE_THIS);
    private final ModelField bookAuthor = new ModelField("author",
                                                         AUTHOR,
                                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                         ModelField.FIELD_ORIGIN.DECLARED,
                                                         FieldAccessorsAndMutators.BOTH,
                                                         AUTHOR);
    private final ModelField bookIsAvailable = new ModelField("isAvailable",
                                                              Boolean.class.getSimpleName(),
                                                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                              ModelField.FIELD_ORIGIN.DECLARED,
                                                              FieldAccessorsAndMutators.BOTH,
                                                              DataType.TYPE_BOOLEAN);
    private final ModelField bookName = new ModelField("name",
                                                       String.class.getSimpleName(),
                                                       ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                       ModelField.FIELD_ORIGIN.DECLARED,
                                                       FieldAccessorsAndMutators.BOTH,
                                                       DataType.TYPE_STRING);
    private final ModelField bookNumberOfCopies = new ModelField("numberOfCopies",
                                                                 Integer.class.getSimpleName(),
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.DECLARED,
                                                                 FieldAccessorsAndMutators.BOTH,
                                                                 DataType.TYPE_NUMERIC_INTEGER);
    private final ModelField bookTestField = new ModelField("testField",
                                                            Integer.class.getSimpleName(),
                                                            ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                            ModelField.FIELD_ORIGIN.DECLARED,
                                                            FieldAccessorsAndMutators.BOTH,
                                                            DataType.TYPE_NUMERIC_INTEGER);
    private final ModelField bookThis = new ModelField("this",
                                                       "Book",
                                                       ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                       ModelField.FIELD_ORIGIN.SELF,
                                                       FieldAccessorsAndMutators.ACCESSOR,
                                                       DataType.TYPE_THIS);
    private final ModelField[] authorModelFields = {
            authorBooks,
            authorCurrentlyPrinted,
            authorFirstBook,
            authorIsAlive,
            authorName,
            authorThis
    };
    private final ModelField[] bookModelFields = {
            bookAuthor,
            bookIsAvailable,
            bookName,
            bookNumberOfCopies,
            bookTestField,
            bookThis
    };

    @Override
    protected Map<String, ModelField[]> retrieveModelFieldsMap() {
        return Stream.of(
                new AbstractMap.SimpleImmutableEntry<>(AUTHOR, authorModelFields),
                new AbstractMap.SimpleImmutableEntry<>("Book", bookModelFields))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected Map<String, Boolean> retrieveCollectionTypes() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("books", true),
                new AbstractMap.SimpleEntry<>("currentlyPrinted", false),
                new AbstractMap.SimpleEntry<>("firstBook", false),
                new AbstractMap.SimpleEntry<>("isAlive", false)).
                collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    @Override
    protected Map<String, String> retrieveFqcnNamesMap() {
        return Stream.of(factTypes, fqcnNames).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    @Override
    protected String[] retrieveFqcnNames() {
        return new String[] {"com.Author", "com.Book", String.class.getCanonicalName(), Integer.class.getCanonicalName()};

    }

    @Override
    protected String[] retrieveFactTypes() {
        return new String[] {AUTHOR, "Book", String.class.getSimpleName(), Integer.class.getSimpleName()};
    }

    @Override
    protected List<String> retrievePackageNames() {
        return Arrays.asList("com", "com.example");

    }

    @Override
    protected Map<String, String> retrieveParametricFieldMap() {
        return Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("Author.books", "Book"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
