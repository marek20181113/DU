import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.SortedSet;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DUTest {

    @Test
    public void testSampleTreeStructure() throws IOException {
        // given
        final Path path = Paths.get("src/test/resources/DUTest_directory");
        final File dir = path.toFile();

        // when
        final SortedSet<DU.PathInfo> files = DU.contentOfDirectorySortedBySize(path);
        final Iterator<DU.PathInfo> iterator = files.iterator();
        final DU.PathInfo file_201b = iterator.next();
        final DU.PathInfo testdir = iterator.next();
        final DU.PathInfo file_1b = iterator.next();
        final DU.PathInfo emptyDir = iterator.next();

        // then
        assertFalse("Iterator should not contain any more directories", iterator.hasNext());

        assertEquals("File file_201b is expected to have 201 bytes", 201, file_201b.getSize());
        assertEquals("File file_201b is expected to be of type FILE", DU.FileType.FILE, file_201b.getType());

        assertEquals("Dir testdir is expected to have 2 byte", 2, testdir.getSize());
        assertEquals("Dir testdir is expected to be of type DIR", DU.FileType.DIR, testdir.getType());

        assertEquals("File file_1b is expected to have 1 byte", 1, file_1b.getSize());
        assertEquals("File file_1b is expected to be of type FILE", DU.FileType.FILE, file_1b.getType());

        assertEquals("Dir emptyDir is expected to have 0 byte", 0, emptyDir.getSize());
        assertEquals("Dir emptyDir is expected to be of type DIR", DU.FileType.DIR, emptyDir.getType());
    }
}