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

package org.uberfire.ext.editor.commons.client.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

public class VersionMenuDropDownButton
        implements VersionMenuDropDownButtonView.Presenter,
                   HasEnabled,
                   IsWidget {

    private List<VersionRecord> versions;
    private Callback<VersionRecord> selectionCallback;
    private Command showMore;
    private String version;
    private VersionMenuDropDownButtonView view;

    public VersionMenuDropDownButton() {
    }

    @Inject
    public VersionMenuDropDownButton( VersionMenuDropDownButtonView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setItems( List<VersionRecord> versions ) {
        if ( this.versions == null || ( versions.size() > this.versions.size() ) ) {
            this.versions = versions;
        }
        updateTitle();
    }

    @Override
    public void onMenuOpening() {

        PortablePreconditions.checkNotNull( "version", version );
        PortablePreconditions.checkNotNull( "versions", versions );

        view.clear();

        boolean currentHasBeenAdded = false;
        int versionIndex = versions.size();

        ArrayList<VersionRecord> reversedList = new ArrayList<VersionRecord>( versions );
        Collections.reverse( reversedList );
        int versionsDisplayed = 0;

        for ( final VersionRecord versionRecord : reversedList ) {

            boolean isSelected = isSelected( versionRecord );

            if ( isSelected ) {
                currentHasBeenAdded = true;
            }

            if ( versionIndex > ( versions.size() - 7 ) || versions.size() <= 7 ) {

                view.addLabel( versionRecord, isSelected, versionIndex );

            } else {

                if ( !currentHasBeenAdded ) {
                    view.addLabel( getCurrentVersionRecord(), true, getCurrentVersionIndex() );
                    versionsDisplayed++;
                }

                addShowMoreLabel( versionsDisplayed );

                break;

            }

            versionIndex--;
            versionsDisplayed++;
        }
    }

    private void updateTitle() {
        if ( versions != null && version != null ) {
            if ( !versions.isEmpty() && version.equals( versions.get( versions.size() - 1 ).id() ) ) {
                view.setTextToLatest();
            } else {
                view.setTextToVersion( getCurrentVersionIndex() );
            }
        }
    }

    private VersionRecord getCurrentVersionRecord() {
        for ( VersionRecord versionRecord : versions ) {
            if ( versionRecord.id().equals( version ) ) {
                return versionRecord;
            }
        }
        return null;
    }

    private int getCurrentVersionIndex() {
        for ( int i = 0; i < versions.size(); i++ ) {
            if ( versions.get( i ).id().equals( version ) ) {
                return i + 1;
            }
        }
        return -1;
    }

    private boolean isSelected( VersionRecord versionRecord ) {
        return versionRecord.id().equals( version );
    }

    private void addShowMoreLabel( int versionIndex ) {
        view.addViewAllLabel(
                versions.size() - versionIndex,
                new Command() {
                    @Override
                    public void execute() {
                        showMore.execute();
                    }
                } );
    }

    public void addSelectionCallback( Callback<VersionRecord> selectionCallback ) {
        this.selectionCallback = selectionCallback;
    }

    public void setShowMoreCommand( Command showMore ) {
        this.showMore = showMore;
    }

    public void setVersion( String version ) {
        this.version = version;
        updateTitle();
    }

    @Override
    public void onVersionRecordSelected( VersionRecord result ) {
        if ( selectionCallback != null ) {
            selectionCallback.callback( result );
        }
    }

    @Override
    public boolean isEnabled() {
        return view.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        view.setEnabled( enabled );
    }

}
