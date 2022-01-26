/**
 * Copyright (c) 2010-2011 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.ecore.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.Callback;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * TODO
 */
public class URIServiceCallback extends URIHandlerImpl
{
  protected URIServiceAsync uriService;
  
  public URIServiceCallback(URIServiceAsync uriService)
  {
    this.uriService = uriService;
  }

  @Override
  public void createInputStream(final URI uri, Map<?, ?> options, final Callback<Map<?, ?>> callback)
  {
    HashMap<Object, Object> massagedOptions = new HashMap<Object, Object>(options);
    massagedOptions.remove(URIConverter.OPTION_URI_CONVERTER);
    uriService.fetch
      (uri.toString(), 
       massagedOptions, 
       new AsyncCallback<Map<?, ?>>()
       {
         public void onFailure(Throwable caught)
         {
           callback.onFailure(caught);
         }
 
         public void onSuccess(Map<?, ?> result)
         {
           @SuppressWarnings("unchecked")
           Map<String, Object> response = (Map<String, Object>)result.get(URIConverter.OPTION_RESPONSE);
   	       Object responseResult = response.get(URIConverter.RESPONSE_RESULT);
   	       if (responseResult == null)
           {
             callback.onFailure(new IOException("Stream for '" + uri + "' not found"));
           }
   	       else if (responseResult instanceof byte[])
           {
             byte[] bytes = (byte[])responseResult;
             response.put(URIConverter.RESPONSE_RESULT, new ByteArrayInputStream(bytes));
             callback.onSuccess(result);
           }
           else
           {
       	     callback.onFailure((IOException)responseResult);
           }
         }
       });
  }

  @Override
  public void store(URI uri, byte[] bytes, Map<?, ?> options, final Callback<Map<?, ?>> callback)
  {
    HashMap<Object, Object> massagedOptions = new HashMap<Object, Object>(options);
    massagedOptions.remove(URIConverter.OPTION_URI_CONVERTER);
    uriService.store
      (uri.toString(), 
       bytes,
       massagedOptions, 
       new AsyncCallback<Map<?, ?>>()
       {
         public void onFailure(Throwable caught)
         {
           callback.onFailure(caught);
         }
 
         public void onSuccess(Map<?, ?> result)
         {
           @SuppressWarnings("unchecked")
           Map<String, Object> response = (Map<String, Object>)result.get(URIConverter.OPTION_RESPONSE);
           Object responseResult = response.get(URIConverter.RESPONSE_RESULT);
           if (responseResult instanceof Throwable)
           {
             callback.onFailure((Throwable)responseResult);
           }
           else
           {
             callback.onSuccess(result);
           }
         }
       });
  }
  
  @Override
  public void delete(URI uri, Map<?, ?> options, final Callback<Map<?, ?>> callback)
  {
    HashMap<Object, Object> massagedOptions = new HashMap<Object, Object>(options);
    massagedOptions.remove(URIConverter.OPTION_URI_CONVERTER);
    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>)massagedOptions.get(URIConverter.OPTION_RESPONSE);
    if (response == null)
    {
      massagedOptions.put(URIConverter.OPTION_RESPONSE, new HashMap<Object, Object>());
    }
    uriService.delete
      (uri.toString(), 
       massagedOptions, 
       new AsyncCallback<Map<?, ?>>()
       {
         public void onFailure(Throwable caught)
         {
           callback.onFailure(caught);
         }
 
         public void onSuccess(Map<?, ?> result)
         {
           @SuppressWarnings("unchecked")
           Map<String, Object> response = (Map<String, Object>)result.get(URIConverter.OPTION_RESPONSE);
           Object responseResult = response.get(URIConverter.RESPONSE_RESULT);
           if (responseResult instanceof Throwable)
           {
             callback.onFailure((Throwable)responseResult);
           }
           else
           {
             callback.onSuccess(result);
           }
         }
       });
  }

  @Override
  public void exists(URI uri, Map<?, ?> options, final Callback<Boolean> callback)
  {
    HashMap<Object, Object> massagedOptions = new HashMap<Object, Object>(options);
    massagedOptions.remove(URIConverter.OPTION_URI_CONVERTER);
    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>)massagedOptions.get(URIConverter.OPTION_RESPONSE);
    if (response == null)
    {
      massagedOptions.put(URIConverter.OPTION_RESPONSE, new HashMap<Object, Object>());
    }
    uriService.exists
      (uri.toString(), 
       massagedOptions, 
       new AsyncCallback<Boolean>()
       {
         public void onFailure(Throwable caught)
         {
           callback.onFailure(caught);
         }
 
         public void onSuccess(Boolean result)
         {
           callback.onSuccess(result);
         }
       });
  }
}