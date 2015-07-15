package youli.open.filesync.sync.cache;

import java.io.File;

import org.junit.Test;

public class FileSyncCacheTest {

        @Test
        public void testComputeDirectorySyncCache() {
                File file = new File("E:/eclipse-workspace/android");
                FileSyncCache.computeDirectorySyncCache(file);
        }

        @Test
        public void testDeleteDirectorySyncCache() {
                File file = new File("G:/doc");
                FileSyncCache.deleteDirectorySyncCache(file);
        }

}
