import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class StatusFile {
    public static void updateStatus(Path file, int index, byte status) {
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw");
             FileChannel channel = raf.getChannel()) {

            channel.position(index);

            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(status);
            buffer.flip();

            channel.write(buffer);

            channel.position(index);
            buffer.clear();

            channel.read(buffer);
            buffer.flip();

            byte statusByte = buffer.get(0);
            System.out.println(statusByte);

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
