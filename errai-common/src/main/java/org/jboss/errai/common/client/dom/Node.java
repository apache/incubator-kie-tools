/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Node">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public interface Node extends EventTarget {
  @JsOverlay static final short ELEMENT_NODE = 1;
  @JsOverlay static final short ATTRIBUTE_NODE = 2;
  @JsOverlay static final short TEXT_NODE = 3;
  @JsOverlay static final short CDATA_SECTION_NODE = 4;
  @JsOverlay static final short ENTITY_REFERENCE_NODE = 5;
  @JsOverlay static final short ENTITY_NODE = 6;
  @JsOverlay static final short PROCESSING_INSTRUCTION_NODE = 7;
  @JsOverlay static final short COMMENT_NODE = 8;
  @JsOverlay static final short DOCUMENT_NODE = 9;
  @JsOverlay static final short DOCUMENT_TYPE_NODE = 10;
  @JsOverlay static final short DOCUMENT_FRAGMENT_NODE = 11;
  @JsOverlay static final short NOTATION_NODE = 12;

  @JsProperty String getNodeName();
  @JsProperty String getNodeValue();
  @JsProperty short getNodeType();
  @JsProperty Node getParentNode();
  @JsProperty Element getParentElement();
  @JsProperty NodeList getChildNodes();
  @JsProperty Node getFirstChild();
  @JsProperty Node getLastChild();
  @JsProperty Node getPreviousSibling();
  @JsProperty Node getNextSibling();
  @JsProperty NamedNodeMap getAttributes();
  @JsProperty Document getOwnerDocument();

  @JsProperty String getTextContent();
  @JsProperty void setTextContent(String textContent);

  Node insertBefore(Node newChild, Node refChild);
  Node replaceChild(Node newChild, Node oldChild);
  Node removeChild(Node oldChild);
  Node appendChild(Node newChild);
  boolean hasChildNodes();
  Node cloneNode(boolean deep);
}
