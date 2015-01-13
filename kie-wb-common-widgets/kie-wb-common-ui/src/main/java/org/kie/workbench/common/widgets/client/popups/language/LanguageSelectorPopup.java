/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.kie.workbench.common.widgets.client.popups.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class LanguageSelectorPopup extends BaseModal {
	private static AddNewKBasePopupViewImplBinder uiBinder = GWT.create( AddNewKBasePopupViewImplBinder.class );

	@UiField
    ListBox listItems;
    
	private Map<String,String> languageMap = new HashMap<String,String>();
	
    interface AddNewKBasePopupViewImplBinder extends UiBinder<Widget, LanguageSelectorPopup> {

    }

    public LanguageSelectorPopup() {
    	setTitle( CommonConstants.INSTANCE.Language_Selector() );

        add( uiBinder.createAndBindUi( this ) );
        add( new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                onOk();
                hide();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        ) );
        languageMap.put("default", CommonConstants.INSTANCE.English());
        languageMap.put("zh_CN", CommonConstants.INSTANCE.Chinese());
        languageMap.put("de", CommonConstants.INSTANCE.German());
        languageMap.put("es", CommonConstants.INSTANCE.Spanish());
        languageMap.put("fr", CommonConstants.INSTANCE.French());
        languageMap.put("ja", CommonConstants.INSTANCE.Japanese());
        languageMap.put("pt_BR", CommonConstants.INSTANCE.Portuguese());
        setItems(getAllSupportLanguage());
        
    }


    private void setItems(List<Pair<String, String>> items) {
		 listItems.clear();
	        for ( Pair<String, String> item : items ) {
	            listItems.addItem( item.getK1(),
	                               item.getK2() );
	        }
	}
    
    private Pair<String, String> getSelectedItem() {
		final int selectedIndex = listItems.getSelectedIndex();
        if ( selectedIndex == -1 ) {
            return Pair.newPair( "", "" );
        }
        final String text = listItems.getItemText( selectedIndex );
        final String value = listItems.getValue( selectedIndex );
        return Pair.newPair( text, value );
	}
    
    public void onOk(){
    	 final Pair<String, String> selectedItem = getSelectedItem();
         if ( selectedItem != null && !selectedItem.getK2().isEmpty() ) {
        	 setCurrentLanguage(selectedItem.getK2());
         } else {
             showFieldEmptyWarning();
         }
    }

    private List<Pair<String, String>> getAllSupportLanguage(){
    	String[] languages = LocaleInfo.getAvailableLocaleNames();
    	List<Pair<String, String>> allSupportLanguage = new ArrayList<Pair<String, String>>(languages.length);
    	for(String language:languages){
    		Pair<String, String> languagePair = new Pair<String, String>(languageMap.get(language),language);
    		allSupportLanguage.add(languagePair);    		
    	}
    	return allSupportLanguage;
    	
    }
    
    private void setCurrentLanguage(String languageName){
    	Window.Location.assign( 
       		   Window.Location.createUrlBuilder().removeParameter(LocaleInfo.getLocaleQueryParam())
       	       .setParameter(LocaleInfo.getCurrentLocale().getLocaleQueryParam(), languageName)
       		       .buildString());
    }
    
    private void showFieldEmptyWarning() {
		ErrorPopup.showMessage( CommonConstants.INSTANCE.PleaseSetAName() );		
	}
}
