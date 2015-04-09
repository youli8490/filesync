package youli.open.filesync.sync.cache;

import java.io.File;

import org.junit.Test;

public class FileSyncCacheTest {

        @Test
        public void testComputeDirectorySyncCache() {
                File file = new File("E:\\软件开发技术总结");
                FileSyncCache.computeDirectorySyncCache(file);
        }

        @Test
        public void testDeleteDirectorySyncCache() {
                File file = new File("E:\\软件开发技术总结");
                FileSyncCache.deleteDirectorySyncCache(file);
        }

}
