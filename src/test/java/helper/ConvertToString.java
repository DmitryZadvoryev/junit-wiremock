package helper;

import java.io.InputStream;
import java.util.Scanner;

public class ConvertToString {

    public static String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }
}
