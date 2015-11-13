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

package org.uberfire.ext.widgets.common.client.ace;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represents a cursor position.
 */
public class AceEditorCursorPosition {
	private final int row, column;
	
	/**
	 * Constructor.
	 * 
	 * @param row     row (0 for first row)
	 * @param column  column (0 for first column)
	 */
	public AceEditorCursorPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	/**
	 * @return the row (0 for first row)
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * @return the column (0 for first column)
	 */
	public int getColumn() {
		return column;
	}
	
	@Override
	public String toString() {
		return row + ":" + column;
	}
	
	/**
	 * Static creation method.
	 * This is handy for calling from JSNI code.
	 * 
	 * @param row     the row
	 * @param column  the column
	 * @return the {@link AceEditorCursorPosition}
	 */
	public static AceEditorCursorPosition create(int row, int column) {
		return new AceEditorCursorPosition(row, column);
	}
	
	/**
	 * Convert to a native Ace JavaScript position object
	 * (with integer-valued <code>row</code> and <code>column</code> fields.)
	 * 
	 * @return native Ace JavaScript position object
	 */
	public native JavaScriptObject toJsObject() /*-{
		return {
			row: this.@org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition::row,
			column: this.@org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition::column
		};
	}-*/;
}
