/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.json;

import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.json.JsonObject;

import static org.dashbuilder.dataset.json.DataSetDefJSONMarshaller.*;

public class CSVDefJSONMarshaller implements DataSetDefJSONMarshallerExt<CSVDataSetDef> {

    public static CSVDefJSONMarshaller INSTANCE = new CSVDefJSONMarshaller();

    public static final String FILEURL = "fileURL";
    public static final String FILEPATH = "filePath";
    public static final String SEPARATORCHAR = "separatorChar";
    public static final String QUOTECHAR = "quoteChar";
    public static final String ESCAPECHAR = "escapeChar";
    public static final String DATEPATTERN = "datePattern";
    public static final String NUMBERPATTERN = "numberPattern";

    @Override
    public void fromJson(CSVDataSetDef def, JsonObject json) {
        String fileURL = json.getString(FILEURL);
        String filePath = json.getString(FILEPATH);
        String separatorChar = parseCodePoint(json.getString(SEPARATORCHAR));
        String quoteChar = parseCodePoint(json.getString(QUOTECHAR));
        String escapeChar = parseCodePoint(json.getString(ESCAPECHAR));
        String datePattern = json.getString(DATEPATTERN);
        String numberPattern = json.getString(NUMBERPATTERN);

        if (!isBlank(fileURL)) {
            def.setFileURL(fileURL);
        }
        if (!isBlank(filePath)) {
            def.setFilePath(filePath);
        }
        if (!isBlank(separatorChar)) {
            def.setSeparatorChar(separatorChar.charAt(0));
        }
        if (!isBlank(quoteChar)) {
            def.setQuoteChar(quoteChar.charAt(0));
        }
        if (!isBlank(escapeChar)) {
            def.setEscapeChar(escapeChar.charAt(0));
        }
        if (!isBlank(numberPattern)) {
            def.setNumberPattern(numberPattern);
        }
        if (!isBlank(datePattern)) {
            def.setDatePattern(datePattern);
        }
    }

    public String parseCodePoint(String codePoint) {
        try {
            if (!isBlank(codePoint)) {
                return String.valueOf(Character.toChars(Integer.parseInt(codePoint)));
            }
        } catch (Exception e) {
            // If is not a code point then return the string "as is"
        }
        return codePoint;
    }

    @Override
    public void toJson(CSVDataSetDef dataSetDef, JsonObject json) {
        // File.
        if (dataSetDef.getFilePath() != null) {
            json.put(FILEPATH, dataSetDef.getFilePath());
        }
        if (dataSetDef.getFileURL() != null) {
            json.put(FILEURL, dataSetDef.getFileURL());
        }

        // Separator.
        json.put(SEPARATORCHAR, String.valueOf(dataSetDef.getSeparatorChar()));

        // Quote.
        json.put(QUOTECHAR, String.valueOf(dataSetDef.getQuoteChar()));

        // Escape.
        json.put(ESCAPECHAR, String.valueOf(dataSetDef.getEscapeChar()));

        // Date pattern.
        json.put(DATEPATTERN, dataSetDef.getDatePattern());

        // Number pattern.
        json.put(NUMBERPATTERN, dataSetDef.getNumberPattern());

        // All columns flag.
        json.put(ALL_COLUMNS, dataSetDef.isAllColumnsEnabled());
    }
}
