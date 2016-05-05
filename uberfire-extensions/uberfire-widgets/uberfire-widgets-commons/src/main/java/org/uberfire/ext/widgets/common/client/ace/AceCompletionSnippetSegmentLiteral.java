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
public class AceCompletionSnippetSegmentLiteral implements AceCompletionSnippetSegment {
	
	private String literalText;

	/**
	 * The literal text that makes up part of the snippet segment
	 * @param literalText The literal text that makes up part of the snippet. This does not need to be escaped, escaping will be handled automatically.
	 */
	public AceCompletionSnippetSegmentLiteral(String literalText) {
		this.literalText = literalText;
	}

	@Override
	public String getPreparedText(int tabstopNumber) {
		final String escapedText = literalText.replace("\\", "\\\\").replace("$", "\\$");
		return escapedText;
	}
	
	
}