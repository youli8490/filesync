package youli.open.filesync.sync.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * {@DirectorySyncData}对象标识着一个目录的缓存数据，filePath指明目录的绝对路径。
 * 目录中每个子文件的FileSyncData存储在fileMap中，
 * 目录中每个子目录的DirectorySyncData存储在directoryMap中。
 * @author youli
 *
 */
public class DirectorySyncData {
        
        private Map<String, DirectorySyncData> directoryMap;
        
        private Map<String, FileSyncData> fileMap;
        
        private String filePath;

        public DirectorySyncData() {
                directoryMap = new HashMap<String, DirectorySyncData>();
                fileMap = new HashMap<String, FileSyncData>();
        }

        /**
         * Map<String, DirectorySyncData>：
         * String：目录名
         * DirectorySyncData：该目录的文件同步缓存数据
         * @return
         */
        public Map<String, DirectorySyncData> getDirectoryMap() {
                return directoryMap;
        }

        /**
         * Map<String, FileSyncData>：
         * String：文件名
         * FileSyncData：此文件的缓存数据
         * @return
         */
        public Map<String, FileSyncData> getFileMap() {
                return fileMap;
        }

        /**
         * @return 返回缓存数据父目录的绝对文件路径
         */
        public String getFilePath() {
                return filePath;
        }

        /**
         * @param filePath 设置缓存数据父目录的绝对文件路径
         */
        public void setFilePath(String filePath) {
                this.filePath = filePath;
        }
}
