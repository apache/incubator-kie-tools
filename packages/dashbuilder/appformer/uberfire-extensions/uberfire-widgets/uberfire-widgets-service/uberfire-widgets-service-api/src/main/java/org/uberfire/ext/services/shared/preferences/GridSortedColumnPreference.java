/*
 * Copyright 2019 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GridSortedColumnPreference implements Comparable {

    private String dataStoreName;
    private boolean ascending;

    public GridSortedColumnPreference(@MapsTo("dataStoreName") String dataStoreName,
                                      @MapsTo("ascending") boolean ascending) {
        this.dataStoreName = dataStoreName;
        this.ascending = ascending;
    }

    public String getDataStoreName() {
        return dataStoreName;
    }

    public void setDataStoreName(String dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GridSortedColumnPreference that = (GridSortedColumnPreference) o;

        if (ascending != that.ascending) {
            return false;
        }
        return dataStoreName != null ? dataStoreName.equals(that.dataStoreName) : that.dataStoreName == null;
    }

    @Override
    public int hashCode() {
        int result = dataStoreName != null ? dataStoreName.hashCode() : 0;
        result = 31 * result + (ascending ? 1 : 0);
        result = ~~result;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof GridSortedColumnPreference)) {
            return 0;
        }
        String name = ((GridSortedColumnPreference) o).getDataStoreName();
        boolean order = ((GridSortedColumnPreference) o).isAscending();

        if (!dataStoreName.equals(name)) {
            return 0;
        }

        if (order != ascending) {
            return 0;
        }

        return 1;
    }
}
