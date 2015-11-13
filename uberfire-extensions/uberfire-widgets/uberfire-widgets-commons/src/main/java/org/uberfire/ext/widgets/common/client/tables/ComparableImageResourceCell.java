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
package org.uberfire.ext.widgets.common.client.tables;


import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ComparableImageResourceCell extends AbstractCell<ComparableImageResource> {
    @Override
    public void render(Context context,
                       ComparableImageResource value,
                       SafeHtmlBuilder sb) {
        if (value != null) {
            // The template will sanitize the URI.
        	sb.append(SafeHtmlUtils.fromTrustedString(value.getImageHTML()));
          }
    }
}
