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
                        logger.error("源文件夹（" + source.getAbsolutePath() +
                                        "）或目标文件夹（" + destParent.getAbsolutePath() + "）不存在");
                        return;
                }
                logger.info( source.getAbsolutePath() +"-->" + destParent.getAbsolutePath() + "，开始同步");
                DirectorySyncData sourceSyncData = FileSyncCache.computeDirectorySyncCache(source);
                DirectorySyncData destSyncData = FileSyncCache.computeDirectorySyncCache(new File(destParent, source.getName()));
                if(destSyncData != null)
                        updateFileToDest(sourceSyncData, destSyncData);
                else
                        FileUtil.copyFile(source, destParent);
                logger.info("------------------删除过期文件-------------------");
                destSyncData = FileSyncCache.computeDirectorySyncCache(new File(destParent, source.getName()));
                deleteOutDateFile(sourceSyncData, destSyncData);
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
                	DirectorySyncData destChildDirectorySyncData = destDirectoryMap.get(directoryName);
                	if(destChildDirectorySyncData != null)
                	        updateFileToDest(sourceDirectoryMap.get(directoryName), destChildDirectorySyncData);
                	else
                	        FileUtil.copyFile(new File(sourceFileParent, directoryName), destFileParent);
                }
        }
        
        private boolean checkFileIsSame(FileSyncData source, FileSyncData dest){
                if(dest == null)
                        return false;
                return source.getMD5().equals(dest.getMD5());
        }
        
        private void deleteOutDateFile(DirectorySyncData sourceSyncData, DirectorySyncData destSyncData){
                File dest = new File(destSyncData.getFilePath());
                if(sourceSyncData == null){
                        FileUtil.deleteFile(dest);
                }
                //删除过期文件
                Map<String, FileSyncData> sourceFileMap = sourceSyncData.getFileMap();
                Map<String, FileSyncData> destFileMap = destSyncData.getFileMap();
                
                Set<String> destFileSet = destFileMap.keySet();
                Iterator<String> destFileIterator = destFileSet.iterator();
                while(destFileIterator.hasNext()){
                        String destChildFileName = destFileIterator.next();
                        File destChildFile = new File(dest, destChildFileName);
                        if(sourceFileMap.get(destChildFileName) == null)
                                FileUtil.deleteFile(destChildFile);
                }
                //删除过期目录
                Map<String, DirectorySyncData> sourceDirectoryMap = sourceSyncData.getDirectoryMap();
                Map<String, DirectorySyncData> destDirectoryMap = destSyncData.getDirectoryMap();
                Set<String> destDirectorySet = destDirectoryMap.keySet();
                Iterator<String> destDirectoryIterator = destDirectorySet.iterator();
                while(destDirectoryIterator.hasNext()){
                        String destChildDirectoryName = destDirectoryIterator.next();
                        deleteOutDateFile(sourceDirectoryMap.get(destChildDirectoryName), destDirectoryMap.get(destChildDirectoryName));
                }
        }

        public static void main(String[] args) {
//                File source = new File("E:/软件开发技术总结");
//                File destParent = new File("G:/");
//                
//                new FileSync().fileSync(source, destParent);
                
        }

}
