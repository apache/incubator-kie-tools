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

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonToken;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.parser.Parser;

public abstract class CompositeParser<T> extends AbstractParser {

    protected final Queue<Parser> parsers = new ArrayDeque<>();
    protected final String name;
    protected Parser current = null;

    public CompositeParser(final String name) {
        this.name = name;
    }

    public CompositeParser(final String name,
                           final Queue<Parser> _parsers) {
        this.name = name;
        this.parsers.addAll(_parsers);
    }

    protected abstract JsonToken getStartToken();

    protected abstract JsonToken getEndToken();

    public T addParser(Parser _parser) {
        this.parsers.add(_parser);
        return (T) this;
    }

    @Override
    protected JsonToken next() throws IOException, JsonParseException {
        if (this.tokenCount == 0 && hasName()) {
            return JsonToken.FIELD_NAME;
        } else {
            if ((tokenCount == 1 && hasName()) || (tokenCount == 0 && !hasName())) {
                if (!parsers.isEmpty()) {
                    setCurrentParser(this.parsers.remove());
                }
                return getStartToken();
            }
            if (null != current && !this.current.isConsumed()) {
                return this.current.nextToken();
            } else if (null != current && this.current.isConsumed() && !this.parsers.isEmpty()) {
                setCurrentParser(this.parsers.remove());
                return this.current.nextToken();
            } else {
                // Finished.
                setCurrentParser(null);
                return getEndToken();
            }
        }
        // throw new RuntimeException("Invalid token count for this object parser.");
    }

    protected void setCurrentParser(final Parser p) {
        this.current = p;
    }

    @Override
    public String getCurrentName() throws IOException, JsonParseException {
        if (this.tokenCount == 1 && hasName()) {
            return name;
        } else {
            return this.current.getCurrentName();
        }
    }

    @Override
    public String getText() throws IOException, JsonParseException {
        return this.current.getText();
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return this.current.getIntValue();
    }

    @Override
    public boolean isConsumed() {
        return this.tokenCount > 2 && this.current == null;
    }

    private boolean hasName() {
        return name != null && name.trim().length() > 0;
    }
}
