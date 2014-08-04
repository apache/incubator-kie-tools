/*
 * Copyright 2010 JBoss Inc
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

package org.kie.uberfire.client.tables;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.kie.uberfire.shared.preferences.GridColumnPreference;
import org.kie.uberfire.shared.preferences.GridPreferencesStore;


public class ColumnPicker<T> {

    private final DataGrid<T> dataGrid;
    private final List<ColumnMeta<T>> columnMetaList = new ArrayList<ColumnMeta<T>>();
    private final PopupPanel popup = new PopupPanel( true );
    
    private GridPreferencesStore gridPreferences;
    private List<ColumnChangedHandler> columnChangedHandler = new ArrayList<ColumnChangedHandler>();
    
     public ColumnPicker( DataGrid<T> dataGrid, GridPreferencesStore gridPreferences ) {
        this.dataGrid = dataGrid;
        this.gridPreferences = gridPreferences;
    }
   
    
    public ColumnPicker( DataGrid<T> dataGrid ) {
        this.dataGrid = dataGrid;
    }
    
    public void addColumnChangedHandler(ColumnChangedHandler handler){
      columnChangedHandler.add(handler);
    }


    public void addColumn( final Column<T, ?> column,
                           final Header<String> header,
                           final boolean visible ) {
        addColumn( new ColumnMeta<T>( column,
                                      header,
                                      visible ) );
    }

    public void setGridPreferencesStore(GridPreferencesStore gridPreferences) {
      this.gridPreferences = gridPreferences;
    }

    private void addColumn( final ColumnMeta<T> columnMeta ) {
        boolean columnPreferenceFound = false;
        int index = -1;
        boolean onInitConf = false;
        boolean noConfigurations = false;
        if(gridPreferences != null){  
          List<GridColumnPreference> columnPreferences = gridPreferences.getColumnPreferences();
          if(!columnPreferences.isEmpty()){
            Collections.sort(columnPreferences);

            for(GridColumnPreference gcp : gridPreferences.getColumnPreferences()){
              if(gcp.getName().equals(columnMeta.getHeader().getValue())){
                columnPreferenceFound = true;
                columnMeta.setVisible(true);
                if(gcp.getWidth() != null){
                  dataGrid.setColumnWidth(columnMeta.getColumn(),gcp.getWidth());
                }else{
                  dataGrid.setColumnWidth(columnMeta.getColumn(), 100, Style.Unit.PCT);
                }
                index = gcp.getPosition();
              }

            }

          } else if(gridPreferences.getGlobalPreferences() != null){
            if(gridPreferences.getGlobalPreferences().getInitialColumns().contains(columnMeta.getHeader().getValue())){
              columnMeta.setVisible(true);
              onInitConf = true;
            }
          } else{
              columnMeta.setVisible(true);
              noConfigurations = true;
          }
        }else{
          columnMeta.setVisible(true);
          noConfigurations = true;
        }
        
        if(!columnPreferenceFound && !onInitConf && !noConfigurations){
            columnMeta.setVisible(false);
            columnMetaList.add( columnMeta );
          }else if (onInitConf || noConfigurations){
            columnMetaList.add( columnMeta );
          }else{
            columnMetaList.add(index, columnMeta );
          }
        if ( columnMeta.isVisible()) {
            dataGrid.addColumn( columnMeta.getColumn(),
                                columnMeta.getHeader() );
        }
    }

    public Button createToggleButton() {
        final Button button = new Button();
        button.setToggle( true );
        button.setIcon( IconType.LIST_UL );

        popup.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        popup.addAutoHidePartner( button.getElement() );
        popup.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
                if ( popupPanelCloseEvent.isAutoClosed() ) {
                    button.setActive( false );
                }
            }
        } );

        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( !button.isActive() ) {
                    showColumnPickerPopup( button.getAbsoluteLeft(),
                                           button.getAbsoluteTop() + button.getOffsetHeight() );
                } else {
                    popup.hide( false );
                }
            }
        } );
        return button;
    }

    private void showColumnPickerPopup( final int left,
                                        final int top ) {
        VerticalPanel popupContent = new VerticalPanel();
        
        
        for ( final ColumnMeta<T> columnMeta : columnMetaList ) {
          
              final CheckBox checkBox = new CheckBox( columnMeta.getHeader().getValue() );
              
              checkBox.setValue( columnMeta.isVisible() );
              checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                  public void onValueChange( ValueChangeEvent<Boolean> booleanValueChangeEvent ) {
                      boolean visible = booleanValueChangeEvent.getValue();
                      if ( visible ) {
                          dataGrid.insertColumn( getVisibleColumnIndex( columnMeta ),
                                                 columnMeta.getColumn(),
                                                 columnMeta.getHeader() );
                      } else {
                          dataGrid.removeColumn( columnMeta.getColumn() );
                      }
                      columnMeta.setVisible( visible );
                      adjustColumnWidths();
                  }
              } );
              
              if(gridPreferences != null && gridPreferences.getGlobalPreferences()
                  .getBannedColumns().contains(columnMeta.getHeader().getValue())){
                // do not add if it is banned
              }else if (gridPreferences == null || !gridPreferences.getGlobalPreferences()
                  .getBannedColumns().contains(columnMeta.getHeader().getValue())){
                popupContent.add( checkBox );
              }
          
        }
        if(gridPreferences!= null){
          Button resetButton = new Button("Reset");
          resetButton.setSize(ButtonSize.MINI);
          resetButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
              gridPreferences.resetGridColumnPreferences();
              for(ColumnChangedHandler handler : columnChangedHandler){
               handler.beforeColumnChanged();
              }
              showColumnPickerPopup(left, top);
            }
          });

          popupContent.add(resetButton);
        }
        popup.setWidget( popupContent );
        popup.setPopupPosition( left,
                                top );
        popup.show();
    }
    
   public List<GridColumnPreference> getColumnsState() {
    List<GridColumnPreference> state = new ArrayList<GridColumnPreference>();
    for (final ColumnMeta<T> cm : columnMetaList) {
      if (cm.isVisible()) {
        state.add(new GridColumnPreference(cm.getHeader().getValue(),
                        dataGrid.getColumnIndex(cm.getColumn()),  
                        dataGrid.getColumnWidth(cm.getColumn())));
      }
    }
    return state;
  }
  

    private int getVisibleColumnIndex( final ColumnMeta<T> columnMeta ) {
        int index = 0;
        for ( final ColumnMeta<T> cm : columnMetaList ) {
            if ( cm.equals( columnMeta ) ) {
                return index;
            }
            if ( cm.isVisible() ) {
                index++;
            }
        }
        return index;
    }

   private void adjustColumnWidths() {
     for(ColumnChangedHandler handler : columnChangedHandler){
          handler.afterColumnChanged();
     }
     
    int totalVisibleColumnsCount = 0;
    for (ColumnMeta<T> cm : columnMetaList) {
      if (cm.isVisible()) {
        totalVisibleColumnsCount++;
      }
    }
    if (totalVisibleColumnsCount == 0) {
      return;
    }
    int counter = 0;
    for (ColumnMeta<T> cm : columnMetaList) {
      if (cm.isVisible()) {
        if (totalVisibleColumnsCount == 1) {
          dataGrid.setColumnWidth(cm.getColumn(),
                  100 / totalVisibleColumnsCount,
                  Style.Unit.PCT);
        }
        boolean preferenceFound = false;
        for(GridColumnPreference gcp : getColumnsState()){
          if(gcp.getName().equals(cm.getHeader().getValue())){
            preferenceFound = true;
          }
        }
        
        if(!preferenceFound || dataGrid.getColumnWidth(cm.getColumn()) == null){
          dataGrid.setColumnWidth(cm.getColumn(),
                  100 / totalVisibleColumnsCount,
                  Style.Unit.PCT);
        }
        counter ++;
        if (counter == totalVisibleColumnsCount) {
          dataGrid.setColumnWidth(cm.getColumn(),100,
                  Style.Unit.PCT);
        }
      }
    }
  }

    protected void columnMoved( final int visibleFromIndex,
                                final int visibleBeforeIndex ) {
        int visibleColumnFromIndex = 0;
        ColumnMeta<T> columnMetaToMove = null;
        for ( int i = 0; i < columnMetaList.size(); i++ ) {
            final ColumnMeta<T> columnMeta = columnMetaList.get( i );
            if ( columnMeta.isVisible() ) {
                if ( visibleFromIndex == visibleColumnFromIndex ) {
                    columnMetaToMove = columnMeta;
                    break;
                }
                visibleColumnFromIndex++;
            }
        }
        if ( columnMetaToMove == null ) {
            return;
        }

        columnMetaList.remove( columnMetaToMove );

        boolean columnInserted = false;
        int visibleColumnBeforeIndex = 0;
        for ( int i = 0; i < columnMetaList.size(); i++ ) {
            final ColumnMeta<T> columnMeta = columnMetaList.get( i );
            if ( columnMeta.isVisible() ) {
                if ( visibleBeforeIndex == visibleColumnBeforeIndex ) {
                    columnMetaList.add( i,
                                        columnMetaToMove );
                    columnInserted = true;
                    break;
                }
                visibleColumnBeforeIndex++;
            }
        }
        if ( !columnInserted ) {
            columnMetaList.add( columnMetaToMove );
        }
    }

    private static class ColumnMeta<T>{

        private Column<T, ?> column;
        private Header<String> header;
        private boolean visible;

        private ColumnMeta( Column<T, ?> column,
                            Header<String> header,
                            boolean visible ) {
            this.column = column;
            this.header = header;
            this.visible = visible;
        }

        public Column<T, ?> getColumn() {
            return column;
        }

        public Header<String> getHeader() {
            return header;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible( boolean visible ) {
            this.visible = visible;
        }

    }
}
