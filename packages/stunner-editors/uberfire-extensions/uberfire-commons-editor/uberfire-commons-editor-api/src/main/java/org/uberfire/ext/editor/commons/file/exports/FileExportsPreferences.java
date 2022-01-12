/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.file.exports;

public class FileExportsPreferences {

    String pdfOrientation = format(PdfExportPreferences.Orientation.PORTRAIT);

    String pdfUnit = format(PdfExportPreferences.Unit.MM);

    String pdfFormat = format(PdfExportPreferences.Format.A4);

    public PdfExportPreferences getPdfPreferences() {
        return PdfExportPreferences.create(pdfOrientation,
                                           pdfUnit,
                                           pdfFormat);
    }

    private static <T extends Enum<?>> String format(final T value) {
        return EnumValuePropertyValidator.format(value);
    }
}
