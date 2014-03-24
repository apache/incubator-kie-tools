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

package org.kie.workbench.common.services.datamodeller.parser;

import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;

public interface JavaFileHandler {

    FileDescr getFileDescr( );

    String getOriginalContent( );

    String buildResult( );

    void createImport( String source );

    void removePackageImport( String packageName );

    void removeClassImport( String className );

    void setPackageName( String name );

    void setClassName( String name );

    void setSuperClassName( String className );

    void createClassAnnotation( String source );

    void deleteClassAnnotation( String annotationClassName );

    void createField( String source );

    void renameField( String name, String newName );

    void deleteField( String name );

    void createFieldAnnotation( String fieldName, String source );

    void deleteFieldAnnotation( String fieldName, String annotationClassName );

    void createMethod( String source );

    void deleteMethod( String name, String[] paramTypes );
}
