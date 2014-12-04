/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.jcrExport;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.CategoriesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;
import org.drools.workbench.jcr2vfsmigration.xml.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CategoryExporter {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryExporter.class);

    @Inject
    protected RepositoryCategoryService jcrRepositoryCategoryService;

    @Inject
    FileManager fileManager;

    CategoriesXmlFormat categoriesXmlFormat = new CategoriesXmlFormat();

    public void exportAll() {
        System.out.println( "  Category export started" );

        StringBuilder xml = new StringBuilder();
        categoriesXmlFormat.format( xml, export( "/" ) );

        PrintWriter pw = fileManager.createCategoryExportFileWriter();
        pw.print( xml.toString() );
        pw.close();

        System.out.println( "  Category export ended" );
    }

    private Categories export( String category ) {
        String[] categories = jcrRepositoryCategoryService.loadChildCategories( category );
        Collection<Category> cCategories = new ArrayList<Category>( categories.length );

        for(String c : categories) {
            Category _category = new Category( c, export( getCategoryPath( c, category ) ) );
            cCategories.add( _category );
        }
        return new Categories( cCategories );
    }

    private String getCategoryPath(String categoryName, String parentPath) {
        if ( "/".equals( parentPath ) ) {
            return parentPath + categoryName;
        } else {
            return parentPath + "/" + categoryName;
        }
    }
}
