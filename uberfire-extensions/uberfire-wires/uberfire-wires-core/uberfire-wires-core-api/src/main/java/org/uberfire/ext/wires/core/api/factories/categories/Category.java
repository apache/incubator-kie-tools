/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.api.factories.categories;

public class Category {

    private String category;

    public Category( final String category ) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Category ) ) {
            return false;
        }

        Category that = (Category) o;

        if ( !category.equals( that.category ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return category.hashCode();
    }

    @Override
    public String toString() {
        return "Category{" +
                "category='" + category + '\'' +
                '}';
    }

}
