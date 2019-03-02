package io.github.bensku.skript.util;

public class StringUtils {

    public static boolean startsWith(String str1, int start1, int end1, String str2, int start2, int end2) {
        assert str1 != null;
        assert start1 <= end1;
        assert str2 != null;
        assert start2 <= end2;
        
        if (end1 - start1 < end2 - start2) {
            return false; // String can't be shorter than what it starts with is
        }
        
        for (int i = start1, j = start2; j < end2;) {
            int c1 = str1.codePointAt(i);
            int c2 = str2.codePointAt(j);
            if (c1 != c2) {
                return false;
            }
            int ccount = Character.charCount(c1);
            i += ccount;
            j += ccount;
        }
        return true;
    }
    
    public static int trimStart(String str, int start) {
    	for (int i = start; i < str.length();) {
    		int c = str.codePointAt(i);
    		if (!Character.isWhitespace(c)) {
    			return i;
    		}
    		i += Character.charCount(c);
    	}
    	return str.length();
    }
    
    public static int trimEnd(String str, int end) {
    	for (int i = end - 1; i >= 0;) {
    		int c = str.codePointAt(i);
    		if (!Character.isWhitespace(c)) {
    			return i + 1;
    		}
    		i -= Character.charCount(c);
    	}
    	return 0;
    }
}
