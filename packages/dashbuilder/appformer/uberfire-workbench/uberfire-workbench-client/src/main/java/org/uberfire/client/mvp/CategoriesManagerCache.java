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
 *
 */

package org.uberfire.client.mvp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Undefined;

@ApplicationScoped
public class CategoriesManagerCache {

    private Set<Category> categories = new HashSet<>();

    private Undefined undefinedCategory;

    @Inject
    public CategoriesManagerCache(Undefined undefinedCategory) {
        this.undefinedCategory = undefinedCategory;
    }

    public Set<Category> getCategories() {
        return new HashSet<>(categories);
    }

    public void add(Category category) {
        this.categories.add(category);
    }

    public void addAll(Collection<Category> category) {
        this.categories.addAll(category);
    }

    public Category getCategory(String filterType) {
        if (filterType == null) {
            return undefinedCategory;
        } else {
            return this.getCategories()
                    .stream()
                    .filter(category -> category.getName().equals(filterType.toUpperCase()))
                    .findFirst()
                    .orElse(undefinedCategory);
        }
    }
}
