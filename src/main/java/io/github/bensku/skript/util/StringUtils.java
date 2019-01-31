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
        
        for (int i = start1, j = start2; j < end1;) {
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
}
