import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Path pathPayments = Path.of("files/payments.csv");
        Path pathReports = Path.of("files/reports.txt");

        LoadResult result = PaymentLoader.loadWithStats(pathPayments);

        System.out.println("Valid: " + result.payments().size());
        System.out.println("Invalid: " + result.invalidLines());

        System.out.println("List of valid payments:");
        result.payments().forEach(System.out::println);

        PaymentReportWriter.writeReport(pathReports, result.payments(), result.invalidLines());

        InboxArchiver.archiveTmpFiles(Path.of("files"), Path.of("archive"));
        Path resultSafety = PathSafety.safeResolve(Path.of("files"), "reports/2025.txt");
        System.out.println(resultSafety);
        //Path resultSafetyWrong = PathSafety.safeResolve(Path.of("files"), "../secret.txt");
        //System.out.println(resultSafety);

        StatusFile.updateStatus(Path.of("files/status.bin"), 5, (byte) 1);
    }
}
