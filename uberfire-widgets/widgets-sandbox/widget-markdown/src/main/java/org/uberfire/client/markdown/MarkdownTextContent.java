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

package org.uberfire.client.markdown;

import static org.uberfire.commons.util.Preconditions.*;

public class MarkdownTextContent {

    private String content;

    public MarkdownTextContent() {
    }

    public MarkdownTextContent(final String content) {
        this.content = checkNotNull("content", content);
    }

    public String getContent() {
        return content;
    }

}
