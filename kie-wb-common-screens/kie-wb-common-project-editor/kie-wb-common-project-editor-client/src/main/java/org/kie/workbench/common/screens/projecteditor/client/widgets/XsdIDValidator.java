package org.kie.workbench.common.screens.projecteditor.client.widgets;

public class XsdIDValidator {

    public static boolean validate(String name) {
        if (isFirstLetterValid(name)) {

            for (int i = 0; i < name.length(); i++) {
                String text = name.substring(i, i + 1);
                if (!isLetter(text) && !isNumber(text) && !text.contains("_") && !text.contains("-") && !text.contains(".")) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
    //  can only contain letters, digits, underscores, hyphens, and periods.

    private static boolean isFirstLetterValid(String name) {
        return name.startsWith("_") || isLetter(name.substring(0, 1));
    }

    private static boolean isLetter(String text) {
        return text.matches("[a-zA-Z]");
    }

    private static boolean isNumber(String name) {
        return name.matches("[0-9]");
    }
}
