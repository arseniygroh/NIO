import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class InboxArchiver {
    public static void archiveTmpFiles(Path inbox, Path archive) throws IOException {
        if (!Files.exists(archive)) {
            Files.createDirectories(archive);
        }

        if (!Files.exists(inbox)) {
            System.out.println("inbox wasn't found");
            return;
        }


        try (Stream<Path> paths = Files.list(inbox)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".tmp"))
                    .forEach(sourceFile -> {
                        try {
                            Path targetFile = archive.resolve(sourceFile.getFileName());
                            Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
