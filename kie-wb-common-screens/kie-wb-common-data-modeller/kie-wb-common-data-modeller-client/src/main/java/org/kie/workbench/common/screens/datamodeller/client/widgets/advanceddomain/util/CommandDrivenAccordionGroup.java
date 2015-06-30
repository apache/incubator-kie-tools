/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.util;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.github.gwtbootstrap.client.ui.base.HasIcon;
import com.github.gwtbootstrap.client.ui.base.HasVisibility;
import com.github.gwtbootstrap.client.ui.base.HasVisibleHandlers;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.github.gwtbootstrap.client.ui.constants.BaseIconType;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.github.gwtbootstrap.client.ui.constants.IconPosition;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

public class CommandDrivenAccordionGroup
        extends DivWidget
        implements HasIcon,HasVisibility, HasVisibleHandlers {

    private DivWidget innerBody = new DivWidget( Constants.ACCORDION_INNER );

    private IconAnchor trigger = new IconAnchor();

    private Collapse collapse;

    private CollapseTrigger collapseTrigger;

    private Button commandButton;

    private Command command;

    private boolean defaultOpen;

    int item = 0;

    public CommandDrivenAccordionGroup( String commandName, Command command ) {
        super( Constants.ACCORDION_GROUP );

        DivWidget body = new DivWidget( Constants.ACCORDION_BODY );

        body.add( innerBody );

        collapse = new Collapse();

        collapse.setWidget( body );

        collapse.setExistTrigger( true );

        trigger.addStyleName( Constants.ACCORDION_TOGGLE );

        collapseTrigger = new CollapseTrigger( "#" + collapse.getId() );

        collapseTrigger.setWidget( trigger );

        DivWidget heading = new DivWidget( Constants.ACCORDION_HEADING );

        HorizontalPanel headingContent = new HorizontalPanel();
        heading.add( headingContent );

        headingContent.setWidth( "100%" );

        headingContent.add( collapseTrigger );
        final int currentItem = item;

        this.command = command;
        commandButton = new Button( commandName, new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                if ( CommandDrivenAccordionGroup.this.command != null ) {
                    CommandDrivenAccordionGroup.this.command.execute();
                }
            }
        } );

        commandButton.setStylePrimaryName( "accordion-toggle" );

        DivWidget buttonContainer = new DivWidget(  );
        buttonContainer.setStyle( new Style() {
            @Override public String get() {
                return "float: right;";
            }
        } );

        buttonContainer.add( commandButton );
        headingContent.add( buttonContainer );

        headingContent.setCellHorizontalAlignment( buttonContainer, HasHorizontalAlignment.ALIGN_RIGHT );

        super.add( heading );

        super.add( collapse.asWidget() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIcon( IconType type ) {
        setBaseIcon( type );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBaseIcon( BaseIconType type ) {
        trigger.setBaseIcon( type );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconSize( IconSize size ) {
        trigger.setIconSize( size );
    }

    public void setParent( String parent ) {
        collapseTrigger.setParent( parent );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerRegistration addHideHandler( HideHandler handler ) {
        return collapse.addHideHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerRegistration addHiddenHandler( HiddenHandler handler ) {
        return collapse.addHiddenHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerRegistration addShowHandler( ShowHandler handler ) {
        return collapse.addShowHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerRegistration addShownHandler( ShownHandler handler ) {
        return collapse.addShownHandler( handler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        collapse.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hide() {
        collapse.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggle() {
        collapse.toggle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add( Widget w ) {
        innerBody.add( w );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        innerBody.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove( Widget w ) {
        return innerBody.remove( w );
    }

    /**
     * Add a widget to trigger anchor
     * @param w added widget
     */
    @UiChild( limit = 1, tagname = "customTrigger" )
    public void addCustomTrigger( Widget w ) {
        trigger.insert( w, 0 );
    }

    /**
     * is opened on attached.
     * @return defaultOpen true:open false:close
     */
    public boolean isDefaultOpen() {
        return defaultOpen;
    }

    /**
     * Set is opened on attached.
     * @param defaultOpen true:open false:close
     */
    public void setDefaultOpen( boolean defaultOpen ) {
        this.defaultOpen = defaultOpen;

        if ( !isAttached() ) {
            collapse.getWidget().setStyleName( "in", defaultOpen );
        }

    }

    public void setHeading( String heading ) {
        trigger.setText( heading );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCustomIconStyle( String customIconStyle ) {
        trigger.setCustomIconStyle( customIconStyle );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconPosition( IconPosition position ) {
        trigger.setIconPosition( position );
    }

}