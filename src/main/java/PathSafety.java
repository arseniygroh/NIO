import java.nio.file.Path;

public class PathSafety {
    public static Path safeResolve(Path base, String userInput) {
        Path absoluteBase = base.toAbsolutePath().normalize();
        Path resolvedPath = absoluteBase.resolve(userInput).normalize();

        if (!resolvedPath.startsWith(absoluteBase)) {
            throw new IllegalArgumentException("Спроба виходу за межі: " + userInput);
        }

        return resolvedPath;
    }
}
