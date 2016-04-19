package youli.open.filesync.sync.strategy;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class DefaultSyncStrategyTest {

	@Test
	public void test() {
		DefaultSyncStrategy strategy = DefaultSyncStrategy.createDefaultSyncStrategy();
		File file = new File("conf/sync_strategy_white.conf");
		Assert.assertTrue(strategy.isSync(file));
	}

}
