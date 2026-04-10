import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LogIndexer {

    public static List<Long> indexLines(Path log, String token) {
        if (!Files.exists(log)) {
            return new ArrayList<>();
        }
        List<Long> offsets = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(log.toFile(), "rw");
             FileChannel channel = raf.getChannel();
             ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();) {

            int bufferSize = 1024;
            long absoluteFilePos = channel.position();
            long currentLineStart = 0;

            ByteBuffer buff = ByteBuffer.allocate(bufferSize);

            while (channel.read(buff) > 0) {
                buff.flip();
                while (buff.hasRemaining()) {
                    byte b = buff.get();
                    if (b == '\n') {
                        String line = lineBuf.toString(StandardCharsets.UTF_8);
                        if (line.contains(token)) {
                            offsets.add(currentLineStart);
                        }
                        lineBuf.reset();
                        currentLineStart = absoluteFilePos + 1;
                    } else {
                        lineBuf.write(b);
                    }
                    absoluteFilePos++;
                }
                buff.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return offsets;
    }

    public static void writeIndexFile(Path idxFile, List<Long> offsets) {
        try (RandomAccessFile raf = new RandomAccessFile(idxFile.toFile(), "rw");
            FileChannel channel = raf.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(offsets.size() * Long.BYTES);

            for (Long offset : offsets) {
                buffer.putLong(offset);
            }

            buffer.flip();
            channel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
