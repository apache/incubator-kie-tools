/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser.descr;

import java.util.ArrayList;
import java.util.List;

public class FileDescr extends ElementDescriptor {

    public FileDescr( ) {
        super( ElementType.FILE );
    }

    public PackageDescr getPackageDescr( ) {
        return ( PackageDescr ) getElements( ).getFirst( ElementType.PACKAGE );
    }

    public void setPackageDescr( PackageDescr packageDescr ) {
        getElements( ).removeFirst( ElementType.PACKAGE );
        getElements( ).add( packageDescr );
    }

    public void addImport( ImportDescr importDescr ) {
        getElements( ).add( importDescr );
    }

    public List<ImportDescr> getImports( ) {
        List<ImportDescr> imports = new ArrayList<ImportDescr>( );
        for ( ElementDescriptor member : getElements( ).getElementsByType( ElementType.IMPORT ) ) {
            imports.add( ( ImportDescr ) member );
        }
        return imports;
    }

    public ClassDescr getClassDescr( ) {
        return ( ClassDescr ) getElements( ).getFirst( ElementType.CLASS );
    }

    public void setClassDescr( ClassDescr classDescr ) {
        getElements( ).removeFirst( ElementType.CLASS );
        getElements( ).add( classDescr );
    }

}
