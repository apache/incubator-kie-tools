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
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@XmlRootElement(name = "diagramInfo")
public class DiagramInfo
{
   private int width = -1;
   private int height = -1;
   private List<DiagramNodeInfo> nodeList = new ArrayList<DiagramNodeInfo>();

   public DiagramInfo()
   {
   }

   public DiagramInfo(final int height, final int width, final List<DiagramNodeInfo> l) {
      this.height = height;
      this.width = width;
      final List<DiagramNodeInfo> list = new ArrayList<DiagramNodeInfo>();
      for (DiagramNodeInfo nodeInfo : l) {
         list.add(nodeInfo);
      }
      nodeList = Collections.unmodifiableList(list);
   }

   public int getWidth()
   {
      return width;
   }

   public void setWidth(int width)
   {
      this.width = width;
   }

   public int getHeight()
   {
      return height;
   }

   public void setHeight(int height)
   {
      this.height = height;
   }

   public List<DiagramNodeInfo> getNodeList()
   {
      return nodeList;
   }

   public void setNodeList(List<DiagramNodeInfo> nodeList)
   {
      this.nodeList = nodeList;
   }

}
