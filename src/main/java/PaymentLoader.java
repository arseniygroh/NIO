import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PaymentLoader {
    public static List<Payment> load(Path csv) {
        if (!Files.exists(csv)) return null;

        List<Payment> payments = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String line;
            String header = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                long id = Long.parseLong(parts[0].trim());
                String email = parts[1].trim();
                PaymentStatus status = PaymentStatus.valueOf(parts[2].trim().toUpperCase());
                double totalPrice = Double.parseDouble(parts[3].trim());
                payments.add(new Payment(id, email, status, totalPrice));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return payments;
    }


    public static LoadResult loadWithStats(Path csv) {
        if (!Files.exists(csv)) return null;

        List<Payment> payments = new ArrayList<>();
        int invalidLines = 0;

        try (BufferedReader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String line;
            String header = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    invalidLines++;
                    continue;
                }

                try {
                    String[] parts = line.split(",");

                    if (parts.length != 4) {
                        throw new IllegalArgumentException("Data is corrupted");
                    }

                    long id = Long.parseLong(parts[0].trim());
                    String email = parts[1].trim();
                    PaymentStatus status = PaymentStatus.valueOf(parts[2].trim().toUpperCase());
                    double amount = Double.parseDouble(parts[3].trim());
                    payments.add(new Payment(id, email, status, amount));

                } catch (Exception e) {
                    invalidLines++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return new LoadResult(payments, invalidLines);
    }
}
