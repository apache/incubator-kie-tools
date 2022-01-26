/**
 * Copyright (c) 2010 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource;

import java.util.Map;

import org.eclipse.emf.ecore.resource.URIService;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * TODO
 */
public interface URIServiceAsync
{
  void fetch(String uri, Map<?, ?> options, AsyncCallback<Map<?, ?>> callback);

  void store(String uri, byte[] bytes, Map<?, ?> options, AsyncCallback<Map<?, ?>> callback);

  void whiteList(URIService.WhiteList whiteList, AsyncCallback<URIService.WhiteList> callback);

  void delete(String uri, Map<?, ?> options, AsyncCallback<Map<?, ?>> callback);

  void exists(String uri, Map<?, ?> options, AsyncCallback<Boolean> callback);
}
