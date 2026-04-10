import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class IndexIO {
    public static List<Long> readOffsets(Path index) {
        if (!Files.exists(index)) {
            return new ArrayList<>();
        }

        List<Long> offsets = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(index.toFile(), "r");
             FileChannel channel = raf.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (channel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.remaining() >= Long.BYTES) {
                    offsets.add(buffer.getLong());
                }
                buffer.clear();
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return offsets;
    }
}
