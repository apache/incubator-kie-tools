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


package org.jboss.errai.ui.test.elemental2.client.res;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated
public class ElementalComponent implements IsElement {

  public static class Observed {
    public final String type;
    public final String dataField;
    public Observed(final String type, final String dataField) {
      this.type = type;
      this.dataField = dataField;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dataField == null) ? 0 : dataField.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
    }
    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      final Observed other = (Observed) obj;
      if (dataField == null) {
        if (other.dataField != null)
          return false;
      }
      else if (!dataField.equals(other.dataField))
        return false;
      if (type == null) {
        if (other.type != null)
          return false;
      }
      else if (!type.equals(other.type))
        return false;
      return true;
    }
    @Override
    public String toString() {
      return "Observed [type=" + type + ", dataField=" + dataField + "]";
    }
  }

  public final List<Observed> observed = new ArrayList<>();

  @Inject
  @DataField
  public HTMLDivElement foo;

  @EventHandler("foo")
  public void fooMouseClickEvent(@ForEvent("click") final MouseEvent event) {
    observed.add(new Observed("click", "foo"));
  }

  @EventHandler("foo")
  public void fooBaseDblClickEvent(@ForEvent("dblclick") final Event event) {
    observed.add(new Observed("dblclick", "foo"));
  }

  @EventHandler("bar")
  public void barMouseClickEvent(@ForEvent("click") final MouseEvent event) {
    observed.add(new Observed("click", "bar"));
  }

}
