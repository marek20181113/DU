import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class DU {
    enum FileType {
        DIR, FILE;
        static FileType of(Path path) {
            return Files.isDirectory(path) ? DIR : FILE;
        }
    }
    static class PathInfo {
        private final Path path;
        private final long sizeInBytes;
        private final FileType type;

        PathInfo(final Path path, final long sizeInBytes, final FileType type) {
            this.path = path;
            this.sizeInBytes = sizeInBytes;
            this.type = type;
        }

        Path getPath() {
            return path;
        }

        long getSize() {
            return sizeInBytes;
        }

        FileType getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format("%s %S %sKB", type, path.toAbsolutePath(), sizeInBytes / 1024);
        }
    }

    private static long sizeOfDirectory(final Path directory) {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.mapToLong(DU::sizeOf).sum();
        } catch (IOException e) {
            return 0;
        }
    }

    private static long sizeOf(final Path path) {
        if (Files.isSymbolicLink(path)) {
            return 0;
        }
        if (Files.isDirectory(path)) {
            return sizeOfDirectory(path);
        }
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0;
        }
    }

    static SortedSet<PathInfo> contentOfDirectorySortedBySize(final Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            return Collections.emptySortedSet();
        }
        final TreeSet<PathInfo> result = new TreeSet<>((o1, o2) -> {
            final int diff = (int) (o2.getSize() - o1.getSize());
            if (diff != 0) {
                return diff;
            }
            return o2.getPath().compareTo(o1.getPath());
        });
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                .filter(x -> !Files.isSymbolicLink(x))
                .forEach((x) -> result.add(new PathInfo(x, sizeOf(x), FileType.of(x))));
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        final Path dir = Paths.get(args.length > 0 ? args[0] : ".");
        contentOfDirectorySortedBySize(dir)
            .forEach(System.out::println);
    }

}
