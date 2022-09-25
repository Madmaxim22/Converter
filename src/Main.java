import image.Converter;
import image.TextGraphicsConverter;
import server.GServer;

public class Main {
    public static void main(String[] args) throws Exception {
        TextGraphicsConverter converter = new Converter();
        GServer server = new GServer(converter);
        server.start();
    }
}
