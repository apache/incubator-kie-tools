/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

import org.drools.guvnor.client.resources.ComparableImageResource;
/*import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.resources.ComparableImageResource;
import org.drools.guvnor.client.resources.ImagesCore;*/
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.util.ValidImageFactory;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;
/*import org.drools.guvnor.client.util.ValidImageFactory;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;*/

import java.util.Date;
import java.util.List;

/**
 * Widget with a table of Assets.
 */
public class AssetPagedTable extends AbstractAssetPagedTable<AssetPageRow> {

    private static final int PAGE_SIZE = 10;

    public AssetPagedTable(String packageUuid,
                            List<String> formatInList,
                            Boolean formatIsRegistered) {
        this( packageUuid,
                formatInList,
                formatIsRegistered,
                null);
    }

    public AssetPagedTable(final String packageUuid,
                            final List<String> formatInList,
                            final Boolean formatIsRegistered,
                            String feedURL) {
        super( PAGE_SIZE,
                feedURL);

/*        setDataProvider( new AsyncDataProvider<AssetPageRow>() {
            protected void onRangeChanged(HasData<AssetPageRow> display) {
                AssetPageRequest request = new AssetPageRequest( packageUuid,
                                                                 formatInList,
                                                                 formatIsRegistered,
                                                                 pager.getPageStart(),
                                                                 pageSize );
                assetService.findAssetPage( request,
                                            new GenericCallback<PageResponse<AssetPageRow>>() {
                                                public void onSuccess(PageResponse<AssetPageRow> response) {
                                                    updateRowCount( response.getTotalRowSize(),
                                                                    response.isTotalRowSizeExact() );
                                                    updateRowData( response.getStartRowIndex(),
                                                                   response.getPageRowList() );
                                                }
                                            } );
            }
        } );*/

    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<AssetPageRow> columnPicker,
                                        SortableHeaderGroup<AssetPageRow> sortableHeaderGroup) {

        Column<AssetPageRow, ComparableImageResource> formatColumn = new Column<AssetPageRow, ComparableImageResource>( new ComparableImageResourceCell() ) {

            public ComparableImageResource getValue(AssetPageRow row) {
            	//JLIU: TODO
            	return null;
/*                AssetEditorFactory factory = clientFactory.getAssetEditorFactory();
                return new ComparableImageResource( row.getFormat(),
                                                    factory.getAssetEditorIcon( row.getFormat() ) );*/
            }
        };
        columnPicker.addColumn( formatColumn,
                                new SortableHeader<AssetPageRow, ComparableImageResource>( sortableHeaderGroup,
                                                                                           constants.Format(),
                                                                                           formatColumn ),
                                true );

        Column<AssetPageRow, ComparableImageResource> validColumn = new Column<AssetPageRow, ComparableImageResource>( new ComparableImageResourceCell() ) {

            public ComparableImageResource getValue(AssetPageRow row) {
                ImageResource image = ValidImageFactory.getImage(row.getValid());
                return new ComparableImageResource(row.getValid().toString(), image);
            }
        };

        columnPicker.addColumn( validColumn,
                new SortableHeader<AssetPageRow, ComparableImageResource >( sortableHeaderGroup,
                        constants.Valid(),
                        validColumn ),
                true );

        TitledTextColumn<AssetPageRow> titleColumn = new TitledTextColumn<AssetPageRow>() {
            public TitledText getValue(AssetPageRow row) {
                return new TitledText( row.getName(),
                                                row.getAbbreviatedDescription() );
            }
        };
        columnPicker.addColumn( titleColumn,
                                new SortableHeader<AssetPageRow, TitledText>( sortableHeaderGroup,
                                                                              constants.Name(),
                                                                              titleColumn ),
                                true );

        TextColumn<AssetPageRow> packageNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getPackageName();
            }
        };
        columnPicker.addColumn( packageNameColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.PackageName(),
                                                                          packageNameColumn ),
                                false );

        TextColumn<AssetPageRow> stateNameColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getStateName();
            }
        };
        columnPicker.addColumn( stateNameColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.Status(),
                                                                          stateNameColumn ),
                                true );

        TextColumn<AssetPageRow> creatorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCreator();
            }
        };
        columnPicker.addColumn( creatorColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.Creator(),
                                                                          creatorColumn ),
                                false );

        Column<AssetPageRow, Date> createdDateColumn = new Column<AssetPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(AssetPageRow row) {
                return row.getCreatedDate();
            }
        };
        columnPicker.addColumn( createdDateColumn,
                                new SortableHeader<AssetPageRow, Date>( sortableHeaderGroup,
                                                                        constants.CreatedDate(),
                                                                        createdDateColumn ),
                                false );

        TextColumn<AssetPageRow> lastContributorColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getLastContributor();
            }
        };
        columnPicker.addColumn( lastContributorColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.LastContributor(),
                                                                          lastContributorColumn ),
                                false );

        Column<AssetPageRow, Date> lastModifiedColumn = new Column<AssetPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(AssetPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<AssetPageRow, Date>( sortableHeaderGroup,
                                                                        constants.LastModified(),
                                                                        lastModifiedColumn ),
                                true );

        TextColumn<AssetPageRow> categorySummaryColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getCategorySummary();
            }
        };
        columnPicker.addColumn( categorySummaryColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.Categories(),
                                                                          categorySummaryColumn ),
                                false );

        TextColumn<AssetPageRow> externalSourceColumn = new TextColumn<AssetPageRow>() {
            public String getValue(AssetPageRow row) {
                return row.getExternalSource();
            }
        };
        columnPicker.addColumn( externalSourceColumn,
                                new SortableHeader<AssetPageRow, String>( sortableHeaderGroup,
                                                                          constants.ExternalSource(),
                                                                          externalSourceColumn ),
                                false );

        Column<AssetPageRow, Boolean> isDisabledColumn = new Column<AssetPageRow, Boolean>( new RuleEnabledStateCell() ) {
            public Boolean getValue(AssetPageRow row) {
                return row.isDisabled();
            }
        };
        columnPicker.addColumn( isDisabledColumn,
                                new SortableHeader<AssetPageRow, Boolean>( sortableHeaderGroup,
                                                                           constants.AssetTableIsDisabled(),
                                                                           isDisabledColumn ),
                                false );

    }

}
