package youli.open.filesync.log;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import youli.open.filesync.sync.EnvConfig;

import javax.swing.*;
import java.io.Serializable;

@Plugin(name = "SyncAppenderSwing", category = "Core", elementType = "appender", printObject = true)
public class SyncAppenderSwing extends AbstractAppender {

    private static final long serialVersionUID = 1L;

    private JTextArea textArea;

    protected SyncAppenderSwing(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }
    
    @PluginFactory
    public static SyncAppenderSwing createAppender(@PluginAttribute("name") String name,
                                                   @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                                   @PluginElement("Layout") Layout<?> layout,
                                                   @PluginElement("Filters") Filter filter) {
 
        if (name == null) {
            LOGGER.error("No name provided for SyncAppenderSwing");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new SyncAppenderSwing(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        if (textArea == null || !textArea.isEnabled())
        	return;
        
        final String log = new String(getLayout().toByteArray(event), EnvConfig.CONF_CHARSET);

        SwingUtilities.invokeLater(() -> {
            textArea.append(log);
        });
    }

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

}
