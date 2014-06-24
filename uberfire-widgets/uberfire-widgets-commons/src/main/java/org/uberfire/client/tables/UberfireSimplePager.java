/*
 * Copyright 2011 JBoss Inc
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

package org.uberfire.client.tables;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import org.uberfire.client.resources.UberfireSimplePagerResources;
import org.uberfire.client.resources.UberfireSimplePagerResources;

/**
 * Essentially a fork of GWT's SimplePager that maintains a set page size and
 * displays page numbers and total pages more elegantly. SimplePager will ensure
 * <code>pageSize</code> rows are always rendered even if the "last" page has
 * less than <code>pageSize</code> rows remain. Forked not sub-classed as
 * GWTs code is largely private and not open to extension :(
 */
public class UberfireSimplePager extends AbstractPager {

    /**
     * Styles used by this widget.
     */
    public static interface Style
            extends
            CssResource {

        /**
         * Applied to buttons.
         */
        String button();

        /**
         * Applied to disabled buttons.
         */
        String disabledButton();

        /**
         * Applied to the details text.
         */
        String pageDetails();
    }

    /**
     * The location of the text relative to the paging buttons.
     */
    public static enum TextLocation {
        CENTER, LEFT, RIGHT
    }

    /**
     * An {@link Image} that acts as a button.
     */
    private static class ImageButton extends Image {

        private boolean disabled;
        private final ImageResource resDisabled;
        private final ImageResource resEnabled;
        private final String styleDisabled;

        public ImageButton( ImageResource resEnabled,
                            ImageResource resDiabled,
                            String disabledStyle ) {
            super( resEnabled );
            this.resEnabled = resEnabled;
            this.resDisabled = resDiabled;
            this.styleDisabled = disabledStyle;
        }

        public boolean isDisabled() {
            return disabled;
        }

        @Override
        public void onBrowserEvent( Event event ) {
            // Ignore events if disabled.
            if ( disabled ) {
                return;
            }

            super.onBrowserEvent( event );
        }

        public void setDisabled( boolean isDisabled ) {
            if ( this.disabled == isDisabled ) {
                return;
            }

            this.disabled = isDisabled;
            if ( disabled ) {
                setResource( resDisabled );
                getElement().getParentElement().addClassName( styleDisabled );
            } else {
                setResource( resEnabled );
                getElement().getParentElement().removeClassName( styleDisabled );
            }
        }
    }

    private static int DEFAULT_FAST_FORWARD_ROWS = 100;
    private static UberfireSimplePagerResources DEFAULT_RESOURCES;

    private static UberfireSimplePagerResources getDefaultResources() {
        if ( DEFAULT_RESOURCES == null ) {
            DEFAULT_RESOURCES = UberfireSimplePagerResources.INSTANCE;
        }
        return DEFAULT_RESOURCES;
    }

    private final ImageButton fastForward;

    private final int fastForwardRows;

    private final ImageButton firstPage;

    /**
     * We use an {@link HTML} so we can embed the loading image.
     */
    private final HTML label = new HTML();

    private final ImageButton lastPage;
    private final ImageButton nextPage;
    private final ImageButton prevPage;

    /**
     * The {@link UberfireSimplePagerResources} used by this widget.
     */
    private final UberfireSimplePagerResources resources;

    /**
     * The {@link Style} used by this widget.
     */
    private final Style style;

    //Page size is normally derieved from the visibleRange
    private int pageSize = 10;

    /**
     * Construct a {@link SimplePager} with the default text location.
     */
    public UberfireSimplePager() {
        this( TextLocation.CENTER );
    }

    /**
     * Construct a {@link SimplePager} with the specified text location.
     * @param location the location of the text relative to the buttons
     */
    public UberfireSimplePager( TextLocation location ) {
        this( location,
              getDefaultResources(),
              true,
              DEFAULT_FAST_FORWARD_ROWS,
              true );
    }

    /**
     * Construct a {@link SimplePager} with the specified resources.
     * @param location the location of the text relative to the buttons
     * @param resources the {@link UberfireSimplePagerResources} to use
     * @param showFastForwardButton if true, show a fast-forward button that advances by a larger
     * increment than a single page
     * @param fastForwardRows the number of rows to jump when fast forwarding
     * @param showLastPageButton if true, show a button to go the the last page
     */
    public UberfireSimplePager( TextLocation location,
                                UberfireSimplePagerResources resources,
                                boolean showFastForwardButton,
                                final int fastForwardRows,
                                boolean showLastPageButton ) {
        this.resources = resources;
        this.fastForwardRows = fastForwardRows;
        this.style = resources.simplePagerStyle();
        this.style.ensureInjected();

        // Create the buttons.
        String disabledStyle = style.disabledButton();
        firstPage = new ImageButton( resources.simplePagerFirstPage(),
                                     resources.simplePagerFirstPageDisabled(),
                                     disabledStyle );
        firstPage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                firstPage();
            }
        } );
        nextPage = new ImageButton( resources.simplePagerNextPage(),
                                    resources.simplePagerNextPageDisabled(),
                                    disabledStyle );
        nextPage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                nextPage();
            }
        } );
        prevPage = new ImageButton( resources.simplePagerPreviousPage(),
                                    resources.simplePagerPreviousPageDisabled(),
                                    disabledStyle );
        prevPage.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                previousPage();
            }
        } );
        if ( showLastPageButton ) {
            lastPage = new ImageButton( resources.simplePagerLastPage(),
                                        resources.simplePagerLastPageDisabled(),
                                        disabledStyle );
            lastPage.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    lastPage();
                }
            } );
        } else {
            lastPage = null;
        }
        if ( showFastForwardButton ) {
            fastForward = new ImageButton( resources.simplePagerFastForward(),
                                           resources.simplePagerFastForwardDisabled(),
                                           disabledStyle );
            fastForward.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    setPage( getPage() + getFastForwardPages() );
                }
            } );
        } else {
            fastForward = null;
        }

        // Construct the widget.
        HorizontalPanel layout = new HorizontalPanel();
        layout.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        initWidget( layout );
        if ( location == TextLocation.RIGHT ) {
            layout.add( label );
        }
        layout.add( firstPage );
        layout.add( prevPage );
        if ( location == TextLocation.CENTER ) {
            layout.add( label );
        }
        layout.add( nextPage );
        if ( showFastForwardButton ) {
            layout.add( fastForward );
        }
        if ( showLastPageButton ) {
            layout.add( lastPage );
        }
        if ( location == TextLocation.LEFT ) {
            layout.add( label );
        }

        // Add style names to the cells.
        firstPage.getElement().getParentElement().addClassName( style.button() );
        prevPage.getElement().getParentElement().addClassName( style.button() );
        label.getElement().getParentElement().addClassName( style.pageDetails() );
        nextPage.getElement().getParentElement().addClassName( style.button() );
        if ( showFastForwardButton ) {
            fastForward.getElement().getParentElement().addClassName( style.button() );
        }
        if ( showLastPageButton ) {
            lastPage.getElement().getParentElement().addClassName( style.button() );
        }

        // Disable the buttons by default.
        setDisplay( null );
    }

    // We want pageSize to remain constant
    @Override
    public int getPageSize() {
        return pageSize;
    }

    // Page forward by an exact size rather than the number of visible
    // rows as is in the norm in the underlying implementation
    @Override
    public void nextPage() {
        if ( getDisplay() != null ) {
            Range range = getDisplay().getVisibleRange();
            setPageStart( range.getStart()
                                  + getPageSize() );
        }
    }

    // Page back by an exact size rather than the number of visible rows
    // as is in the norm in the underlying implementation
    @Override
    public void previousPage() {
        if ( getDisplay() != null ) {
            Range range = getDisplay().getVisibleRange();
            setPageStart( range.getStart()
                                  - getPageSize() );
        }
    }

    @Override
    public void setDisplay( HasRows display ) {
        // Enable or disable all buttons.
        boolean disableButtons = ( display == null );
        setFastForwardDisabled( disableButtons );
        setNextPageButtonsDisabled( disableButtons );
        setPrevPageButtonsDisabled( disableButtons );
        super.setDisplay( display );
    }

    @Override
    public void setPage( int index ) {
        super.setPage( index );
    }

    @Override
    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;
        super.setPageSize( pageSize );
    }

    // Override so the last page is shown with a number of rows less
    // than the pageSize rather than always showing the pageSize number
    // of rows and possibly repeating rows on the last and penultimate
    // page
    @Override
    public void setPageStart( int index ) {
        if (getDisplay() != null) {
          Range range = getDisplay().getVisibleRange();
          int displayPageSize = range.getLength();
          if (!isRangeLimited() && getDisplay().isRowCountExact()) {
            index = Math.min(index, getDisplay().getRowCount() - displayPageSize);
          }
          index = Math.max(0, index);
          if (index != range.getStart()) {
            getDisplay().setVisibleRange(index, displayPageSize);
          }
        }  
    }

    /**
     * Let the page know that the table is loading. Call this method to clear
     * all data from the table and hide the current range when new data is being
     * loaded into the table.
     */
    public void startLoading() {
        getDisplay().setRowCount( 0,
                                  true );
        label.setHTML( "" );
    }

    /**
     * Get the number of pages to fast forward based on the current page size.
     * @return the number of pages to fast forward
     */
    private int getFastForwardPages() {
        int pageSize = getPageSize();
        return pageSize > 0 ? fastForwardRows / pageSize : 0;
    }

    /**
     * Enable or disable the fast forward button.
     * @param disabled true to disable, false to enable
     */
    private void setFastForwardDisabled( boolean disabled ) {
        if ( fastForward == null ) {
            return;
        }
        if ( disabled ) {
            fastForward.setResource( resources.simplePagerFastForwardDisabled() );
            fastForward.getElement().getParentElement().addClassName(
                    style.disabledButton() );
        } else {
            fastForward.setResource( resources.simplePagerFastForward() );
            fastForward.getElement().getParentElement().removeClassName(
                    style.disabledButton() );
        }

        //The one line change to GWT's SimplePager code!
        fastForward.setDisabled( disabled );
    }

    /**
     * Enable or disable the next page buttons.
     * @param disabled true to disable, false to enable
     */
    private void setNextPageButtonsDisabled( boolean disabled ) {
        nextPage.setDisabled( disabled );
        if ( lastPage != null ) {
            lastPage.setDisabled( disabled );
        }
    }

    /**
     * Enable or disable the previous page buttons.
     * @param disabled true to disable, false to enable
     */
    private void setPrevPageButtonsDisabled( boolean disabled ) {
        firstPage.setDisabled( disabled );
        prevPage.setDisabled( disabled );
    }

    // Override to display "0 of 0" when there are no records (otherwise
    // you get "1-1 of 0") and "1 of 1" when there is only one record
    // (otherwise you get "1-1 of 1"). Not internationalised (but
    // neither is SimplePager)
    protected String createText() {
        NumberFormat formatter = NumberFormat.getFormat( "#,###" );
        HasRows display = getDisplay();
        Range range = display.getVisibleRange();
        int pageStart = range.getStart() + 1;
        int pageSize = range.getLength();
        int dataSize = display.getRowCount();
        int endIndex = Math.min( dataSize,
                                 pageStart
                                         + pageSize
                                         - 1 );
        endIndex = Math.max( pageStart,
                             endIndex );
        boolean exact = display.isRowCountExact();
        if ( dataSize == 0 ) {
            return "0 of 0";
        } else if ( pageStart == endIndex ) {
            return formatter.format( pageStart )
                    + " of "
                    + formatter.format( dataSize );
        }
        return formatter.format( pageStart )
                + "-"
                + formatter.format( endIndex )
                + ( exact ? " of " : " of over " )
                + formatter.format( dataSize );
    }

    @Override
    protected void onRangeOrRowCountChanged() {
        HasRows display = getDisplay();
        label.setText( createText() );

        // Update the prev and first buttons.
        setPrevPageButtonsDisabled( !hasPreviousPage() );

        // Update the next and last buttons.
        if ( isRangeLimited() || !display.isRowCountExact() ) {
            setNextPageButtonsDisabled( !hasNextPage() );
            setFastForwardDisabled( !hasNextPages( getFastForwardPages() ) );
        }
    }

    /**
     * Check if the next button is disabled. Visible for testing.
     */
    boolean isNextButtonDisabled() {
        return nextPage.isDisabled();
    }

    /**
     * Check if the previous button is disabled. Visible for testing.
     */
    boolean isPreviousButtonDisabled() {
        return prevPage.isDisabled();
    }

}
