package youli.open.filesync.sync.cache;

import java.io.File;

import org.junit.Test;

import youli.open.filesync.sync.strategy.DefaultSyncStrategy;

public class FileSyncCacheTest {

        @Test
        public void testComputeDirectorySyncCache() {
                File file = new File("E:/eclipse-workspace/android");
                FileSyncCache.computeDirectorySyncCache(file, DefaultSyncStrategy.createDefaultSyncStrategy());
        }

        @Test
        public void testDeleteDirectorySyncCache() {
                File file = new File("G:/doc");
                FileSyncCache.deleteDirectorySyncCache(file);
        }

}
