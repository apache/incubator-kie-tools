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
package org.uberfire.ext.widgets.common.client.colorpicker;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.colorpicker.dialog.Dialog;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

public class 	ColorPickerDialog extends Dialog {
	private SaturationLightnessPicker slPicker;
	private HuePicker huePicker;
	private String color;

	@Override
	protected Widget createDialogArea() {
		setText(CommonConstants.INSTANCE.ColorPickerTitle());
		
		HorizontalPanel panel = new HorizontalPanel();
		
		// the pickers
		slPicker = new SaturationLightnessPicker();
		panel.add(slPicker);
		huePicker = new HuePicker();
		panel.add(huePicker);

		// bind saturation/lightness picker and hue picker together
		huePicker.addHueChangedHandler(new HueChangedHandler() {
			public void hueChanged(HueChangedEvent event) {
				slPicker.setHue(event.getHue());
			}
		});

		return panel;
	}

	public void setColor(String color) {
		int[] rgb = ColorUtils.getRGB(color);
		int[] hsl = ColorUtils.rgb2hsl(rgb);
		huePicker.setHue(hsl[0]);
		slPicker.setColor(color);
	}
	
	public String getColor() {
		return color;
	}
	
	@Override
	protected void buttonClicked(Widget button) {
		// remember color when "OK" is clicked
		if (button == getOkButton()) {
			color = slPicker.getColor();
		}
		
		close(button == getCancelButton());
	}
}
