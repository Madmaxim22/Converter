package ru.netology.graphics.image;

public class TextColor implements TextColorSchema {

    final String CHARS = "#$@%*+-\\.";

    @Override
    public char convert(int color) {
        return CHARS.charAt((int) Math.round((CHARS.length() - 1) / 255.0 * color));
    }
}
