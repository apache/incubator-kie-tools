/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.rpc.*;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.SmallLabel;

import java.util.Date;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class MetaDataWidget extends Composite {
    private ConstantsCore constants = GWT.create( ConstantsCore.class );
    private static GuvnorImages images = GWT.create( GuvnorImages.class );

    private final Artifact artifact;
    private final boolean readOnly;
    private final String uuid;
    private VerticalPanel layout = new VerticalPanel();
    //AssetCategoryEditor ed;
    private FormStyleLayout currentSection;
    private String currentSectionName;


    public MetaDataWidget(final Artifact artifact,
                          boolean readOnly,
                          final String uuid) {
        super();

        this.uuid = uuid;
        this.artifact = artifact;
        this.readOnly = readOnly;

        layout.setWidth( "100%" );
        initWidget( layout );
        render();
    }

    private void render() {
        layout.clear();
        //layout.add( new SmallLabel( constants.Title() + ": [<b>" + data.name + "</b>]" ) );
        startSection( constants.Metadata() );
        addHeader( images.assetVersion(),
                artifact.getName(),
                null );

        loadData();
    }

    private void addHeader(ImageResource img,
                           String name,
                           Image edit) {

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( new SmallLabel( "<b>" + name + "</b>" ) );
        if ( edit != null ) hp.add( edit );
        currentSection.addAttribute( constants.Title(),
                hp );
    }

    private void loadData() {
    	//JLIU
/*        if ( artifact instanceof Asset ) {
            addAttribute( constants.CategoriesMetaData(),
                    categories() );
        }*/

        addAttribute( constants.LastModified(),
                readOnlyDate( artifact.getLastModified() ) );
        addAttribute( constants.ModifiedByMetaData(),
                readOnlyText( artifact.getLastContributor() ) );
        addAttribute( constants.NoteMetaData(),
                readOnlyText( artifact.getCheckinComment() ) );

        if ( !readOnly ) {
            addAttribute( constants.CreatedOnMetaData(),
                    readOnlyDate( artifact.getDateCreated() ) );
        }

        if ( artifact instanceof Asset ) {
            addAttribute( constants.CreatedByMetaData(),
                    readOnlyText( ((Asset) artifact).getMetaData().getCreator() ) );

            addAttribute( constants.PackageMetaData(),
                    packageEditor( ((Asset) artifact).getMetaData().getModuleName() ) );

            addAttribute( constants.IsDisabledMetaData(),
                    editableBoolean( new FieldBooleanBinding() {
                        public boolean getValue() {
                            return ((Asset) artifact).getMetaData().isDisabled();
                        }

                        public void setValue(boolean val) {
                            ((Asset) artifact).getMetaData().setDisabled( val );
                        }
                    },
                            constants.DisableTip() ) );
        }
        
        addAttribute( constants.FormatMetaData(),
                readOnlyText(artifact.getFormat()));
        addAttribute( "UUID:",
                readOnlyText( uuid ) );

        endSection( false );

        if ( artifact instanceof Asset ) {

            final MetaData data = ((Asset) artifact).getMetaData();
            startSection( constants.OtherMetaData() );

            addAttribute( constants.SubjectMetaData(),
                    editableText( new FieldBinding() {
                        public String getValue() {
                            return data.subject;
                        }

                        public void setValue(String val) {
                            data.subject = val;
                        }
                    },
                            constants.AShortDescriptionOfTheSubjectMatter()));

            addAttribute(constants.TypeMetaData(),
                    editableText(new FieldBinding() {
                        public String getValue() {
                            return data.type;
                        }

                        public void setValue(String val) {
                            data.type = val;
                        }

                    },
                            constants.TypeTip()));

            addAttribute(constants.ExternalLinkMetaData(),
                    editableText(new FieldBinding() {
                        public String getValue() {
                            return data.externalRelation;
                        }

                        public void setValue(String val) {
                            data.externalRelation = val;
                        }

                    },
                            constants.ExternalLinkTip()));

            addAttribute(constants.SourceMetaData(),
                    editableText(new FieldBinding() {
                        public String getValue() {
                            return data.externalSource;
                        }

                        public void setValue(String val) {
                            data.externalSource = val;
                        }

                    },
                            constants.SourceMetaDataTip()));

            endSection(true);
        }

        startSection(constants.VersionHistory());

        Image image = new Image(images.feed());
        image.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Window.open(getVersionFeed(artifact), "_blank", null);

            }
        });
        addAttribute(constants.VersionFeed(), image);

        addAttribute(constants.CurrentVersionNumber(),
                getVersionNumberLabel());

        //JLIU
/*        if (!readOnly) {
            addRow(new VersionBrowser(clientFactory,
                    eventBus,
                    this.uuid,
                    !(artifact instanceof Asset)));
        }
*/
        endSection(true);
    }

    private void addRow(Widget widget) {
        this.currentSection.addRow(widget);
    }

    private void addAttribute(String string,
            Widget widget) {
        this.currentSection.addAttribute(string,
                widget);
    }

    private void endSection(boolean collapsed) {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel(currentSectionName);
        advancedDisclosure.setWidth("100%");
        advancedDisclosure.setOpen(!collapsed);
        advancedDisclosure.setContent(this.currentSection);
        layout.add(advancedDisclosure);
    }

    private void startSection(String name) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    private Widget packageEditor(final String packageName) {
    	//JLIU: TODO: 
        if (this.readOnly /*|| !UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_KNOWLEDGE_BASES_VIEW)*/) {
            return readOnlyText(packageName);
        } else {
            HorizontalPanel horiz = new HorizontalPanel();
            horiz.setStyleName("metadata-Widget"); //NON-NLS
            horiz.add(readOnlyText(packageName));
            Image editPackage = new ImageButton(images.edit());
            editPackage.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent w) {
                    showEditPackage(packageName,
                            w);
                }
            });
            horiz.add(editPackage);
            return horiz;
        }
    }

    private void showEditPackage(final String pkg,
            ClickEvent source) {
    	//JLIU
/*        final FormStylePopup pop = new FormStylePopup(images.packageLarge(),
                constants.MoveThisItemToAnotherPackage());
        pop.addAttribute(constants.CurrentPackage(),
                new Label(pkg));
        final RulePackageSelector sel = new RulePackageSelector();
        pop.addAttribute(constants.NewPackage(),
                sel);
        Button ok = new Button(constants.ChangePackage());
        pop.addAttribute("",
                ok);
        ok.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent w) {
                if (sel.getSelectedPackage().equals(pkg)) {
                    Window.alert(constants.YouNeedToPickADifferentPackageToMoveThisTo());
                    return;
                }
                AssetServiceAsync assetService = GWT.create(AssetService.class);
                assetService.changeAssetPackage(uuid,
                        sel.getSelectedPackage(),
                        constants.MovedFromPackage(pkg),
                        new GenericCallback<java.lang.Void>() {
                            public void onSuccess(Void v) {
                                eventBus.fireEvent(new RefreshAssetEditorEvent(sel.getSelectedPackage(), uuid));
                                pop.hide();
                            }

                        });

            }

        });

        pop.show();*/
    }

    private void close() {
    	//JLIU
        //eventBus.fireEvent(new ClosePlaceEvent(new AssetEditorPlace(uuid)));
    }

    private Widget getVersionNumberLabel() {
        if (artifact.getVersionNumber() == 0) {
            return new SmallLabel(constants.NotCheckedInYet());
        } else {
            return readOnlyText(Long.toString(artifact.getVersionNumber()));
        }
    }

    private Widget readOnlyDate(Date lastModifiedDate) {
        if (lastModifiedDate == null) {
            return null;
        } else {
            return new SmallLabel(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(lastModifiedDate));
        }
    }

    private Label readOnlyText(String text) {
        SmallLabel lbl = new SmallLabel(text);
        lbl.setWidth("100%");
        return lbl;
    }

    //JLIU
/*    private Widget categories() {
        ed = new AssetCategoryEditor(((Asset) this.artifact).getMetaData(),
                this.readOnly);
        return ed;
    }*/

    /**
     * This binds a field, and returns a check box editor for it.
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableBoolean(final FieldBooleanBinding bind,
            String toolTip) {
        if (!readOnly) {
            final CheckBox box = new CheckBox();
            box.setTitle(toolTip);
            box.setValue(bind.getValue());
            ClickHandler listener = new ClickHandler() {
                public void onClick(ClickEvent w) {
                    boolean b = box.getValue();
                    bind.setValue(b);
                }
            };
            box.addClickHandler(listener);
            return box;
        } else {
            final CheckBox box = new CheckBox();

            box.setValue(bind.getValue());
            box.setEnabled(false);

            return box;
        }
    }

    /**
     * This binds a field, and returns a TextBox editor for it.
     *
     * @param bind    Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableText(final FieldBinding bind,
                                String toolTip) {
        if ( !readOnly ) {
            final TextBox tbox = new TextBox();
            tbox.setTitle( toolTip );
            tbox.setText( bind.getValue() );
            tbox.setVisibleLength( 10 );
            ChangeHandler listener = new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    String txt = tbox.getText();
                    bind.setValue( txt );
                }

            };
            tbox.addChangeHandler( listener );
            return tbox;
        } else {
            return new Label( bind.getValue() );
        }
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBinding {
        void setValue(String val);

        String getValue();
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBooleanBinding {
        void setValue(boolean val);

        boolean getValue();
    }

    /**
     * Return the data if it is to be saved.
     */
    public Artifact getData() {
        return artifact;
    }

    public void refresh() {
        render();
    }

    static String getVersionFeed(Artifact artifact) {
        if ( artifact instanceof Module ) {
            return getRESTBaseURL() + "packages/" + artifact.getName() + "/versions";
        } else {
            return getRESTBaseURL() + "packages/" + ((Asset) artifact).getMetaData().getModuleName()
                    + "/assets/" + artifact.getName() + "/versions";
        }
    }

    static String getRESTBaseURL() {
        String url = GWT.getModuleBaseURL();
        return url.replaceFirst( "org.drools.guvnor.Guvnor",
                "rest" );
    }

}
