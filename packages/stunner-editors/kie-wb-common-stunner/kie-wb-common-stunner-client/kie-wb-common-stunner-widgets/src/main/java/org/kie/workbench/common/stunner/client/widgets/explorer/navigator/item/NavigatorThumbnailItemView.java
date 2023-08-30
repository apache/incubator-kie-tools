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


package org.kie.workbench.common.stunner.client.widgets.explorer.navigator.item;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelFooter;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.ThumbnailLink;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.explorer.navigator.NavigatorItemView;

@Dependent
public class NavigatorThumbnailItemView
        extends Composite implements NavigatorItemView<NavigatorItem> {

    interface ViewBinder extends UiBinder<Widget, NavigatorThumbnailItemView> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    FlowPanel mainPanel;

    @UiField
    Panel panel;

    @UiField
    PanelBody body;

    @UiField
    Heading heading;

    @UiField
    ThumbnailLink item;

    @UiField
    Popover popover;

    @UiField
    Image thumbImage;

    @UiField
    PanelFooter footer;

    private NavigatorItem presenter;
    private boolean selected = false;

    @Override
    public void init(final NavigatorItem presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        item.addClickHandler(clickEvent -> presenter.onItemSelected());
        footer.addDomHandler(event -> presenter.onItemSelected(),
                             ClickEvent.getType());
        footer.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        popover.addShowHandler(showEvent -> onGotFocus());
        popover.addHideHandler(hideEvent -> onLostFocus());
    }

    public NavigatorThumbnailItemView select() {
        this.selected = true;
        doSelect();
        return this;
    }

    public NavigatorThumbnailItemView deselect() {
        this.selected = false;
        doDeselect();
        return this;
    }

    @Override
    public NavigatorThumbnailItemView setUUID(final String uuid) {
        popover.setContent(uuid);
        return this;
    }

    @Override
    public NavigatorThumbnailItemView setItemTitle(final String title) {
        heading.setText(title);
        heading.setTitle(title);
        popover.setTitle(title);
        return this;
    }

    @Override
    public NavigatorThumbnailItemView setThumbData(final String thumbData) {
        thumbImage.setUrl(thumbData);
        return this;
    }

    @Override
    public NavigatorThumbnailItemView setThumbUri(final SafeUri safeUri) {
        thumbImage.setUrl(safeUri);
        return this;
    }

    @Override
    public NavigatorThumbnailItemView setItemPxSize(final int width,
                                                    final int height) {
        thumbImage.addLoadHandler((e) -> {
            final int imgWidth = thumbImage.getWidth();
            final int imgHeight = thumbImage.getHeight();
            final float wfactor = imgWidth > width ? imgWidth / width : 1;
            final float hfactor = imgHeight > height ? imgHeight / height : 1;
            final float factor = wfactor >= hfactor ? wfactor : hfactor;
            int w = width;
            int h = height;
            if (factor > 1) {
                w = (int) Math.ceil(imgWidth / factor);
                h = (int) Math.ceil(imgHeight / factor);
            }
            thumbImage.setPixelSize(w,
                                    h);
            body.setPixelSize(width,
                              height);
        });
        return this;
    }

    private void doSelect() {
        panel.getElement().getStyle().setBorderColor("#0000FF");
        heading.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
    }

    private void doDeselect() {
        if (!selected) {
            panel.getElement().getStyle().setBorderColor("#000000");
            heading.getElement().getStyle().setFontWeight(Style.FontWeight.NORMAL);
        }
    }

    private void onGotFocus() {
        doSelect();
    }

    private void onLostFocus() {
        doDeselect();
    }
}
