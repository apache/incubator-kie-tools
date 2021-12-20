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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public final class LayoutTemplate {

    private int version = 3;

    private String name;

    private Style style = Style.FLUID;

    private Map<String, String> layoutProperties = new HashMap<>();

    private List<LayoutRow> rows = new ArrayList<>();

    public LayoutTemplate() {

    }

    public LayoutTemplate(String name) {
        this.name = name;
    }

    public LayoutTemplate(String name, Style style) {
        this.name = name;
        this.style = style;
    }

    public LayoutTemplate(String layoutName,
                          Map<String, String> properties,
                          Style pageStyle) {
        this.name = layoutName;
        this.layoutProperties = properties;
        this.style = pageStyle;
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
        if (layoutProperties != null ? !layoutProperties
                .equals(that.layoutProperties) : that.layoutProperties != null) {
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

    @Override
    public String toString() {
        return "LayoutTemplate{" +
                "version=" + version +
                ", name='" + name + '\'' +
                ", layoutProperties=" + layoutProperties +
                ", rows=" + rows +
                '}';
    }

    public void addLayoutProperty(String key,
                                  String value) {
        layoutProperties.put(key,
                             value);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }
    
    public boolean contains(LayoutComponent component) {
        for (LayoutRow row : rows) {
            if (row.contains(component)) {
                return true;
            }
        }
        return false;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isPageStyle() {
        return Style.PAGE.equals(style);
    }

    @Portable
    public enum Style {
        PAGE,
        FLUID;
    }
}
