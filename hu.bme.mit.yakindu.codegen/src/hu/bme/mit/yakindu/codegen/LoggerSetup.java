package hu.bme.mit.yakindu.codegen;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerSetup {

	public static void doSetup() {
		final ConsoleAppender console = new ConsoleAppender();
		final String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.ERROR);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}

}
