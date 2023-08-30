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


package org.uberfire.client.mvp;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.workbench.model.NamedPosition;

public interface TemplatedActivity {

    /**
     * Returns the HTMLElement that contains the child WorkbenchPanelView at the given position.
     * @return the HTMLElement that contains the child at the given position, or null if the given position does not exist
     * within this activity's view.
     */
    HTMLElement resolvePosition(NamedPosition p);

    /**
     * Returns the HTMLElement that is the root panel of this activity.
     */
    HTMLElement getRootElement();
}
