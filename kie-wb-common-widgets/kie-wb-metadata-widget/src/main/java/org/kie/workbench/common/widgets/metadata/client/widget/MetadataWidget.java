/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.SmallLabel;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
@Dependent
public class MetadataWidget extends DirtyableComposite implements HasBusyIndicator {

    private ClientTypeRegistry clientTypeRegistry = null;

    private Metadata metadata = null;
    private boolean readOnly;
    private VerticalPanel layout = new VerticalPanel();

    private FormStyleLayout currentSection;
    private String currentSectionName;

    private List<DirtyableComposite> compositeList = new ArrayList<DirtyableComposite>();

    @Inject
    private BusyIndicatorView busyIndicatorView;

    public MetadataWidget() {
        layout.setWidth( "100%" );
        initWidget( layout );
    }

    public void setContent( final Metadata metadata,
                            final boolean readOnly ) {
        this.metadata = checkNotNull( "metadata", metadata );
        this.readOnly = readOnly;

        layout.clear();

        startSection( MetadataConstants.INSTANCE.Metadata() );
        addHeader( metadata.getPath().getFileName() );

        loadData();
    }

    private void addHeader( final String name ) {
        final HorizontalPanel hp = new HorizontalPanel();
        hp.add( new SmallLabel( "<b>" + name + "</b>" ) );
        currentSection.addAttribute( MetadataConstants.INSTANCE.Title(), hp );
    }

    private void loadData() {
        addAttribute( MetadataConstants.INSTANCE.CategoriesMetaData(), categories() );

        addAttribute( MetadataConstants.INSTANCE.LastModified(),
                      readOnlyDate( metadata.getLastModified() ) );
        addAttribute( MetadataConstants.INSTANCE.ModifiedByMetaData(),
                      readOnlyText( metadata.getLastContributor() ) );
        addAttribute( MetadataConstants.INSTANCE.NoteMetaData(),
                      readOnlyText( metadata.getCheckinComment() ) );

        if ( !readOnly ) {
            addAttribute( MetadataConstants.INSTANCE.CreatedOnMetaData(),
                          readOnlyDate( metadata.getDateCreated() ) );
        }

        addAttribute( MetadataConstants.INSTANCE.CreatedByMetaData(),
                      readOnlyText( metadata.getCreator() ) );

        addAttribute( MetadataConstants.INSTANCE.FormatMetaData(),
                      readOnlyText( getClientTypeRegistry().resolve( metadata.getPath() ).getShortName() ) );
        addAttribute( "URI:",
                      readOnlyText( metadata.getPath().toURI() ) );

        endSection( false );

        startSection( MetadataConstants.INSTANCE.OtherMetaData() );

        addAttribute( MetadataConstants.INSTANCE.SubjectMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getSubject();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setSubject( val );
                          }
                      }, MetadataConstants.INSTANCE.AShortDescriptionOfTheSubjectMatter() ) );

        addAttribute( MetadataConstants.INSTANCE.TypeMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getType();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setType( val );
                          }

                      }, MetadataConstants.INSTANCE.TypeTip() ) );

        addAttribute( MetadataConstants.INSTANCE.ExternalLinkMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getExternalRelation();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setExternalRelation( val );
                          }

                      }, MetadataConstants.INSTANCE.ExternalLinkTip() ) );

        addAttribute( MetadataConstants.INSTANCE.SourceMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getExternalSource();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setExternalSource( val );
                          }

                      }, MetadataConstants.INSTANCE.SourceMetaDataTip() ) );

        endSection( true );

        if ( !readOnly ) {
            startSection( MetadataConstants.INSTANCE.VersionHistory() );
            addRow( new VersionBrowser( metadata ) );
            endSection( true );
        }

        layout.add( commentWidget() );

        layout.add( discussionWidget() );
    }

    private Widget commentWidget() {
        final CommentWidget widget = new CommentWidget( metadata, readOnly );
        compositeList.add( widget );

        return widget;
    }

    private Widget discussionWidget() {
        final DiscussionWidget widget = new DiscussionWidget( metadata, readOnly );
        compositeList.add( widget );

        return widget;
    }

    private void addRow( Widget widget ) {
        this.currentSection.addRow( widget );
    }

    private void addAttribute( final String string,
                               final Widget widget ) {
        this.currentSection.addAttribute( string, widget );
    }

    private void endSection( final boolean collapsed ) {
        final DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( currentSectionName );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( !collapsed );
        advancedDisclosure.setContent( this.currentSection );
        layout.add( advancedDisclosure );
    }

    private void startSection( final String name ) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    private Widget readOnlyDate( final Date date ) {
        if ( date == null ) {
            return null;
        } else {
            return new SmallLabel( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( date ) );
        }
    }

    private Label readOnlyText( final String text ) {
        SmallLabel lbl = new SmallLabel( text );
        lbl.setWidth( "100%" );
        return lbl;
    }

    private Widget categories() {
        return new CategorySelectorWidget( metadata, this.readOnly );
    }

    public boolean isDirty() {
        for ( final DirtyableComposite widget : compositeList ) {
            if ( widget.isDirty() ) {
                return true;
            }
        }

        return dirtyflag;
    }

    public void resetDirty() {
        for ( final DirtyableComposite widget : compositeList ) {
            widget.resetDirty();
        }
        this.dirtyflag = false;
    }

    /**
     * This binds a field, and returns a check box editor for it.
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableBoolean( final FieldBooleanBinding bind,
                                    final String toolTip ) {
        if ( !readOnly ) {
            final CheckBox box = new CheckBox();
            box.setTitle( toolTip );
            box.setValue( bind.getValue() );
            box.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent w ) {
                    boolean b = box.getValue();
                    bind.setValue( b );
                }
            } );
            return box;
        } else {
            final CheckBox box = new CheckBox();

            box.setValue( bind.getValue() );
            box.setEnabled( false );

            return box;
        }
    }

    /**
     * This binds a field, and returns a TextBox editor for it.
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableText( final FieldBinding bind,
                                 String toolTip ) {
        if ( !readOnly ) {
            final TextBox tbox = new TextBox();
            tbox.setTitle( toolTip );
            tbox.setText( bind.getValue() );
            tbox.setVisibleLength( 10 );
            tbox.addChangeHandler( new ChangeHandler() {
                public void onChange( final ChangeEvent event ) {
                    bind.setValue( tbox.getText() );
                }
            } );
            return tbox;
        } else {
            return new Label( bind.getValue() );
        }
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBinding {

        void setValue( String val );

        String getValue();
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBooleanBinding {

        void setValue( boolean val );

        boolean getValue();
    }

    /**
     * Return the data if it is to be saved.
     */
    public Metadata getContent() {
        return metadata;
    }

    public ClientTypeRegistry getClientTypeRegistry() {
        if ( this.clientTypeRegistry == null ) {
            clientTypeRegistry = IOC.getBeanManager().lookupBean( ClientTypeRegistry.class ).getInstance();
        }
        return clientTypeRegistry;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        busyIndicatorView.showBusyIndicator( message );
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }
}
