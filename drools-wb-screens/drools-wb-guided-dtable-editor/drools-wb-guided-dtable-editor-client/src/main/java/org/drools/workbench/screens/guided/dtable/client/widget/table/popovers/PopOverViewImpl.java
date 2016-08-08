/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.jboss.errai.ui.client.local.spi.TranslationService;

@Dependent
public class PopOverViewImpl extends Timer implements PopOverView {

    private final Element div = DOM.createDiv();

    private ContentProvider provider;

    @Inject
    public PopOverViewImpl( final TranslationService translator ) {
        div.setId( DOM.createUniqueId() );
        div.getStyle().setPosition( Style.Position.ABSOLUTE );
        div.getStyle().setWidth( 1,
                                 Style.Unit.PX );
        div.setAttribute( "title",
                          translator.getTranslation( GuidedDecisionTableErraiConstants.PopOverViewImpl_Title ) );
        div.setAttribute( "data-toggle",
                          "popover" );
        RootPanel.get().getElement().appendChild( div );

        Scheduler.get().scheduleDeferred( () -> initPopover( div.getId() ) );
    }

    @Override
    public void show( final ContentProvider provider ) {
        this.provider = provider;
        schedule( 500 );
    }

    @Override
    public void run() {
        Scheduler.get().scheduleDeferred( () -> {
            provider.getContent( ( c ) -> {
                div.setAttribute( "data-content",
                                  c.getContent() );
                div.getStyle().setLeft( c.getX(),
                                        Style.Unit.PX );
                div.getStyle().setTop( c.getY(),
                                       Style.Unit.PX );
                showPopover( div.getId() );
            } );
        } );
    }

    @Override
    public void hide() {
        hidePopover( div.getId() );
        cancel();
    }

    private native void initPopover( final String id ) /*-{
        var jQueryId = '#' + id;
        var div = $wnd.jQuery(jQueryId);
        var template = '<div class="popover" style="width:400px;min-width:400px;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content" style="font-family:Courier New"></div></div>';

        div.popover({
            trigger: 'manual',
            template: template,
            placement: 'top',
            html: true,
            content: function () {
                var offsetWidth = $wnd.document.getElementById(id).offsetWidth;
                var scrollWidth = $wnd.document.getElementById(id).scrollWidth;
                return offsetWidth < scrollWidth ? div.html() : "";
            },
            container: 'body'
        });
    }-*/;

    private native void showPopover( final String id ) /*-{
        $wnd.jQuery('#' + id).popover('show');
    }-*/;

    private native void hidePopover( final String id ) /*-{
        $wnd.jQuery('#' + id).popover('hide');
    }-*/;

}
