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

import com.google.gwt.core.client.JavaScriptObject;

public class RenderingContext extends JavaScriptObject {
	protected RenderingContext() {}
	
	public final native void setFillStyle(String style) /*-{
		this.fillStyle = style;
	}-*/;

	public final native void setFillStyle(Gradient gradient) /*-{
		this.fillStyle = gradient;
	}-*/;

	public final native void fillRect(int x, int y, int width, int height) /*-{
		this.fillRect(x, y, width, height);
	}-*/;
	
	public final native void beginPath() /*-{
		this.beginPath();
	}-*/;

	public final native void closePath() /*-{
		this.closePath();
	}-*/;

	public final native void fill() /*-{
		this.fill();
	}-*/;

	public final native void moveTo(int x, int y) /*-{
		this.moveTo(x, y);
	}-*/;

	public final native void lineTo(int x, int y) /*-{
		this.lineTo(x, y);
	}-*/;

	public final native void arc(int x, int y, double radius, double startAngle, double endAngle, boolean counterClockwise) /*-{
		this.arc(x, y, radius, startAngle, endAngle, counterClockwise);
	}-*/;

	public final native Gradient createLinearGradient(int x1, int y1, int x2, int y2) /*-{
		return this.createLinearGradient(x1, y1, x2, y2);
	}-*/;

	public final native ImageData getImageData(int x, int y, int width, int height) /*-{
		return this.getImageData(x, y, width, height);
	}-*/;
}
