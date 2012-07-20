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
package org.jboss.bpm.console.client.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@XmlRootElement(name = "tokenReference")
public class TokenReference
{
   private String id;
   private String name;

   private String currentNodeName;

   private List<TokenReference> children = new ArrayList<TokenReference>();
   private List<String> availableSignals = new ArrayList<String>();

   private boolean canBeSignaled = false;

   public TokenReference()
   {
   }

   public TokenReference(String id, String name, String nodeName)
   {
      this.id = id;
      this.name = name!=null ? name : "";
      this.currentNodeName = nodeName;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setCurrentNodeName(String currentNodeName)
   {
      this.currentNodeName = currentNodeName;
   }

   public void setChildren(List<TokenReference> children)
   {
      this.children = children;
   }

   public void setAvailableSignals(List<String> availableSignals)
   {
      this.availableSignals = availableSignals;
   }

   public String getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public List<TokenReference> getChildren()
   {
      return children;
   }

   public List<String> getAvailableSignals()
   {
      return availableSignals;
   }

   public String getCurrentNodeName()
   {
      return currentNodeName;
   }

   public boolean canBeSignaled()
   {
      return canBeSignaled;
   }

   public void setCanBeSignaled(boolean canBeSignaled)
   {
      this.canBeSignaled = canBeSignaled;
   }

}
