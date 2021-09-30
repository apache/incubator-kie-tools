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

package org.uberfire.ext.layout.editor.impl.old.perspective.editor;

@Deprecated
public class HTMLEditor {

    private String htmlCode;

    public HTMLEditor() {
    }

    public HTMLEditor(String htmlCode) {

        this.htmlCode = htmlCode;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HTMLEditor)) {
            return false;
        }

        HTMLEditor that = (HTMLEditor) o;

        if (htmlCode != null ? !htmlCode.equals(that.htmlCode) : that.htmlCode != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return htmlCode != null ? htmlCode.hashCode() : 0;
    }
}
