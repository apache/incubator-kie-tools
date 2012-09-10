/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.uberfire.client.editors.gadget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Jeff Yu
 * @date: 28/02/12
 */
public class PortalLayout extends Composite {

    private int m_column;

    private FlowPanel portalPanel;
    
    private String portalId;
    
    private List<FlowPanel> columnPanel;
    
    public static final int THREE_COLUMN_WIDTH = 325;
    public static final int TWO_COLUMN_WIDTH = 480;
    public static final int ONE_COLUMN_WIDTH = 980;
    
    private int columnWidth = TWO_COLUMN_WIDTH;

    public PortalLayout(String pid) {
        portalPanel = new FlowPanel();
        this.portalId = "portal-" + pid;
        portalPanel.getElement().addClassName("portal");
        portalPanel.getElement().setId(portalId);
        initWidget(portalPanel);
    }

    public PortalLayout(String portalId,int column) {
        this(portalId);
        this.m_column = column;
        columnPanel = new ArrayList<FlowPanel>(column);
        if (column == 3) {
            columnWidth = THREE_COLUMN_WIDTH;
        } else if (column == 1) {
            columnWidth = ONE_COLUMN_WIDTH;
        }

        for (int i = 0; i < column; i++) {
           FlowPanel cpanel = new FlowPanel();
           cpanel.getElement().addClassName("column");
           cpanel.setWidth(columnWidth + "px");
           columnPanel.add(cpanel);
           portalPanel.add(cpanel);
        }
    }
    
    public int getPortletWidth() {
    	return columnWidth;
    }
    
    
    public String getPortalId() {
    	return this.portalId;
    }

    public void addPortlet(int i, Widget portlet) {
        columnPanel.get(i).add(portlet);
    }
    
    /**
     * This is for getting the portal's height.
     */
    public void addClosingDiv() {
    	portalPanel.add(new HTML("<div style='clear:both'></div>"));
    }

    @Override
    public void onAttach() {
        super.onAttach();
        sortableScript();
    }

    /**
     * JSNI methods
     */
    private static native void sortableScript() /*-{
        $wnd.$(".column").sortable({
            connectWith: ".column"
        });
    }-*/;
}
