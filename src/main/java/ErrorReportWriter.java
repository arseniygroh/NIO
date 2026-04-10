import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ErrorReportWriter {
    public static void generateReport(Path file, List<String> errors) {
        if (!Files.exists(file)) throw new RuntimeException("File doesn't exists");

        List<String> dates = new ArrayList<>();
        List<String> codes = new ArrayList<>();

        for (int i = 0; i < errors.size(); i++) {
            String error = errors.get(i);
            String data = error.substring(0, 10);
            dates.add(data);

            int codeIndex = error.indexOf("code");
            StringBuilder code = new StringBuilder();
            for (int j = codeIndex; j < error.length(); j++) {
                if (error.charAt(j) != ' ') code.append(error.charAt(j));
                else break;
            }
            codes.add(code.toString());
        }

        Map<String, Long> dateEntries = dates.stream()
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

        Map<String, Long> codeEntries = codes.stream()
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("[by-day]");
            writer.newLine();
            dateEntries.forEach((key, value) -> {
                try {
                    writer.write(key + "=" + value);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.write("[by-code]");
            writer.newLine();

            codeEntries.forEach((key, value) -> {
                try {
                    writer.write(key + "=" + value);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
