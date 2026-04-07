import java.util.List;

record LoadResult(List<Payment> payments, int invalidLines) {}
