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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;

public abstract class DirtyableComposite extends Composite
    implements
    DirtyableWidget {

    protected boolean dirtyflag = false;

    public DirtyableComposite() {
    }

    public void onBrowserEvent(Event event) {
        super.onBrowserEvent( event );
    }

    /*
     * (non-Javadoc)
     * @see org.drools.guvnor.client.common.isDirtable#isDirty()
     */
    public boolean isDirty() {
        return this.dirtyflag;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.guvnor.client.common.isDirtable#resetDirty()
     */
    public void resetDirty() {
        this.dirtyflag = false;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.guvnor.client.common.isDirtable#makeDirty()
     */
    public void makeDirty() {
        this.dirtyflag = true;
    }

    public static native int getHeight() /*-{
		var yScroll;

		if ($wnd.innerHeight && $wnd.scrollMaxY) {
			yScroll = $wnd.innerHeight + $wnd.scrollMaxY;
		} else if ($doc.body.scrollHeight > $doc.body.offsetHeight) { // all but Explorer Mac
			yScroll = $doc.body.scrollHeight;
		} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
			yScroll = $doc.body.offsetHeight;
		}

		var windowHeight;
		if (self.innerHeight) { // all except Explorer
			windowHeight = self.innerHeight;
		} else if ($doc.documentElement && $doc.documentElement.clientHeight) { // Explorer 6 Strict Mode
			windowHeight = $doc.documentElement.clientHeight;
		} else if ($doc.body) { // other Explorers
			windowHeight = $doc.body.clientHeight;
		}

		// for small pages with total height less then height of the viewport
		if (yScroll < windowHeight) {
			pageHeight = windowHeight;
		} else {
			pageHeight = yScroll;
		}
		return pageHeight;
    }-*/;

    public static native int getWidth() /*-{
		var xScroll;

		if ($wnd.innerHeight && $wnd.scrollMaxY) {
			xScroll = $doc.body.scrollWidth;
		} else if ($doc.body.scrollHeight > $doc.body.offsetHeight) { // all but Explorer Mac
			xScroll = $doc.body.scrollWidth;
		} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
			xScroll = $doc.body.offsetWidth;
		}

		var windowHeight;
		if (self.innerHeight) { // all except Explorer
			windowWidth = self.innerWidth;
		} else if ($doc.documentElement && $doc.documentElement.clientHeight) { // Explorer 6 Strict Mode
			windowWidth = $doc.documentElement.clientWidth;
		} else if ($doc.body) { // other Explorers
			windowWidth = $doc.body.clientWidth;
		}

		// for small pages with total width less then width of the viewport
		if (xScroll < windowWidth) {
			pageWidth = windowWidth;
		} else {
			pageWidth = xScroll;
		}
		return pageWidth;
    }-*/;

}
