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

import java.io.InputStream;

import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

public class JavaFileHandlerImpl {

    private StringBuilder source = null;

    private JavaParser parser;

    private FileDescr fileDescr;

    public JavaFileHandlerImpl( InputStream inputStream ) throws Exception {
        //TODO implement better exceptions handling
        source = new StringBuilder( ParserUtil.readString( inputStream ) );
        parseSource( );
    }

    public FileDescr getFileDescr( ) {
        return fileDescr;
    }

    public String getOriginalContent( ) {
        return source.toString( );
    }

    private void parseSource( ) throws Exception {
        parser = JavaParserFactory.newParser( source.toString( ) );
        parser.compilationUnit( );
        fileDescr = parser.getFileDescr( );
        ParserUtil.setSourceBufferTMP( fileDescr, parser.getSourceBuffer( ) );
        ParserUtil.populateUnManagedElements( fileDescr );
    }

    public String buildResult( ) {
        return ParserUtil.printTree( fileDescr );
    }

    /*
    public void populateUnManagedElements(ElementDescriptor element) {
        populateUnManagedElements(element.getStart(), element);
    }


    public void populateUnManagedElements(int startIndex, ElementDescriptor element) {

        String text;
        TextTokenElementDescr unmanagedToken;

        if (element.getElements().size() > 0) {
            List<ElementDescriptor> originalElements = new ArrayList<ElementDescriptor>();
            originalElements.addAll(element.getElements());

            for (ElementDescriptor child : originalElements) {
                if (startIndex < child.getStart()) {
                    unmanagedToken = new TextTokenElementDescr();
                    unmanagedToken.setStart(startIndex);
                    unmanagedToken.setStop(child.getStart()-1);
                    unmanagedToken.setSourceBuffer(child.getSourceBuffer());

                    text = unmanagedToken.getSourceBuffer().substring(unmanagedToken.getStart(), unmanagedToken.getStop() +1);
                    unmanagedToken.setText(text);

                    element.getElements().addMemberBefore(child, unmanagedToken);
                }
                startIndex = child.getStop() + 1;

                populateUnManagedElements(child);
            }

            if (startIndex < element.getStop()) {
                unmanagedToken = new TextTokenElementDescr();
                unmanagedToken.setStart(startIndex);
                unmanagedToken.setStop(element.getStop());
                unmanagedToken.setSourceBuffer(element.getSourceBuffer());

                text = unmanagedToken.getSourceBuffer().substring(unmanagedToken.getStart(), unmanagedToken.getStop() +1);
                unmanagedToken.setText(text);
                element.getElements().add(unmanagedToken);
            }
        }
    }

*/

    /*
    public static String printTree(ElementDescriptor element) {
        StringBuilder result = new StringBuilder();
        if (element.getElements().size() == 0) {
            result.append(element.getSourceBuffer().substring(element.getStart(), element.getStop() +1));
        } else {
            for (ElementDescriptor child : element.getElements()) {
                result.append(printTree(child));
            }
        }
        return result.toString();
    }

    //temporal to not touch the parser
    public void setSourceBufferTMP(ElementDescriptor element, StringBuilder source) {
        element.setSourceBuffer(source);
        for (ElementDescriptor child : element.getElements()) {
            setSourceBufferTMP(child, source);
        }
    }
    */

}