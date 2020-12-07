package fr.tokazio.ripper;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RipperUtils {

    private RipperUtils() {
        super();
    }

    public static String tidyFilename(final String filename) {
        String ret;
        Pattern bad = Pattern.compile("[\\:*?\"`<>|]");
        Matcher badMatcher = bad.matcher(filename);
        ret = badMatcher.replaceAll("");
        Pattern slash = Pattern.compile("/");
        Matcher slashMatcher = slash.matcher(ret);
        ret = slashMatcher.replaceAll("-");
        return ret;
    }

    public static File tidyFilename(final File filename) {
        String ret;
        Pattern bad = Pattern.compile("[\\:*?\"`<>|]");
        Matcher badMatcher = bad.matcher(filename.getName());
        ret = badMatcher.replaceAll("");
        Pattern slash = Pattern.compile("/");
        Matcher slashMatcher = slash.matcher(ret);
        ret = slashMatcher.replaceAll("-");
        return new File(filename.getParentFile(), ret);
    }
}
