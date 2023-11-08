/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.util;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;
import org.gwtproject.safehtml.shared.SafeUri;
import org.gwtproject.safehtml.shared.UriUtils;
import org.jboss.errai.common.client.util.Base64Util;

/**
 * An util class that consumes and generates an SVG data-uri, but it
 * performs additional operations.
 */
@ApplicationScoped
public class SvgDataUriGenerator {

    public static final String SVG_CONTENT_TYPE = "image/svg+xml";
    public static final String SVG_DATA_URI_BASE64 = "data:image/svg+xml;base64,";
    public static final String SVG_DATA_URI_UTF8 = "data:image/svg+xml;utf8,";
    static final String SVG_OPEN_TAG = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" " +
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\" %1s>\n";
    private static final String SVG_CLOSE_TAG = "</svg>";
    private static final String DEFS_OPEN_TAG = "<defs>";
    private static final String DEFS_CLOSE_TAG = "</defs>";
    private static final RegExp HREF_FILENAME_PATTERN = RegExp.compile(".*xlink:href=\"(.*\\.svg).*");
    private static final RegExp VIEWBOX_PATTERN = RegExp.compile(".*viewBox=\"(.*)\".*");
    private static final RegExp USE_TAG_PATTERN = RegExp.compile("<use.*xlink:href.*/>");
    private static final RegExp USE_TAG_REFID_PATTERN = RegExp.compile(".*xlink:href=\".*\\#(.*)\".*");
    private static final RegExp XML_TAG_PATTERN = RegExp.compile("<\\?xml.*\\?>");

    /**
     * Encodes the specified data-uri string in XML/UTF8 format.
     */
    public static String encodeUtf8(final String dataUriDecoded) {
        return SVG_DATA_URI_UTF8 + UriUtils.encode(dataUriDecoded).replace("#",
                                                                           "%23");
    }

    /**
     * Encodes the specified data-uri string in XML/Base64 format.
     */
    public static String encodeBase64(final String dataUriDecoded) {
        return SVG_DATA_URI_BASE64 + Base64Util.encode(dataUriDecoded.getBytes(),
                                                       0,
                                                       dataUriDecoded.length());
    }

    /**
     * Generates an SVG data-uri with no external resource references, if any.
     * <p/>
     * GWT DataResource does not support external references, used for example
     * by the <code>use</code> element.
     */
    @SuppressWarnings("unchecked")
    public String generate(final SafeUri svgUri) {
        return generate(svgUri,
                        Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST);
    }

    /**
     * Generates a single SVG data-uri from different input SVG declarations.
     * <p/>
     * As GWT DataResource does not like SVG external references, this is an alternative
     * solution that creates a composite SVG declaration by joining each SVG declaration
     * given from the arguments.
     * <p/>
     * Given a parent SVG declaration, which contain external resource references to other
     * SVG declarations, and its given SVG referenced declarations, it aggregates the
     * referenced elements in a SVG <code>Def</code> element, and generates a composite
     * SVG which encapsulates the parent and referenced ones in a single declaration. It also
     * handles the external resource references, within the parent SVG, in order to match the actual
     * content, and removes the invalid references.
     * <p/>
     * It generates a valid SVG declaration as:
     * <svg id="composite">
     * <svg id="parent">
     * <use xlink:href .../>
     * </svg>
     * <defs>
     * <svg id="referenced1" ...></svg>
     * </defs>
     * </svg>
     * @param svgUri The parent SVG declaration data-uri, which may contain external resource references.
     * @param svgDefs The external resources which are referenced in the parent SVG declaration, given by the <code>svgUri</code> argument.
     * @param validUseRefIds The valid external reference identifiers to keep in the resulting declaration. Usually those are
     * the SVG identifiers for each of the declarations given by the <code>svgDefs</code> argument.
     */
    public String generate(final SafeUri svgUri,
                           final Collection<SafeUri> svgDefs,
                           final Collection<String> validUseRefIds) {
        final String mainContent =
                getSVGContent(svgUri.asString(),
                              new SVGGeneratorOptions(true,
                                                      validUseRefIds.isEmpty(),
                                                      viewName -> !validUseRefIds.contains(viewName),
                                                      true));

        if (!svgDefs.isEmpty()) {
            final String childrenContent = getChildrenSVGContent(svgDefs,
                                                                 new SVGGeneratorOptions(true,
                                                                                         true,
                                                                                         false));
            final String defs = DEFS_OPEN_TAG + childrenContent + DEFS_CLOSE_TAG;
            final String svgOpenTag = SVG_OPEN_TAG.replaceAll("\\%1s",
                                                              generateSVGSizeAttributesToAppend(mainContent));
            return svgOpenTag +
                    mainContent +
                    defs
                    + SVG_CLOSE_TAG;
        }
        return mainContent;
    }

    private static String getChildrenSVGContent(final Collection<SafeUri> uris,
                                                final SVGGeneratorOptions options) {
        return uris.stream()
                .map(uriStream -> getSVGContent(uriStream.asString(),
                                                options))
                .collect(Collectors.joining(" "));
    }

    private static String getSVGContent(final String dataUriEncodedContent,
                                        final SVGGeneratorOptions options) {
        final SVGContentType type = getType(dataUriEncodedContent);
        String s = dataUriEncodedContent.substring(type.getLength());
        if (SVGContentType.XML_BASE64.equals(type)) {
            s = new String(Base64Util.decode(s));
        }
        if (options.removeXmlTag) {
            s = removeXmlTag(s);
        }
        if (options.removeUseTags) {
            s = removeAllUseTags(s);
        } else {
            s = removeUseTagsById(s,
                                  options.isRemoveUseForView);
        }
        if (options.removeHrefFileNames) {
            s = removeAllHrefFileName(s);
        }
        return s;
    }

    private static String removeXmlTag(final String content) {
        return removeAll(XML_TAG_PATTERN,
                         0,
                         content);
    }

    private static String removeAllUseTags(final String content) {
        return removeAll(USE_TAG_PATTERN,
                         0,
                         content);
    }

    private static String removeAllHrefFileName(final String content) {
        return removeAll(HREF_FILENAME_PATTERN,
                         1,
                         content);
    }

    private static String removeAll(final RegExp exp,
                                    final int group,
                                    final String content) {
        String result = content;
        while (exp.test(result)) {
            final MatchResult matchResult = exp.exec(result);
            if (matchResult != null) {
                String toReplace = matchResult.getGroup(group);
                result = result.replace(toReplace,
                                        "");
            }
        }
        return result;
    }

    private static String removeUseTagsById(final String content,
                                            final Predicate<String> isRemove) {
        String result = content;
        String temp = content;
        while (USE_TAG_REFID_PATTERN.test(temp)) {
            final MatchResult matchResult = USE_TAG_REFID_PATTERN.exec(temp);
            if (matchResult != null) {
                String id = matchResult.getGroup(1);
                if (isRemove.test(id)) {
                    result = result.replace(matchResult.getGroup(0),
                                            "");
                }
                temp = temp.substring(matchResult.getIndex() + id.length());
            }
        }
        return result;
    }

    private String generateSVGSizeAttributesToAppend(final String mainContent) {
        final String[] bb = parseViewBox(mainContent);
        if (null != bb) {
            return "width=\"" + bb[2] + "\" " +
                    "height=\"" + bb[3] + "\" " +
                    "viewBox=\"" + bb[0] + " " + bb[1] + " " +
                    bb[2] + " " + bb[3] + "\" ";
        }
        return "";
    }

    private String[] parseViewBox(final String raw) {
        final MatchResult result = VIEWBOX_PATTERN.exec(raw);
        if (null != result && result.getGroupCount() == 2) {
            final String bbRaw = result.getGroup(1);
            return bbRaw.split(" ");
        }
        return null;
    }

    private static SVGContentType getType(final String s) {
        if (s.startsWith(SVG_DATA_URI_BASE64)) {
            return SVGContentType.XML_BASE64;
        } else if (s.startsWith(SVG_DATA_URI_UTF8)) {
            return SVGContentType.XML_UTF8;
        }
        throw new IllegalArgumentException("The image data-uri specified is not a valid SVG data " +
                                                   "for being embedded into the DOM.");
    }

    private enum SVGContentType {
        XML_BASE64(26),
        XML_UTF8(24);

        private final int length;

        SVGContentType(final int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }
    }

    private static final class SVGGeneratorOptions {

        private final boolean removeXmlTag;
        private final boolean removeUseTags;
        private final Predicate<String> isRemoveUseForView;
        private final boolean removeHrefFileNames;

        public SVGGeneratorOptions(final boolean removeXmlTag,
                                   final boolean removeUseTags,
                                   final boolean removeHrefFileNames) {
            this.removeXmlTag = removeXmlTag;
            this.removeUseTags = removeUseTags;
            this.isRemoveUseForView = viewName -> true;
            this.removeHrefFileNames = removeHrefFileNames;
        }

        public SVGGeneratorOptions(final boolean removeXmlTag,
                                   final boolean removeUseTags,
                                   final Predicate<String> isRemoveUseForView,
                                   final boolean removeHrefFileNames) {
            this.removeXmlTag = removeXmlTag;
            this.removeUseTags = removeUseTags;
            this.isRemoveUseForView = isRemoveUseForView;
            this.removeHrefFileNames = removeHrefFileNames;
        }
    }
}
