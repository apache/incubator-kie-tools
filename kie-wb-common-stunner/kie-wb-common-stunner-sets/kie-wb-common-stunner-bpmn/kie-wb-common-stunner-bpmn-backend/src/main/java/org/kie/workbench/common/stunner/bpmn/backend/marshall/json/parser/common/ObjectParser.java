/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.common;

import org.codehaus.jackson.JsonToken;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.Parser;

import java.util.Queue;

public class ObjectParser extends CompositeParser<ObjectParser> {

    public ObjectParser( String name ) {
        super( name );
    }

    public ObjectParser( String name, Queue<Parser> _parsers ) {
        super( name, _parsers );
    }

    @Override
    protected JsonToken getStartToken() {
        return JsonToken.START_OBJECT;
    }

    @Override
    protected JsonToken getEndToken() {
        return JsonToken.END_OBJECT;
    }

}
