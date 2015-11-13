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
package org.uberfire.ext.widgets.common.client.colorpicker.dialog;

import com.google.gwt.event.shared.GwtEvent;

public class DialogClosedEvent extends GwtEvent<DialogClosedHandler> {
	private static Type<DialogClosedHandler> TYPE;

	private boolean canceled;

	DialogClosedEvent(boolean canceled) {
		this.canceled = canceled;
	}

	public static Type<DialogClosedHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<DialogClosedHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<DialogClosedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DialogClosedHandler handler) {
		handler.dialogClosed(this);
	}
	
	public boolean isCanceled() {
		return canceled;
	}
}
