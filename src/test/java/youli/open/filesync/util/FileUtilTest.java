package youli.open.filesync.util;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void testDeleteFile() {
		String filePath = "E:/任务";
		FileUtil.deleteFile(filePath);
	}

}
