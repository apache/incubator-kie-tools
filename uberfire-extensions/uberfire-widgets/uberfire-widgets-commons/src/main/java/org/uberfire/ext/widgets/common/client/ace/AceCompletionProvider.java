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

/**
 * A provider of custom code-completion proposals.
 * 
 * <strong>Warning</strong>: this is an experimental feature of AceGWT.
 * It is possible that the API will change in an incompatible way
 * in future releases.
 */
public interface AceCompletionProvider {
	/**
	 * Call to get code completion proposals, which are delivered
	 * to a callback. Note that it is fine for this method to
	 * invoke the callback at a later time (for example, on completion
	 * of RPC.) 
	 * 
	 * @param editor   the {@link AceEditor}
	 * @param pos      the cursor position
	 * @param prefix   the word prefix
	 * @param callback the {@link AceCompletionCallback} to which the
	 *                 proposals should be delivered
	 */
	public void getProposals( AceEditor editor,
                              AceEditorCursorPosition pos,
                              String prefix,
                              AceCompletionCallback callback );
}
