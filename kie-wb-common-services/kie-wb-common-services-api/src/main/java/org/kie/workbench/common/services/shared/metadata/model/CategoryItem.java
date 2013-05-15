/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.shared.metadata.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.kie.commons.validation.PortablePreconditions.checkNotEmpty;

@Portable
public class CategoryItem {

    private CategoryItem parent;
    private List<CategoryItem> children = new ArrayList<CategoryItem>();
    private String name;
    private String description;

    public CategoryItem() {
    }

    public CategoryItem( final String name,
                         final String description,
                         final CategoryItem parent ) {
        this.name = checkNotEmpty( "name", name );
        this.description = description;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CategoryItem getParent() {
        return parent;
    }

    public List<CategoryItem> getChildren() {
        return unmodifiableList( children );
    }

    public void removeChildren( final String name ) {
        for ( int i = 0; i < children.size(); i++ ) {
            final CategoryItem child = children.get( i );
            if ( child.getName().equals( name ) ) {
                children.remove( i );
                break;
            }
        }
    }

    public boolean hasChild() {
        return children.size() > 0;
    }

    public CategoryItem addChildren( final String name,
                                     final String description ) {
        final CategoryItem newCategory = new CategoryItem( name, description, this );
        children.add( newCategory );
        return newCategory;
    }

    public boolean contains( final String name ) {
        for ( final CategoryItem child : children ) {
            if ( child.getName().equals( name ) ) {
                return true;
            }
        }
        return false;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getFullPath() {
        if ( getName() == null ) {
            return "";
        }

        if ( getParent() == null ) {
            return getName();
        }
        final String parent = getParent().getFullPath();
        if ( parent.equals( "" ) ) {
            return getName();
        }

        return parent + "/" + getName();
    }
}
