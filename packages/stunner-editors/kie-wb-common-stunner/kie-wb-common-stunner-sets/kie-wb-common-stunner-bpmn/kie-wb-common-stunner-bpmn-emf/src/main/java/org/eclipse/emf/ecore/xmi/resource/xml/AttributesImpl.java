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


package org.eclipse.emf.ecore.xmi.resource.xml;

public class AttributesImpl implements Attributes {

    int length;
    String[] data;

    public AttributesImpl() {
        this.length = 0;
        this.data = null;
    }

    public AttributesImpl(Attributes atts) {
        this.setAttributes(atts);
    }

    public int getLength() {
        return this.length;
    }

    public String getURI(int index) {
        return index >= 0 && index < this.length ? this.data[index * 5] : null;
    }

    public String getLocalName(int index) {
        return index >= 0 && index < this.length ? this.data[index * 5 + 1] : null;
    }

    public String getQName(int index) {
        return index >= 0 && index < this.length ? this.data[index * 5 + 2] : null;
    }

    public String getType(int index) {
        return index >= 0 && index < this.length ? this.data[index * 5 + 3] : null;
    }

    public String getValue(int index) {
        return index >= 0 && index < this.length ? this.data[index * 5 + 4] : null;
    }

    public int getIndex(String uri, String localName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return i / 5;
            }
        }

        return -1;
    }

    public int getIndex(String qName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return i / 5;
            }
        }

        return -1;
    }

    public String getType(String uri, String localName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return this.data[i + 3];
            }
        }

        return null;
    }

    public String getType(String qName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return this.data[i + 3];
            }
        }

        return null;
    }

    public String getValue(String uri, String localName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return this.data[i + 4];
            }
        }

        return null;
    }

    public String getValue(String qName) {
        int max = this.length * 5;

        for (int i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return this.data[i + 4];
            }
        }

        return null;
    }

    public void clear() {
        if (this.data != null) {
            for (int i = 0; i < this.length * 5; ++i) {
                this.data[i] = null;
            }
        }

        this.length = 0;
    }

    public void setAttributes(Attributes atts) {
        this.clear();
        this.length = atts.getLength();
        if (this.length > 0) {
            this.data = new String[this.length * 5];

            for (int i = 0; i < this.length; ++i) {
                this.data[i * 5] = atts.getURI(i);
                this.data[i * 5 + 1] = atts.getLocalName(i);
                this.data[i * 5 + 2] = atts.getQName(i);
                this.data[i * 5 + 3] = atts.getType(i);
                this.data[i * 5 + 4] = atts.getValue(i);
            }
        }
    }

    public void addAttribute(String uri, String localName, String qName, String type, String value) {
        this.ensureCapacity(this.length + 1);
        this.data[this.length * 5] = uri;
        this.data[this.length * 5 + 1] = localName;
        this.data[this.length * 5 + 2] = qName;
        this.data[this.length * 5 + 3] = type;
        this.data[this.length * 5 + 4] = value;
        ++this.length;
    }

    public void setAttribute(int index, String uri, String localName, String qName, String type, String value) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5] = uri;
            this.data[index * 5 + 1] = localName;
            this.data[index * 5 + 2] = qName;
            this.data[index * 5 + 3] = type;
            this.data[index * 5 + 4] = value;
        } else {
            this.badIndex(index);
        }
    }

    public void removeAttribute(int index) {
        if (index >= 0 && index < this.length) {
            if (index < this.length - 1) {
                System.arraycopy(this.data, (index + 1) * 5, this.data, index * 5, (this.length - index - 1) * 5);
            }

            index = (this.length - 1) * 5;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index] = null;
            --this.length;
        } else {
            this.badIndex(index);
        }
    }

    public void setURI(int index, String uri) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5] = uri;
        } else {
            this.badIndex(index);
        }
    }

    public void setLocalName(int index, String localName) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 1] = localName;
        } else {
            this.badIndex(index);
        }
    }

    public void setQName(int index, String qName) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 2] = qName;
        } else {
            this.badIndex(index);
        }
    }

    public void setType(int index, String type) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 3] = type;
        } else {
            this.badIndex(index);
        }
    }

    public void setValue(int index, String value) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 4] = value;
        } else {
            this.badIndex(index);
        }
    }

    private void ensureCapacity(int n) {
        if (n > 0) {
            int max;
            if (this.data != null && this.data.length != 0) {
                if (this.data.length >= n * 5) {
                    return;
                }

                max = this.data.length;
            } else {
                max = 25;
            }

            while (max < n * 5) {
                max *= 2;
            }

            String[] newData = new String[max];
            if (this.length > 0) {
                System.arraycopy(this.data, 0, newData, 0, this.length * 5);
            }

            this.data = newData;
        }
    }

    private void badIndex(int index) throws ArrayIndexOutOfBoundsException {
        String msg = "Attempt to modify attribute at illegal index: " + index;
        throw new ArrayIndexOutOfBoundsException(msg);
    }
}
