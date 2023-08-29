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


package org.jboss.errai.ui.test.runtime.client.res;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;

@Dependent
public class CustomTemplateProvider implements TemplateProvider {

  @Override
  public void provideTemplate(String location, TemplateRenderingCallback renderingCallback) {
    String template = 
            "<div id=\"root\">"
            + "<h1>This will be rendered</h1>"
            + "<div id=\"c1\" class=\"c1\" align=\"left\">This will not be rendered</div>"
            + "<div id=\"c2\">This will be rendered inside button</div>"
            + "<div>This will be rendered</div>"
            + "<input id=\"c3\" name=\"address\" /> "
            + "<a id=\"c4\" href=\"blah\"><span>LinkHTML</span></a> "
            + "<a href=\"blah2\" id=\"c5\"><img id=\"c6\" src=\"/some/img.png\"/></a>"
            + "</div>";
    
    renderingCallback.renderTemplate(template);
  }

}
