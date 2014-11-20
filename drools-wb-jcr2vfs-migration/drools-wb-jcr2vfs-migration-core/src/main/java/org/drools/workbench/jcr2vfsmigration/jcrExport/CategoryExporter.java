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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CategoryExporter {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryExporter.class);

    public static final String CATEGORIES = "categories";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_NAME = "name";

    @Inject
    protected RepositoryCategoryService jcrRepositoryCategoryService;

    @Inject
    FileManager fileManager;

    public void exportAll() {
        System.out.println( "  Category export started" );

        PrintWriter pw = fileManager.createCategoryExportFileWriter();
        pw.println( "<" + CATEGORIES + ">" );

        export( "/", pw, 1 );

        pw.println( "</" + CATEGORIES + ">" );
        pw.close();

        System.out.println( "  Category export ended" );
    }

//    private void _export() {
//        StringWriter stringWriter = new StringWriter(  );
//        Categories categories = new Categories();
//
//        List<Category> lCat = new ArrayList<Category>( 5 );
//
//        Category category = new Category();
//        category.setName( "head1" );
//        lCat.add( category );
//        category = new Category();
//        category.setName( "head2" );
//        lCat.add( category );
//        category = new Category();
//        category.setName( "head3withsub" );
//        Categories subcat = new Categories();
//        List<Category> lCatsub = new ArrayList<Category>( 5 );
//        Category subcategory = new Category();
//        subcategory.setName( "sub1" );
//        lCatsub.add( subcategory );
//        subcat.setCategoryList( lCatsub );
//        category.setCategories( subcat );
//
//        lCat.add( category );
//        categories.setCategoryList( lCat );
//
//        xmlWriter.categoriesToXml( categories, stringWriter );
//        System.out.println(stringWriter.toString());
//    }

    private void export( String category, PrintWriter pw, int indent ) {
        String[] categories = jcrRepositoryCategoryService.loadChildCategories( category );

        for(String c : categories) {
            printIndent( pw, indent++ );
            pw.println( "<category " + CATEGORY_NAME + "=\"" + c + "\">" );
            export( getCategoryPath( c, category ), pw, indent );
            printIndent( pw, --indent );
            pw.println( "</category>" );
        }
    }

    private void printIndent(PrintWriter out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    private String getCategoryPath(String categoryName, String parentPath) {
        if ( "/".equals( parentPath ) ) {
            return parentPath + categoryName;
        } else {
            return parentPath + "/" + categoryName;
        }
    }
}
