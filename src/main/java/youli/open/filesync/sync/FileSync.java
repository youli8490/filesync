package youli.open.filesync.sync;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.cache.DirectorySyncData;
import youli.open.filesync.sync.cache.FileSyncCache;
import youli.open.filesync.sync.cache.FileSyncData;
import youli.open.filesync.util.FileUtil;

public class FileSync {
        private static Logger logger = LoggerFactory.getLogger(FileSync.class);
        
        public void fileSync(File source, File destParent){
                if(!source.exists() || !destParent.exists()){
                        logger.error("源文件（夹）（" + source.getAbsolutePath() +
                                        "）或目标文件夹（" + destParent.getAbsolutePath() + "）不存在");
                        return;
                }
                logger.info( source.getAbsolutePath() +"-->" + destParent.getAbsolutePath() + "，开始同步");
                DirectorySyncData sourceSyncData = FileSyncCache.computeDirectorySyncCache(source);
                DirectorySyncData destSyncData = FileSyncCache.computeDirectorySyncCache(new File(destParent, source.getName()));
                updateFileToDest(sourceSyncData, destSyncData);
                logger.info("------------------删除过期文件-------------------");
                deleteOutDateFile(source.getParentFile(), new File(destParent, source.getName()));
                logger.info( source.getAbsolutePath() +"-->" + destParent.getAbsolutePath() + "，同步完成");
        }
        
        public void updateFileToDest(DirectorySyncData sourceSyncData, DirectorySyncData destSyncData){
                //同步文件
                File sourceFileParent = new File(sourceSyncData.getFilePath());
                File destFileParent = new File(destSyncData.getFilePath());
                
                Map<String, FileSyncData> sourceFileMap = sourceSyncData.getFileMap();
                Map<String, FileSyncData> destFileMap = destSyncData.getFileMap();
                
                Set<String> sourceFileSet = sourceFileMap.keySet();
                Iterator<String> sourceFileIterator = sourceFileSet.iterator();
                while(sourceFileIterator.hasNext()){
                	String fileName = sourceFileIterator.next();
                	File sourceFile = new File(sourceFileParent, fileName);
                	File destFile = new File(destFileParent, fileName);
                	if(checkFileIsSame(sourceFileMap.get(fileName), destFileMap.get(fileName))){//文件没有发生变化
                		logger.trace(sourceFile.getAbsolutePath() + "文件没有发生变化，无须同步");
                	}else{//文件发生变化
                		logger.trace(sourceFile.getAbsolutePath() + "文件发生变化，正在同步");
                		FileUtil.deleteFile(destFile);
                		 FileUtil.copyFile(sourceFile, destFileParent);
                	}
                }
               //同步目录
                Map<String, DirectorySyncData> sourceDirectoryMap = sourceSyncData.getDirectoryMap();
                Map<String, DirectorySyncData> destDirectoryMap = destSyncData.getDirectoryMap();
                Set<String> sourceDirectorySet = sourceDirectoryMap.keySet();
                Iterator<String> sourceDirectoryIterator = sourceDirectorySet.iterator();
                while(sourceDirectoryIterator.hasNext()){
                	String directoryName = sourceDirectoryIterator.next();
                	updateFileToDest(sourceDirectoryMap.get(directoryName), destDirectoryMap.get(directoryName));
                }
        }
        
        private boolean checkFileIsSame(FileSyncData source, FileSyncData dest){
                return source.getMD5().equals(dest.getMD5());
        }
        
        private void deleteOutDateFile(File sourceParent, File dest){
                File source = new File(sourceParent, dest.getName());
                if(!source.exists()){
                        logger.info(dest.getAbsolutePath() + "文件已过期，正在删除");
                        FileUtil.deleteFile(dest);
                        return;
                }
                
                if(dest.isFile())
                        return;
                
                for(File child : dest.listFiles()){
                        deleteOutDateFile(source, child);
                }
        }

        public static void main(String[] args) {
//                File source = new File("E:/软件开发技术总结");
//                File destParent = new File("G:/");
//                
//                new FileSync().fileSync(source, destParent);
                
        }

}
