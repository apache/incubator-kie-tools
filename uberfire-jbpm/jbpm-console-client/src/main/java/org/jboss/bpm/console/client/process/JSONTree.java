/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.client.process;

import org.drools.guvnor.client.util.ConsoleLog;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;


public class JSONTree extends ScrollPanel
{
  String json = null;

  public JSONTree(String json)
  {
    this.json = json;

    Tree tree = new Tree();
    TreeItem root = tree.addItem("message");

    parseObject(root, "root", JSONParser.parse(json));
    
    this.add(tree);
  }

  private void parseValue(TreeItem root, String key, JSONValue jsonValue)
  {
    if(jsonValue.isBoolean()!=null)
    {
      TreeItem treeItem = root.addItem(key);
      treeItem.addItem(jsonValue.isBoolean().toString());
    }
    else if(jsonValue.isNumber()!=null)
    {
      TreeItem fastTreeItem = root.addItem(key);
      fastTreeItem.addItem(jsonValue.isNumber().toString());
    }
    else if(jsonValue.isString()!=null)
    {
      TreeItem treeItem = root.addItem(key);
      treeItem.addItem(jsonValue.isString().toString());
    }
    else
    {
      ConsoleLog.warn("Unexpected JSON value: " + jsonValue);
    }

  }

  private void parseArray(TreeItem root, String key, JSONValue jsonValue)
  {
    
  }

  private void parseObject(TreeItem root, String key, JSONValue topLevel)
  {
    JSONObject rootJSO = topLevel.isObject();
    if(null==rootJSO)
      throw new IllegalArgumentException("Not a JSON object: "+topLevel);
        
    for(String innerKey : rootJSO.keySet())
    {
      JSONValue jsonValue = rootJSO.get(innerKey);
      if(jsonValue.isObject()!=null)
      {
        parseObject(root, innerKey, jsonValue);
      }
      else if (jsonValue.isArray()!=null)
      {
        parseArray(root, innerKey, jsonValue);
      }
      else
      {
        parseValue(root, innerKey, jsonValue);
      }
    }
  }

}
