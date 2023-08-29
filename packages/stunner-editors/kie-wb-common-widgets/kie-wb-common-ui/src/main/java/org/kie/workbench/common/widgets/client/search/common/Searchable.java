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


package org.kie.workbench.common.widgets.client.search.common;

import org.uberfire.mvp.Command;

/**
 * {@link Searchable} represents a searchable element.
 */
public interface Searchable {

    /**
     * Returns true when <code>text</code> satisfies the match logic.
     * @param text represent the text used by the search mechanism.
     * @return true if the text satisfies the match logic.
     */
    boolean matches(final String text);

    /**
     * Returns the command that is triggered when the element is found.
     * @return the {@link Command} with the <code>onFound</code> logic.
     */
    Command onFound();
}
