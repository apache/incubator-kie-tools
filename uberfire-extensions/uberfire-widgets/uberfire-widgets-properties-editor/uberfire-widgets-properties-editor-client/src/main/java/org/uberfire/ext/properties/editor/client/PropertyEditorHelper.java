package org.uberfire.ext.properties.editor.client;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.properties.editor.client.fields.AbstractField;
import org.uberfire.ext.properties.editor.client.fields.PropertyEditorFieldType;
import org.uberfire.ext.properties.editor.client.widgets.AbstractPropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorErrorWidget;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemHelp;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemRemovalButton;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemsWidget;
import org.uberfire.ext.properties.editor.model.CustomPropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public class PropertyEditorHelper {

    public static void extractEditorFrom( PropertyEditorWidget propertyEditorWidget,
                                          Accordion propertyMenu,
                                          PropertyEditorEvent event,
                                          String propertyNameFilter ) {
        propertyMenu.clear();
        for ( PropertyEditorCategory category : event.getSortedProperties() ) {
            createCategory( propertyEditorWidget, propertyMenu, category, propertyNameFilter );
        }
    }

    public static void extractEditorFrom( PropertyEditorWidget propertyEditorWidget,
                                          Accordion propertyMenu,
                                          PropertyEditorEvent event ) {
        extractEditorFrom( propertyEditorWidget, propertyMenu, event, "" );
    }

    static void createCategory( final PropertyEditorWidget propertyEditorWidget,
                                Accordion propertyMenu,
                                final PropertyEditorCategory category,
                                String propertyNameFilter ) {

        AccordionGroup categoryAccordion = createAccordionGroup( propertyEditorWidget, category );
        boolean categoryHasActiveChilds = false;
        for ( final PropertyEditorFieldInfo field : category.getFields() ) {
            if ( isAMatchOfFilter( propertyNameFilter, field ) ) {
                categoryHasActiveChilds = true;
                categoryAccordion.add( createItemsWidget( field,
                                                          category,
                                                          categoryAccordion ) );
            }

        }
        if ( categoryHasActiveChilds ) {
            propertyMenu.add( categoryAccordion );
        }
    }

    static AccordionGroup createAccordionGroup( final PropertyEditorWidget propertyEditorWidget,
                                                final PropertyEditorCategory category ) {
        AccordionGroup categoryAccordion = GWT.create( AccordionGroup.class );
        categoryAccordion.setHeading( category.getName() );
        categoryAccordion.addShowHandler( new ShowHandler() {
            @Override
            public void onShow( ShowEvent showEvent ) {
                propertyEditorWidget.setLastOpenAccordionGroupTitle( category.getName() );
            }
        } );
        categoryAccordion.addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
                hiddenEvent.stopPropagation();
            }
        } );
        if ( propertyEditorWidget.getLastOpenAccordionGroupTitle().equals( category.getName() ) ) {
            categoryAccordion.setDefaultOpen( true );
        }
        return categoryAccordion;
    }

    static PropertyEditorItemsWidget createItemsWidget( PropertyEditorFieldInfo field,
                                                        PropertyEditorCategory category,
                                                        AccordionGroup categoryAccordion ) {
        PropertyEditorItemsWidget items = GWT.create( PropertyEditorItemsWidget.class );
        items.add( createLabel( field ) );
        items.add( createField( field, items ) );
        if ( field.hasHelpInfo() ) {
            items.add( createHelp( field) );
        }
        if ( field.isRemovalSupported() ) {
            items.add( createRemovalButton( field,
                    category,
                    items,
                    categoryAccordion ) );
        }
        return items;
    }

    static PropertyEditorItemLabel createLabel( PropertyEditorFieldInfo field ) {
        PropertyEditorItemLabel item = GWT.create( PropertyEditorItemLabel.class );
        item.setText( field.getLabel() );
        return item;
    }

    static PropertyEditorItemHelp createHelp( PropertyEditorFieldInfo field ) {
        PropertyEditorItemHelp itemHelp = GWT.create( PropertyEditorItemHelp.class );
        itemHelp.setHeading( field.getHelpHeading() );
        itemHelp.setText( field.getHelpText() );
        return itemHelp;
    }

    static PropertyEditorItemWidget createField( PropertyEditorFieldInfo field,
                                                 PropertyEditorItemsWidget parent ) {
        PropertyEditorItemWidget itemWidget = GWT.create( PropertyEditorItemWidget.class );
        PropertyEditorErrorWidget errorWidget = GWT.create( PropertyEditorErrorWidget.class );
        PropertyEditorFieldType editorFieldType = PropertyEditorFieldType.getFieldTypeFrom( field );
        Widget widget;
        if ( editorFieldType == PropertyEditorFieldType.CUSTOM ) {
            Class<?> widgetClass = ( ( CustomPropertyEditorFieldInfo ) field ).getCustomEditorClass();
            widget = getWidget( field, widgetClass );
        } else {
            widget = editorFieldType.widget( field );
        }

        createErrorHandlingInfraStructure( parent, itemWidget, errorWidget, widget );
        itemWidget.add( widget );
        itemWidget.add( errorWidget );

        return itemWidget;
    }


    private static Widget getWidget( PropertyEditorFieldInfo property,
            Class fieldType ) {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef iocBeanDef = beanManager.lookupBean( fieldType );
        AbstractField field = (AbstractField) iocBeanDef.getInstance();
        return field.widget( property );
    }

    static void createErrorHandlingInfraStructure( PropertyEditorItemsWidget parent,
                                                   PropertyEditorItemWidget itemWidget,
                                                   PropertyEditorErrorWidget errorWidget,
                                                   Widget widget ) {
        AbstractPropertyEditorWidget abstractPropertyEditorWidget = (AbstractPropertyEditorWidget) widget;
        abstractPropertyEditorWidget.setErrorWidget( errorWidget );
        abstractPropertyEditorWidget.setParent( parent );
        itemWidget.add( widget );
    }

    static PropertyEditorItemRemovalButton createRemovalButton( final PropertyEditorFieldInfo field,
                                                                final PropertyEditorCategory category,
                                                                final PropertyEditorItemsWidget items,
                                                                final AccordionGroup categoryAccordion ) {
        PropertyEditorItemRemovalButton button = new PropertyEditorItemRemovalButton();
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                category.getFields().remove( field );
                categoryAccordion.remove( items );
            }
        } );
        return button;
    }

    public static boolean validade( PropertyEditorEvent event ) {
        if ( event == null ) {
            throw new NullEventException();
        }
        if ( event.getSortedProperties().isEmpty() ) {
            throw new NoPropertiesException();
        }

        return event != null && !event.getSortedProperties().isEmpty();
    }

    static boolean isAMatchOfFilter( String propertyNameFilter,
                                     PropertyEditorFieldInfo field ) {
        if ( propertyNameFilter.isEmpty() ) {
            return true;
        }
        if ( field.getLabel().toUpperCase().contains( propertyNameFilter.toUpperCase() ) ) {
            return true;
        }
        return false;
    }

    public static class NullEventException extends RuntimeException {

    }

    public static class NoPropertiesException extends RuntimeException {

    }
}