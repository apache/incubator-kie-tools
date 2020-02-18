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
package javax.xml.namespace;

import java.util.Objects;

import javax.xml.XMLConstants;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * GWT Super-source version of javax.xml.namespace.QName.
 */
@JsType(namespace = JsPackage.GLOBAL)
public class QName {

    @JsProperty(name = "namespaceURI")
    public final native String getNamespaceURI();

    @JsProperty(name = "namespaceURI")
    public final native void setNamespaceURI(final String namespaceURI);

    @JsProperty(name = "localPart")
    public final native String getLocalPart();

    @JsProperty(name = "localPart")
    public final native void setLocalPart(final String localPart);

    @JsProperty(name = "prefix")
    public final native String getPrefix();

    @JsProperty(name = "prefix")
    public final native void setPrefix(final String prefix);

    @JsProperty(name = "key")
    public final native String getKey();

    @JsProperty(name = "key")
    public final native void setKey(String key);

    @JsProperty(name = "string")
    public final native String getString();

    @JsProperty(name = "string")
    public final native void setString(String string);

    @JsConstructor
    public QName(final String namespaceURI,
                 final String localPart,
                 final String prefix) {
        if (Objects.isNull(namespaceURI)) {
            setNamespaceURI(XMLConstants.NULL_NS_URI);
        } else {
            setNamespaceURI(namespaceURI);
        }

        if (Objects.isNull(localPart)) {
            throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
        }
        setLocalPart(localPart);

        if (Objects.isNull(prefix)) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
        }
        setPrefix(prefix);

        //jsonix JSON properties
        setKey(toString());
        String retrievedPrefix = getPrefix();
        final String usedPrefix = (retrievedPrefix == null || retrievedPrefix.isEmpty()) ?  "" : retrievedPrefix + ":";
        final String string = "{" + getNamespaceURI() + "}" + usedPrefix + getLocalPart();
        setString(string);
    }

    @Override
    public String toString() {
        if (Objects.equals(getNamespaceURI(), XMLConstants.NULL_NS_URI)) {
            return getLocalPart();
        } else {
            return "{" + getNamespaceURI() + "}" + getLocalPart();
        }
    }

    /**
     * See {@link QName#equals(Object)}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final QName qName = (QName) o;

        return Objects.equals(getNamespaceURI(), qName.getNamespaceURI()) &&
                Objects.equals(getLocalPart(), qName.getLocalPart());
    }

    /**
     * See {@link QName#hashCode()}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getNamespaceURI(), getLocalPart());
    }

    public static QName valueOf(final String qNameAsString) {

        // null is not valid
        if (Objects.isNull(qNameAsString)) {
            throw new IllegalArgumentException("cannot create QName from \"null\"");
        }

        // "" local part is valid to preserve compatible behavior with QName 1.0
        if (qNameAsString.length() == 0) {
            return new QName(XMLConstants.NULL_NS_URI,
                             qNameAsString,
                             XMLConstants.DEFAULT_NS_PREFIX);
        }

        // local part only?
        if (qNameAsString.charAt(0) != '{') {
            return new QName(XMLConstants.NULL_NS_URI,
                             qNameAsString,
                             XMLConstants.DEFAULT_NS_PREFIX);
        }

        // Namespace URI improperly specified?
        if (qNameAsString.startsWith("{" + XMLConstants.NULL_NS_URI + "}")) {
            throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), "
                                                       + ".equals(\"" + XMLConstants.NULL_NS_URI + "\"), "
                                                       + "only the local part, "
                                                       + "\""
                                                       + qNameAsString.substring(2 + XMLConstants.NULL_NS_URI.length())
                                                       + "\", "
                                                       + "should be provided.");
        }

        // Namespace URI and local part specified
        final int endOfNamespaceURI = qNameAsString.indexOf('}');
        if (endOfNamespaceURI == -1) {
            throw new IllegalArgumentException("cannot create QName from \""
                                                       + qNameAsString
                                                       + "\", missing closing \"}\"");
        }
        return new QName(qNameAsString.substring(1, endOfNamespaceURI),
                         qNameAsString.substring(endOfNamespaceURI + 1),
                         XMLConstants.DEFAULT_NS_PREFIX);
    }
}
