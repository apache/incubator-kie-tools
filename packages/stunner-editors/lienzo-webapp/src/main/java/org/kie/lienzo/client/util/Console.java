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

package org.kie.lienzo.client.util;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

import static elemental2.dom.DomGlobal.document;

public class Console {

    private String lastLog;
    private int lastLogCount;
    private HTMLDivElement lastElement;
    private elemental2.dom.Text lastText;

    public Console() {
    }

    public void log(String log) {
        HTMLDivElement e1;
        elemental2.dom.Text e1Text;
        Element links = document.getElementById("console");

        if (lastLog != null && lastLog.equals(log)) {
            e1 = lastElement;
            lastLogCount++;
            lastText.remove();

            e1Text = document.createTextNode(log + " (" + lastLogCount + ")");
            lastElement.appendChild(e1Text);
        } else {
            e1 = (HTMLDivElement) document.createElement("div");
            e1Text = document.createTextNode(log);
            e1.appendChild(e1Text);

            links.appendChild(e1);

            lastLogCount = 1;
            lastLog = log;
            lastText = e1Text;
        }

        lastText = e1Text;
        lastElement = e1;

        links.scrollTop = links.scrollHeight;
    }
}
