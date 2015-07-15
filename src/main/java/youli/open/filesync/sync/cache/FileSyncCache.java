package youli.open.filesync.sync.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import youli.open.filesync.log.LoggerFactory;
import youli.open.filesync.sync.EnvConfig;
import youli.open.filesync.sync.strategy.DefaultSyncStrategy;
import youli.open.filesync.sync.strategy.SyncStrategy;
import youli.open.filesync.util.FileUtil;
import youli.open.filesync.util.MDUtil;

public class FileSyncCache {
	private final static String Sync_Cache_File_Name = ".fileSync";
	private final static String Sync_Cache_File_Valid_Str = "This is a FileSyncCache file.";

	private static SyncStrategy syncStrategy = new DefaultSyncStrategy();

	private static Logger logger = LoggerFactory.getLogger(FileSyncCache.class);

	/**
	 * @param syncedDirectory
	 *                目录文件，若文件不存在或不是目录，返回null
	 * @return DirectorySyncData，表示当前硬盘上目录的实际缓存数据。有一个目录，就有一个{@DirectorySyncData}对象，
	 * 有一个文件，就有一个{@FileSyncData}对象，当然得满足同步策略。
	 */
	public static DirectorySyncData computeDirectorySyncCache(File syncedDirectory) {
		DirectorySyncData directorySyncData = null;
		// 文件不存在、或不是目录、再或不满足同步策略，返回null
		if (!syncedDirectory.exists() || !syncedDirectory.isDirectory() || !syncStrategy.isSync(syncedDirectory))
			return directorySyncData;

		directorySyncData = new DirectorySyncData();
		// 设置目录的绝对路径
		directorySyncData.setFilePath(syncedDirectory.getAbsolutePath());
		// 从硬盘上读取缓存数据
		boolean conflictWithUserFile = initDirectorySyncData(syncedDirectory, directorySyncData);
		// 硬盘到内存的同步
		File[] files = syncedDirectory.listFiles();
		// 是否需要回写到硬盘
		boolean needSave = false;
		for (File f : files) {
			if (f.isDirectory()) {
				DirectorySyncData childDirectorySyncData = computeDirectorySyncCache(f);
				if(childDirectorySyncData != null)
					directorySyncData.getDirectoryMap().put(f.getName(), childDirectorySyncData);
			}
			// 如果缓存文件不与用户文件冲突，则本目录进行同步
			else if (f.isFile() && !conflictWithUserFile && !f.getName().equals(Sync_Cache_File_Name)) {
				FileSyncData fileSyncData = directorySyncData.getFileMap().get(f.getName());
				if (fileSyncData == null || fileSyncData.getDate() != f.lastModified()){
					directorySyncData.getFileMap().put(f.getName(), computeFileSyncCache(f));
					needSave = true;
				}
			}
		}
		// 内存到硬盘的同步
		Set<String> fileCacheSet = directorySyncData.getFileMap().keySet();
		String[] fileCacheArray = fileCacheSet.toArray(new String[0]);
		for (String childFileName : fileCacheArray) {
			if (!new File(syncedDirectory, childFileName).exists())
				directorySyncData.getFileMap().remove(childFileName);
		}
		// 将缓存数据写入到硬盘上
		if (needSave)
			saveDirectorySyncData(syncedDirectory, directorySyncData);

		return directorySyncData;
	}

	/**
	 * 保存最新的文件MD信息
	 * 此处不再检测是否会有缓存文件冲突，因为若有缓存文件冲突，此时DirectorySyncData的文件同步缓存一定为空
	 * @param file
	 * @param directorySyncData
	 */
	private static void saveDirectorySyncData(File file, DirectorySyncData directorySyncData) {
		File syncFile = new File(file, Sync_Cache_File_Name);

		logger.debug("正在写入" + syncFile.getAbsolutePath());
		if (directorySyncData.getFileMap().size() > 0) {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;

			try {
				fos = new FileOutputStream(syncFile);
				osw = new OutputStreamWriter(fos, EnvConfig.CONF_CHARSET);
				bw = new BufferedWriter(osw);

				bw.write(Sync_Cache_File_Valid_Str);
				bw.newLine();

				Collection<FileSyncData> fileSyncSet = directorySyncData.getFileMap().values();
				for (FileSyncData fileSyncData : fileSyncSet) {
					bw.write(fileSyncData.toString());
					bw.newLine();
				}
			} catch (FileNotFoundException e) {
				logger.error(e);
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			} finally {
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if (osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
	}

	/**
	 * 每个目录下都会有一个{Sync_Cache_File_Suffix}的缓存文件，如果与用户文件有同名冲突（返回true），
	 * 需要用户手工同步，本目录下的文件
	 * 
	 * @param syncedDirectory
	 *                待同步目录
	 * @param directorySyncData
	 * @return true 有与缓存文件重名的用户文件；其它情况返回false。
	 */
	private static boolean initDirectorySyncData(File syncedDirectory, DirectorySyncData directorySyncData) {
		File syncCacheFile = new File(syncedDirectory, Sync_Cache_File_Name);
		if (!syncCacheFile.exists())
			return false;

		logger.debug("正在读取" + syncCacheFile.getAbsolutePath());
		List<String> syncCacheList = FileUtil.readConfigureFile(syncCacheFile.getAbsolutePath(), EnvConfig.CONF_CHARSET);
		if (syncCacheList == null || syncCacheList.size() == 0)
			return false;
		String firstRowStr = syncCacheList.get(0);
		if (!Sync_Cache_File_Valid_Str.equals(firstRowStr)) {// 检测用户文件与缓存文件重名
			logger.error(syncedDirectory.getAbsolutePath() + "目录下存在用户的" + Sync_Cache_File_Name + "文件，忽略同步此目录");
			return true;
		}
		for (int i = 1; i < syncCacheList.size(); i++) {
			String str = syncCacheList.get(i);
			FileSyncData fileSyncData = FileSyncData.instance(str);
			directorySyncData.getFileMap().put(fileSyncData.getFileName(), fileSyncData);
		}
		
		return false;
	}

	private static FileSyncData computeFileSyncCache(File file) {
		FileSyncData fileSyncData = null;
		if (!file.exists() || !file.isFile() || !syncStrategy.isSync(file))
			return fileSyncData;

		logger.debug("计算（" + file.getAbsolutePath() + "）文件的FileSyncData");
		fileSyncData = new FileSyncData();

		fileSyncData.setFileName(file.getName());
		fileSyncData.setLength(file.length());
		fileSyncData.setDate(file.lastModified());
		fileSyncData.setMD5(MDUtil.computeMD5OfFile(file));

		return fileSyncData;
	}

	/**
	 * 删除FileSync的缓存文件
	 * 
	 * @param file
	 */
	public static void deleteDirectorySyncCache(File file) {
		if (!file.exists() || !file.isDirectory())
			return;
		File syncCacheFile = new File(file, Sync_Cache_File_Name);
		if (syncCacheFile.exists() && checkSyncCachFile(syncCacheFile)) {
			syncCacheFile.delete();
		}

		File[] children = file.listFiles();
		for (File child : children) {
			if (child.isDirectory())
				deleteDirectorySyncCache(child);
		}
	}

	private static boolean checkSyncCachFile(File syncCacheFile) {
		FileInputStream fis = null;
		Reader reader = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(syncCacheFile);
			reader = new InputStreamReader(fis, EnvConfig.CONF_CHARSET);
			br = new BufferedReader(reader);
			String firstRowStr = br.readLine();
			if (Sync_Cache_File_Valid_Str.equals(firstRowStr))
				return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return false;
	}

}
