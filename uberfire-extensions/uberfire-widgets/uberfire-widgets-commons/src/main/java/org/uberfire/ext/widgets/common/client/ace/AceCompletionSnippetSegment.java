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
 * A segment of a completion snippet
 * */
public interface AceCompletionSnippetSegment  {
	
	/**
	 * Gets the escaped and prepared textual representation of this snippet segment (backslash and dollar are escaped in general and the dollar is used for certain special tokens)
	 * @return the prepared textual representation of this snippet segment
	 */
	public String getPreparedText( int tabStopNumber );
}