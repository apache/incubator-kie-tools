/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.jcr2vfsmigration.xml;

import java.util.Stack;

public class XMLStringBuilder {

    private static final String LT = "<";
    private static final String LT_SLASH = "</";
    private static final String GT = ">";

    private StringBuilder sb;
    private String newLine;
    private int indent = 0;
//    private Stack<TagInfo> tagStack;
//    private TagInfo currentTag;

    public XMLStringBuilder() {
        this.sb = new StringBuilder();
//        this.tagStack = new Stack();
        this.newLine = System.getProperty( "line.separator" );
    }

    public XMLStringBuilder startTag( String tagName ) {
        if ( tagName == null ) throw new NullPointerException( "No tag name specified" );

//        currentTag = new TagInfo( tagName, indent );
//        tagStack.push( currentTag );

        printIndent();
        sb.append( LT ).append( tagName ).append( GT );

        return this;
    }

    public XMLStringBuilder printTagContent( String tagContent ) {
        sb.append( tagContent );
        return this;
    }

    public XMLStringBuilder endTag( String tagName ) {
        if ( tagName == null ) throw new NullPointerException( "No tag name specified" );
//        if( !tagName.equalsIgnoreCase( currentTag.tagName ) ) throw new RuntimeException( "Wrong tag end, current tag is " + currentTag.tagName );

//        tagStack.pop();
//        currentTag = tagStack.size() > 0 ? tagStack.peek() : null;

        printIndent();
        sb.append( LT_SLASH ).append( tagName ).append( GT );

        return this;
    }

    public XMLStringBuilder newLine() {
        sb.append( newLine );
        return this;
    }

    public XMLStringBuilder indent() {
        indent++;
        return this;
    }

    public XMLStringBuilder unIndent() {
        indent--;
        return this;
    }

    public String toString() {
        return sb.toString();
    }

    private void printIndent() {
        for ( int i = 0; i < indent; i++ ) {
            sb.append( "  " );
        }
    }

//    private class TagInfo {
//        private String tagName;
//        private boolean indented = false;
//        private TagInfo( String tagName, boolean indented ) {
//            this.tagName = tagName;
//            this.indented = indented;
//        }
//    }
}
