/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.common;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LazyStackPanelRow extends VerticalPanel {

    private final AbstractLazyStackPanelHeader header;
    private LoadContentCommand contentLoad;
    private Widget contentWidget = null;
    private SimplePanel contentPanel = new SimplePanel();

    private static final int ANIMATION_DURATION = 350;

    private boolean expanded;

    public LazyStackPanelRow(AbstractLazyStackPanelHeader titleWidget,
                             LoadContentCommand contentLoad,
                             boolean expanded) {
        this.setWidth("100%");
        this.expanded = expanded;
        this.header = titleWidget;
        this.contentLoad = contentLoad;
        init();
    }

    public LazyStackPanelRow(AbstractLazyStackPanelHeader titleWidget,
                             LoadContentCommand contentLoad) {
        this(titleWidget,
                contentLoad,
                false);
    }

    private void init() {
        clear();
        add(header);
        if (contentWidget != null) {
            contentWidget.setVisible(expanded);
        }
    }

    public AbstractLazyStackPanelHeader getHeader() {
        return header;
    }

    public SimplePanel getContentPanel() {
        return contentPanel;
    }

    public void expand() {
        expanded = true;

        if (contentWidget == null) {
            contentWidget = contentLoad.load();
            contentPanel.add(contentWidget);
        }
        contentPanel.setVisible(true);
        doAnimation(true);
    }

    public void compress() {
        expanded = false;
        contentPanel.setVisible(true);
        doAnimation(false);
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void doAnimation(boolean isExpanding) {
        if (contentPanel.getWidget() != null) {
            ContentAnimation contentAnimation = new ContentAnimation(contentPanel);
            contentAnimation.setExpanding(isExpanding);
            contentAnimation.run(ANIMATION_DURATION);
        }
    }

    private static class ContentAnimation extends Animation {

        private boolean isExpanding;
        private SimplePanel content;
        private int height;

        ContentAnimation(SimplePanel content) {
            this.content = content;
            this.height = content.getOffsetHeight();
        }

        void setExpanding(boolean isExpanding) {
            this.isExpanding = isExpanding;
        }

        @Override
        protected void onUpdate(double progress) {
            if (!isExpanding) {
                progress = 1.0 - progress;
            }
            int h = (int) (this.height * progress);
            content.setHeight(h + "px");
        }

        @Override
        protected void onStart() {
            content.getElement().getStyle().setOverflow(Overflow.HIDDEN);
            super.onStart();
        }

        @Override
        protected void onComplete() {
            super.onComplete();
            content.setVisible(isExpanding);
            content.getElement().getStyle().setOverflow(Overflow.VISIBLE);
            content.setHeight("100%");
        }

    }



}
