import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogReader {
    public static String readLineAt(Path log, long offset) {
        if (!Files.exists(log)) {
            return null;
        }
        String line;
        try (RandomAccessFile raf = new RandomAccessFile(log.toFile(), "r")) {

            raf.seek(offset);
            line = raf.readLine();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return line;
    }
}
