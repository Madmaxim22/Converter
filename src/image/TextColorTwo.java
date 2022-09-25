package image;

public class TextColorTwo implements TextColorSchema {

    final String CHARS = "MNHQ&OC?7>!;:-.";

    @Override
    public char convert(int color) {
        return CHARS.charAt((int) Math.round((CHARS.length() - 1) / 255.0 * color));
    }
}
