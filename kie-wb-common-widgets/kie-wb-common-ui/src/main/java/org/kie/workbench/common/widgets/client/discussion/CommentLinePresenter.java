/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.discussion;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.kie.workbench.common.widgets.client.util.DateFormatHelper;

public class CommentLinePresenter
        implements IsWidget,
                    CommentLineView.Presenter {

    private CommentLineView view;

    public CommentLinePresenter() {
        this( new CommentLineViewImpl() );
    }

    public CommentLinePresenter( CommentLineView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    protected String formatTimestamp( long timestamp ) {
        return DateFormatHelper.shortFormat( timestamp );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setRecord( DiscussionRecord record ) {
        view.setAuthor( record.getAuthor() + ":" );
        view.setComment( "\"" + record.getNote() + "\"" );
        view.setDate( formatTimestamp( record.getTimestamp() ) );
    }
}