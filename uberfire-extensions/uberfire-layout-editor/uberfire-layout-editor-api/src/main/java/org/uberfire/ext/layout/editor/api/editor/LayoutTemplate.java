/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.api.editor;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Portable
public final class LayoutTemplate {

    private int version = 1;

    private String name;

    private Map<String, String> layoutProperties = new HashMap<String, String>();

    private List<LayoutRow> rows = new ArrayList<LayoutRow>();

    public LayoutTemplate(String name) {
        this.name = name;
    }

    public LayoutTemplate(String name,
                          Map<String, String> layoutProperties) {
        this.name = name;
        this.layoutProperties = layoutProperties;
    }

    public LayoutTemplate() {
    }

    public int getVersion() {
        return version;
    }

    public void addRow(LayoutRow layoutRow) {
        rows.add(layoutRow);
    }

    public List<LayoutRow> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getLayoutProperties() {
        return layoutProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LayoutTemplate)) {
            return false;
        }

        LayoutTemplate that = (LayoutTemplate) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (layoutProperties != null ? !layoutProperties.equals(that.layoutProperties) : that.layoutProperties != null) {
            return false;
        }
        return !(rows != null ? !rows.equals(that.rows) : that.rows != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (layoutProperties != null ? layoutProperties.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }

    public static LayoutTemplate defaultLayout(String layoutName) {

        final LayoutTemplate layoutTemplate = new LayoutTemplate(layoutName);
        final LayoutRow layoutRow = new LayoutRow(new ArrayList<String>() {{
            add("12");
        }});
        layoutRow.add(new LayoutColumn("12"));
        layoutTemplate.addRow(layoutRow);

        return layoutTemplate;
    }

    @Override
    public String toString() {
        return "LayoutTemplate{" +
                "version=" + version +
                ", name='" + name + '\'' +
                ", layoutProperties=" + layoutProperties +
                ", rows=" + rows +
                '}';
    }

    public void addLayoutProperty(String key, String value) {
        layoutProperties.put(key, value);
    }
}
