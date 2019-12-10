package me.umov.shared.libraries.domain.utils

class Title {

    private static final int TITLE_MAX_LENGTH = 84

    static String buildTitle(String title) {
        if (title == null || title.length() >= TITLE_MAX_LENGTH) {
            return title
        }

        int fillSpaces = (int) (((TITLE_MAX_LENGTH - title.length()) / 2)) - 1

        String firstPart = "${'-' * fillSpaces} $title "

        return "$firstPart${'-' * (TITLE_MAX_LENGTH - firstPart.length())}"
    }

}
