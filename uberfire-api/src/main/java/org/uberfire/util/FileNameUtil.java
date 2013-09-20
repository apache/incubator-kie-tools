package org.uberfire.util;


public final class FileNameUtil {
	public static String removeExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		final int index = indexOfExtension(filename);
		if (index == -1) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}

	private static int indexOfExtension(final String filename) {
		if (filename == null) {
			return -1;
		}
		final int extensionPos = filename.lastIndexOf(".");
		return extensionPos;
	}
    

}
