/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.xml;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * GWT Super-source version of javax.xml.XMLConstants.
 */
@JsType(namespace = JsPackage.GLOBAL)
public final class XMLConstants {

    /**
     * <p>Private constructor to prevent instantiation.</p>
     */
    private XMLConstants() {
    }

    public static final String NULL_NS_URI = "";

    public static final String DEFAULT_NS_PREFIX = "";

    public static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";

    public static final String XML_NS_PREFIX = "xml";

    public static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";

    public static final String XMLNS_ATTRIBUTE = "xmlns";

    public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

    public static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String W3C_XPATH_DATATYPE_NS_URI = "http://www.w3.org/2003/11/xpath-datatypes";

    public static final String XML_DTD_NS_URI = "http://www.w3.org/TR/REC-xml";

    public static final String RELAXNG_NS_URI = "http://relaxng.org/ns/structure/1.0";

    public static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

    public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";

    public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";

    public static final String ACCESS_EXTERNAL_STYLESHEET = "http://javax.xml.XMLConstants/property/accessExternalStylesheet";
}