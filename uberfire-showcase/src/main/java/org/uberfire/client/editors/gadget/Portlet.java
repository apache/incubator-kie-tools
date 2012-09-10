/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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

import java.util.ArrayList;
import java.util.List;

import org.uberfire.shared.gadget.UserPreference;
import org.uberfire.shared.gadget.UserPreference.Option;
import org.uberfire.shared.gadget.UserPreference.UserPreferenceSetting;
import org.uberfire.shared.gadget.WidgetModel;

/*import org.overlord.gadgets.web.client.URLBuilder;
import org.overlord.gadgets.web.client.util.RestfulInvoker;
import org.overlord.gadgets.web.shared.dto.UserPreference;
import org.overlord.gadgets.web.shared.dto.UserPreference.Option;
import org.overlord.gadgets.web.shared.dto.UserPreference.UserPreferenceSetting;
import org.overlord.gadgets.web.shared.dto.WidgetModel;

import com.allen_sauer.gwt.log.client.Log;*/
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author: Jeff Yu
 * @date: 2/03/12
 */
public class Portlet extends Composite {

    interface PortletUiBinder extends UiBinder<Widget, Portlet> {}

    private static PortletUiBinder uiBinder = GWT.create(PortletUiBinder.class);
    
    private String id;

    private String widgetId;
    
    //This is the portalLayout Id.
    private String portalId;
    
    private WidgetModel wmodel;
    
    private List<Widget> prefs = new ArrayList<Widget>();

    @UiField InlineLabel minBtn;
    @UiField InlineLabel title;
    @UiField InlineLabel removeBtn;
    @UiField InlineLabel maxBtn;
    @UiField InlineLabel settingBtn;
    @UiField InlineLabel restoreBtn;
    @UiField FlowPanel userPreference;
    @UiField FlowPanel portletContent;
    @UiField Frame gadgetSpec;
    @UiField FlexTable prefTable;

    private Portlet(final String wid, final String pid) {
        widgetId = wid;
        id = "portlet-" + widgetId;
        this.portalId = pid;
        initWidget(uiBinder.createAndBindUi(this));
        getElement().setId(id);

        minBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                 toggle(id);
            }
        });

        removeBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
/*                String theURL =  URLBuilder.getRemoveWidgetURL(Long.valueOf(widgetId));
                RestfulInvoker.invoke(RequestBuilder.POST, theURL,
                        null, new RestfulInvoker.Response() {

                    public void onResponseReceived(Request request, Response response) {
                        remove(id);
                    }
                });*/
            }
        });
        
        maxBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				maximizeWindow(id, portalId);
				hideUserPref(id);
				showRestoreButton(id);
				gadgetSpec.setWidth("100%");
				gadgetSpec.setHeight("90%");
				gadgetSpec.getElement().setAttribute("scrolling", "auto");
				gadgetSpec.setUrl("http://localhost:8080/gadget-server/gadgets/ifr?url=" + wmodel.getSpecUrl() + "?" + getCanvasView());
			}        	
        });
        
        restoreBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				restoreWindow(id);
				hideRestoreButton(id);
				gadgetSpec.setWidth("100%");
				gadgetSpec.setHeight("250px");
				gadgetSpec.getElement().setAttribute("scrolling", "no");
				gadgetSpec.setUrl("http://localhost:8080/gadget-server/gadgets/ifr?url=" + wmodel.getSpecUrl() + "?" + getHomeView());
			}        	
        });
        
        settingBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				showUserPref(id);
			}        	
        });

        gadgetSpec.getElement().setId(widgetId);
    }

    public Portlet(WidgetModel model, int width, String portalId) {
        this(String.valueOf(model.getWidgetId()), portalId);
        title.setText(model.getName());
        wmodel = model;
        
        generateUserPref(model);
        
        gadgetSpec.getElement().setAttribute("scrolling", "no");
        gadgetSpec.getElement().setAttribute("frameborder", "0");
        gadgetSpec.setWidth(width - 20 + "px");
        gadgetSpec.setHeight("250px");
        gadgetSpec.setUrl("http://localhost:8080/gadget-server/gadgets/ifr?url=" + model.getSpecUrl() + "?" + getHomeView());
    }
    
    
    private void generateUserPref(WidgetModel model) {
    	UserPreference pref = model.getUserPreference();
    	int row = 0;
    	for (UserPreferenceSetting prefSet : pref.getData()) { 		
    		if (UserPreference.Type.STRING.equals(prefSet.getType())) {
    			prefTable.setWidget(row, 0, new Label(prefSet.getDisplayName()));
    			Widget textBox = createTextBox(prefSet.getName(), prefSet.getDefaultValue());
    			prefs.add(textBox);
    			prefTable.setWidget(row, 1, textBox);
    		} else if (UserPreference.Type.ENUM.equals(prefSet.getType())) {
    			prefTable.setWidget(row, 0, new Label(prefSet.getDisplayName()));
    			Widget listBox = createSelectBox(prefSet.getName(), prefSet.getDefaultValue(), prefSet.getEnumOptions());
    			prefs.add(listBox);
    			prefTable.setWidget(row, 1, listBox);
    		}
    		row ++;
    	}
    	prefTable.setWidget(row, 1, createPrefSettingButtons());
    	
    }
    
    private Widget createTextBox(String name, String defaultVal) {
    	TextBox textBox = new TextBox();
    	textBox.setName(name);
    	textBox.setValue(defaultVal);
    	return textBox;
    }
    
    private Widget createSelectBox(String name, String defaultVal, List<Option> options) {
    	ListBox listBox = new ListBox(false);
    	listBox.setName(name);
    	for (Option option : options) {
    		listBox.addItem(option.getValue());
    	}
    	int index = 0;
    	for (int i = 0; i < options.size(); i ++) {
    		if (options.get(i).getValue().equals(defaultVal)) {
    			index = i;
    		}
    	}
    	listBox.setSelectedIndex(index);   	
    	return listBox;
    }
    
    private Widget createPrefSettingButtons() {
    	HorizontalPanel btnPanel = new HorizontalPanel();
    	Button saveBtn = new Button("Save");
    	Button cancelBtn = new Button("Cancel");
    	btnPanel.add(saveBtn);
    	saveBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				StringBuffer sbuffer = new StringBuffer();
				sbuffer.append("{");
				
				for (int i = 0; i < prefs.size(); i++) {
					Widget theWidget = prefs.get(i);
					String name = null;
					String value = null;
					if (theWidget instanceof TextBox) {
						TextBox tbox = (TextBox)theWidget;
						name = tbox.getName();
						value = tbox.getValue();
					} else if (theWidget instanceof ListBox) {
						ListBox lbox = (ListBox)theWidget;
						name = lbox.getName();
						value = lbox.getValue(lbox.getSelectedIndex());
					}
					if (name != null) {
						sbuffer.append("\"" + name + "\":\"" + value + "\"");
						if (i < prefs.size() -1) {
							sbuffer.append(",");
						}
					}
				}
				sbuffer.append("}");
				//Log.debug("The map value is: " + sbuffer.toString());
				
/*				RestfulInvoker.invoke(RequestBuilder.POST, URLBuilder.updatePreferenceURL(Long.valueOf(widgetId)), sbuffer.toString(), new RestfulInvoker.Response(){
					public void onResponseReceived(Request request, Response response) {
						hideUserPref(id);
						gadgetSpec.setUrl("http://localhost:8080/gadget-server/gadgets/ifr?url=" + wmodel.getSpecUrl() + "?" + getHomeView());
					}
					
				});*/
				
			}  		
    	});
    	btnPanel.add(cancelBtn);
    	cancelBtn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				hideUserPref(id);
			}   		
    	});
    	return btnPanel;
    }
    
    public String getHomeView() {
    	return "view=home";
    }
    
    public String getCanvasView() {
    	return "view=canvas";
    }

    @Override
    public void onAttach() {
        super.onAttach();
        hideRestoreButton(id);
        hideUserPref(id);
    }

    /**
     * JSNI methods
     */
    private static native void toggle(String id) /*-{
        $wnd.$('#' + id).find(".portlet-min")
                .toggleClass( "ui-icon-triangle-1-s" )
                .toggleClass( "ui-icon-triangle-1-n" );

        $wnd.$('#' + id).find(".portlet-content").toggle();
    }-*/;

    private static native void remove(String id) /*-{
        $wnd.$('#' + id).remove();
    }-*/;

    private static native void showUserPreference(String id) /*-{
        $wnd.$('#' + id).find(".portlet-menu").show();
    }-*/;
    
    private static native void hideRestoreButton(String id) /*-{
    	$wnd.$('#'+id).find(".portlet-restore").hide();
    	$wnd.$('#'+id).find(".portlet-max").show();
    	$wnd.$('#'+id).find(".portlet-setting").show();
    	$wnd.$('#'+id).find(".portlet-close").show();
    }-*/;
    
    private static native void showRestoreButton(String id) /*-{
    	$wnd.$('#'+id).find(".portlet-restore").show();
    	$wnd.$('#'+id).find(".portlet-max").hide();
    	$wnd.$('#'+id).find(".portlet-setting").hide();
    	$wnd.$('#'+id).find(".portlet-close").hide();
    }-*/;
    
    private static native void maximizeWindow(String id, String portalId) /*-{
	    var overlay = $wnd.$('<div></div>');
	    var jqElm = $wnd.$('#' + portalId);
	    var styleMap = {
	        position: "absolute",
	        height : jqElm.height(),
	        width : jqElm.width(),
	        'z-index': 10,
	        opacity : 0.7,
	        background : "#FFFFFF"
	    };
	    $wnd.$(overlay).css(styleMap);
	    $wnd.$(overlay).addClass("added-overlay");
	    jqElm.prepend(overlay[0]);
		$wnd.$(".column").sortable( "option", "disabled", true);
		$wnd.$('#' + id).removeClass("portlet").addClass("portlet-canvas");
	}-*/;
    
    private static native void restoreWindow(String id) /*-{
		$wnd.$(".added-overlay").remove();
		$wnd.$(".column").sortable( "option", "disabled", false);
		$wnd.$('#' + id).removeClass("portlet-canvas").addClass("portlet");
	}-*/;
    
    private static native void hideUserPref(String id) /*-{
		$wnd.$('#' + id).find(".portlet-preference").hide();
	}-*/;
    
    private static native void showUserPref(String id) /*-{
		$wnd.$('#' + id).find(".portlet-preference").show();
	}-*/; 

}
