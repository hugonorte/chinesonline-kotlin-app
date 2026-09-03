import java.util.Properties;
import java.io.FileInputStream;
public class TestProp {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream("local.properties"));
        System.out.println(p.getProperty("KEYSTORE_PASSWORD"));
    }
}
