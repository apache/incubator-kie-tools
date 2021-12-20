/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.shared.wrapper;

import org.w3c.dom.*;

/**
 * Read only wrapper to transform a gwt dom node to w3c one.
 * @author edewit@redhat.com
 */
public class NodeWrapper implements Node {
  private final com.google.gwt.dom.client.Node node;

  public NodeWrapper(com.google.gwt.dom.client.Node node) {
    this.node = node;
  }

  public String getNodeName() {
    return node.getNodeName();
  }

  @Override
  public String getNodeValue() throws DOMException {
    return node.getNodeValue();
  }

  @Override
  public void setNodeValue(String nodeValue) throws DOMException {
    node.setNodeValue(nodeValue);
  }

  @Override
  public short getNodeType() {
    return node.getNodeType();
  }

  @Override
  public Node getParentNode() {
    return new NodeWrapper(node.getParentNode());
  }

  @Override
  public NodeList getChildNodes() {
    return new NodeListWrapper(node.getChildNodes());
  }

  @Override
  public Node getFirstChild() {
    return new NodeWrapper(node.getFirstChild());
  }

  @Override
  public Node getLastChild() {
    return new NodeWrapper(node.getLastChild());
  }

  @Override
  public Node getPreviousSibling() {
    return new NodeWrapper(node.getPreviousSibling());
  }

  @Override
  public Node getNextSibling() {
    return new NodeWrapper(node.getNextSibling());
  }

  @Override
  public NamedNodeMap getAttributes() {
    return null;
  }

  @Override
  public Document getOwnerDocument() {
    return null;
  }

  @Override
  public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    return null;
  }

  @Override
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    return null;
  }

  @Override
  public Node removeChild(Node oldChild) throws DOMException {
    return null;
  }

  @Override
  public Node appendChild(Node newChild) throws DOMException {
    return null;
  }

  @Override
  public boolean hasChildNodes() {
    return node.hasChildNodes();
  }

  @Override
  public Node cloneNode(boolean deep) {
    return null;
  }

  @Override
  public void normalize() {
  }

  @Override
  public boolean isSupported(String feature, String version) {
    return false;
  }

  @Override
  public String getNamespaceURI() {
    return null;
  }

  @Override
  public String getPrefix() {
    return null;
  }

  @Override
  public void setPrefix(String prefix) throws DOMException {
  }

  @Override
  public String getLocalName() {
    return null;
  }

  @Override
  public boolean hasAttributes() {
    return true; //maybe
  }

  @Override
  public String getBaseURI() {
    return null;
  }

  @Override
  public short compareDocumentPosition(Node other) throws DOMException {
    return 0;
  }

  @Override
  public String getTextContent() throws DOMException {
    return null;
  }

  @Override
  public void setTextContent(String textContent) throws DOMException {
  }

  @Override
  public boolean isSameNode(Node other) {
    return false;
  }

  @Override
  public String lookupPrefix(String namespaceURI) {
    return null;
  }

  @Override
  public boolean isDefaultNamespace(String namespaceURI) {
    return false;
  }

  @Override
  public String lookupNamespaceURI(String prefix) {
    return null;
  }

  @Override
  public boolean isEqualNode(Node arg) {
    return false;
  }

  @Override
  public Object getFeature(String feature, String version) {
    return null;
  }

  @Override
  public Object setUserData(String key, Object data, UserDataHandler handler) {
    return null;
  }

  @Override
  public Object getUserData(String key) {
    return null;
  }
}
