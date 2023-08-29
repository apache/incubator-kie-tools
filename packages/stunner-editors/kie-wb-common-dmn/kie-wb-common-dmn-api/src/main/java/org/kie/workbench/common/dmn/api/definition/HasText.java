/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.definition;

import org.kie.workbench.common.dmn.api.property.dmn.Text;

/**
 * Implementations have a {@link Text} property.
 */
public interface HasText extends HasValue<Text> {

    /**
     * Returns the {@link Text}
     * Convenience method replicating {@link HasValue#getValue()} to honour DMN model semantics.
     * @return
     */
    Text getText();

    /**
     * Sets the {@link Text}
     * Convenience method replicating {@link HasValue#setValue(Object)} to honour DMN model semantics.
     * @param text
     */
    void setText(final Text text);

    /**
     * Returns the {@link Text}
     * @return
     */
    @Override
    default Text getValue() {
        return getText();
    }

    /**
     * Sets the {@link Text}
     * @param text
     */
    @Override
    default void setValue(final Text text) {
        setText(text);
    }

    HasText NOP = new HasText() {

        private final Text TEXT = new Text();

        @Override
        public Text getText() {
            return TEXT;
        }

        @Override
        public void setText(final Text text) {
            //Do nothing
        }
    };
}
