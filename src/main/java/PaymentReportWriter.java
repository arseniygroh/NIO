import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class PaymentReportWriter {
    public static void writeReport(Path out, List<Payment> payments, int invalidLines) throws IOException {
        try {
            if (out.getParent() != null) {
                Files.createDirectories(out.getParent());
            }
        } catch (IOException e) {
            System.out.println("Could not create directories: " + e.getMessage());
            return;
        }

        double paidTotal = 0;
        int countNew = 0;
        int countPaid = 0;
        int countFailed = 0;

        for (Payment payment : payments) {
            switch (payment.status()) {
                case NEW -> countNew++;
                case PAID -> {
                    countPaid++;
                    paidTotal += payment.amount();
                }
                case FAILED -> countFailed++;
            }
        }
        Path tempPath = Files.createTempFile(out.getParent(), "report_tmp", ".txt");

        try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
            writer.write("invalidLines=" + invalidLines);
            writer.newLine();

            writer.write("paidTotalCents=" + paidTotal);
            writer.newLine();

            writer.write("NEW=" + countNew + ", PAID=" + countPaid + ", FAILED=" + countFailed);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            Files.move(tempPath, out, ATOMIC_MOVE, REPLACE_EXISTING);
            System.out.println("File moved atomically.");
        } catch (AtomicMoveNotSupportedException e) {
            System.err.println("Atomic move not supported: " + e.getMessage());
            Files.move(tempPath, out, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
