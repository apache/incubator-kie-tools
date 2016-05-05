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

import com.google.gwt.event.shared.GwtEvent;

public class ColorChangedEvent extends GwtEvent<ColorChangedHandler> {
	private static Type<ColorChangedHandler> TYPE;

	private String color;

	ColorChangedEvent(String color) {
		this.color = color;
	}

	public static Type<ColorChangedHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ColorChangedHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ColorChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ColorChangedHandler handler) {
		handler.colorChanged(this);
	}

	public String getColor() {
		return color;
	}
}
