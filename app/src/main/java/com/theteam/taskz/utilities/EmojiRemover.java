package com.theteam.taskz.utilities;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiRemover {
    // Updated emoji regex pattern
    private static final String emojiRegex = "[\uD83C-\uDBFF\uDC00-\uDFFF]+|[\u2600-\u26FF\u2700-\u27BF]+|[\u200D\uFE0F]+";

    public static String removeEmojis(String input) {
        Pattern pattern = Pattern.compile(emojiRegex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
}
