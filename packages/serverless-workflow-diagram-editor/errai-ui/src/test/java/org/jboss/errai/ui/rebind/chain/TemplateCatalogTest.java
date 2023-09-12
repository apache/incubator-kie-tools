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


package org.jboss.errai.ui.rebind.chain;

import java.net.URL;

import org.jboss.errai.ui.shared.chain.Chain;
import org.jboss.errai.ui.shared.chain.Command;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author edewit@redhat.com
 */
public class TemplateCatalogTest {

  @Test
  public void shouldHaveAutomatedSetup() {
    // given
    final DummyCommand command = new DummyCommand();
    final TemplateCatalog catalog = TemplateCatalog.createTemplateCatalog(command);

    // when
    final URL template = getClass().getResource("/simple.html");
    final Document document = catalog.visitTemplate(template);

    // then
    final Chain chain = catalog.getChain();
    assertEquals(1, chain.getCommands().size());
    assertEquals(5, command.getCounter());
    assertNotNull(document);
  }

  public static class DummyCommand implements Command {
    private int counter;

    @Override
    public void execute(Element element) {
      counter++;
    }

    public int getCounter() {
      return counter;
    }
  }
}
