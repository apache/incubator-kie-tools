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
package org.uberfire.ext.widgets.common.client.colorpicker.canvas;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class Canvas extends Composite implements HasAllMouseHandlers {
	private HTML html;
	private Element canvas;

	public Canvas() {
		html = new HTML("<canvas></canvas>");
		initWidget(html);
		canvas = (Element) html.getElement().getFirstChild();
	}
	
	public native RenderingContext getContext() /*-{
		return this.@org.uberfire.ext.widgets.common.client.colorpicker.canvas.Canvas::canvas.getContext("2d");
	}-*/;

	public void setCanvasSize(int width, int height) {
		DOM.setElementPropertyInt(canvas, "width", width);
		DOM.setElementPropertyInt(canvas, "height", height);
	}

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return html.addMouseDownHandler(handler);
	}
	
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return html.addMouseUpHandler(handler);
	}
	
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return html.addMouseOverHandler(handler);
	}
	
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return html.addMouseOutHandler(handler);
	}
	
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return html.addMouseMoveHandler(handler);
	}
	
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return html.addMouseWheelHandler(handler);
	}
}
