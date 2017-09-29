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

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.impl.validation.EnumValuePropertyValidator;

@WorkbenchPreference(identifier = "FileExport",
        bundleKey = "FileExport.Label",
        parents = "LibraryPreferences")
public class FileExportsPreferences implements BasePreference<FileExportsPreferences> {

    @Property(bundleKey = "FileExport.PdfOrientation.Text",
            helpBundleKey = "FileExport.PdfOrientation.Help",
            validators = PdfOrientationValidator.class)
    String pdfOrientation;

    @Property(bundleKey = "FileExport.PdfUnit.Text",
            helpBundleKey = "FileExport.PdfUnit.Help",
            validators = PdfUnitValidator.class)
    String pdfUnit;

    @Property(bundleKey = "FileExport.PdfFormat.Text",
            helpBundleKey = "FileExport.PdfFormat.Help",
            validators = PdfFormatValidator.class)
    String pdfFormat;

    @Override
    public FileExportsPreferences defaultValue(final FileExportsPreferences defaultValue) {
        defaultValue.pdfOrientation = format(PdfExportPreferences.Orientation.PORTRAIT);
        defaultValue.pdfUnit = format(PdfExportPreferences.Unit.MM);
        defaultValue.pdfFormat = format(PdfExportPreferences.Format.A4);
        return defaultValue;
    }

    public PdfExportPreferences getPdfPreferences() {
        return PdfExportPreferences.create(pdfOrientation,
                                           pdfUnit,
                                           pdfFormat);
    }

    private static <T extends Enum<?>> String format(final T value) {
        return EnumValuePropertyValidator.format(value);
    }
}
