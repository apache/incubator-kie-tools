/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.moduleeditor;

import java.util.List;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.Module;
//import org.drools.guvnor.client.rpc.PushClient;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.ServerPushNotification;
/*import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;
import org.drools.guvnor.client.util.Util;*/
import org.drools.guvnor.client.widgets.tables.AssetPagedTable;
import org.uberfire.client.common.LazyStackPanel;
import org.uberfire.client.common.LoadContentCommand;
import org.uberfire.client.common.LoadingPopup;
import org.uberfire.client.common.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A View displaying a package's assets
 */
public class AssetViewerActivityViewImpl extends Composite
        implements
        AssetViewerActivityPresenter.View {

    private static final ConstantsCore constants = GWT.create( ConstantsCore.class );

    interface AssetViewerActivityViewImplBinder
            extends
            UiBinder<Widget, AssetViewerActivityViewImpl> {
    }

    private static AssetViewerActivityViewImplBinder uiBinder = GWT.create( AssetViewerActivityViewImplBinder.class );

    @UiField
    VerticalPanel                                    assetGroupsContainer;

    @UiField
    HorizontalPanel                                  msgNoAssetsDefinedInPackage;

    public AssetViewerActivityViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void showLoadingPackageInformationMessage() {
        LoadingPopup.showMessage( constants.LoadingPackageInformation() );
    }

    public void closeLoadingPackageInformationMessage() {
        LoadingPopup.close();
    }

    @Override
    public void addAssetFormat(final List<String> formatsInList,
                               final Boolean formatIsRegistered,
                               final String title,
                               final ImageResource icon,
                               final Module packageConfigData) {
        LazyStackPanel lsp = new LazyStackPanel();
        lsp.add( title,
                 icon,
                 new LoadContentCommand() {
                     public Widget load() {
                         //Lazy load of table
                         return makeTable( formatsInList,
                                           formatIsRegistered,
                                           packageConfigData );
                     }
                 } );
        assetGroupsContainer.add( lsp );
    }

    public void showHasNoAssetsWarning(boolean isVisible) {
        msgNoAssetsDefinedInPackage.setVisible( isVisible );
    }

    private AssetPagedTable makeTable(final List<String> formatsInList,
                                      final Boolean formatIsRegistered,
                                      final Module packageConfigData) {

        //Asset table
        final AssetPagedTable table = new AssetPagedTable( packageConfigData.getUuid(),
                                                           formatsInList,
                                                           formatIsRegistered,
                                                           getFeedUrl( packageConfigData.getName() ) );

        //Add handlers for servers-side push notifications (of new Assets)
        final ServerPushNotification sub = new ServerPushNotification() {
            public void messageReceived(PushResponse response) {
                if ( response.messageType.equals( "packageChange" )
                        && response.message.equals( packageConfigData.getName() ) ) {
                    table.refresh();
                }
            }
        };
        
        //JLIU: TODO
/*        PushClient.instance().subscribe( sub );
        table.addUnloadListener( new Command() {
            public void execute() {
                PushClient.instance().unsubscribe( sub );
            }
        } );
*/
        return table;

    }

    private String getFeedUrl(String packageName) {
        return GWT.getModuleBaseURL()
                + "feed/package?name="
                + packageName
                + "&viewUrl="
                + Util.getSelfURL()
                + "&status=*";
    }

}
