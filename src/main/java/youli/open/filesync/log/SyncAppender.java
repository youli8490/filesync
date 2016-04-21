package youli.open.filesync.log;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import youli.open.filesync.sync.EnvConfig;

@Plugin(name = "SyncAppender", category = "Core", elementType = "appender", printObject = true)
public class SyncAppender extends AbstractAppender {

    private static final long serialVersionUID = 1L;
    
    private Text text; 
    
    protected SyncAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }
    
    @PluginFactory
    public static SyncAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                              @PluginElement("Layout") Layout<?> layout,
                                              @PluginElement("Filters") Filter filter) {
 
        if (name == null) {
            LOGGER.error("No name provided for SyncAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new SyncAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        if (text == null || text.isDisposed())
        	return;
        
        final String log = new String(getLayout().toByteArray(event), EnvConfig.CONF_CHARSET);
        Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				if (text == null || text.isDisposed())
		        	return;
				text.append(log);
			}
		});
    }

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

}
