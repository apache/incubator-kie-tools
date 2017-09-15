/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import com.google.gwt.user.client.ui.Panel;

/*
 * Represents browser scrollbars in the Grid Lienzo context,
 * providing an API to get/set their position.
 */

class GridLienzoScrollBars {

    private final GridLienzoScrollHandler gridLienzoScrollHandler;

    GridLienzoScrollBars(final GridLienzoScrollHandler gridLienzoScrollHandler) {
        this.gridLienzoScrollHandler = gridLienzoScrollHandler;
    }

    Double getHorizontalScrollPosition() {

        final Integer scrollLeft = scrollPanel().getElement().getScrollLeft();
        final Integer scrollWidth = scrollPanel().getElement().getScrollWidth();
        final Integer clientWidth = scrollPanel().getElement().getClientWidth();
        final Integer level = scrollWidth - clientWidth;

        return level == 0 ? 0d : 100d * scrollLeft / level;
    }

    void setHorizontalScrollPosition(final Double percentage) {

        final Integer scrollWidth = scrollPanel().getElement().getScrollWidth();
        final Integer clientWidth = scrollPanel().getElement().getClientWidth();
        final Integer max = scrollWidth - clientWidth;

        setScrollLeft((int) ((max * percentage) / 100));
    }

    Double getVerticalScrollPosition() {

        final Integer scrollTop = scrollPanel().getElement().getScrollTop();
        final Integer scrollHeight = scrollPanel().getElement().getScrollHeight();
        final Integer clientHeight = scrollPanel().getElement().getClientHeight();
        final Integer level = scrollHeight - clientHeight;

        return level == 0 ? 0d : 100d * scrollTop / level;
    }

    void setVerticalScrollPosition(final Double percentage) {

        final Integer scrollHeight = scrollPanel().getElement().getScrollHeight();
        final Integer clientHeight = scrollPanel().getElement().getClientHeight();
        final Integer max = scrollHeight - clientHeight;

        setScrollTop((int) ((max * percentage) / 100));
    }

    void setScrollTop(final Integer scrollTop) {
        scrollPanel().getElement().setScrollTop(scrollTop);
    }

    void setScrollLeft(final Integer scrollLeft) {
        scrollPanel().getElement().setScrollLeft(scrollLeft);
    }

    Panel scrollPanel() {
        return gridLienzoScrollHandler.getScrollPanel();
    }
}
