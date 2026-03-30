package net.dflmngr.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LoggingUtils {

	private final Logger logger;
	private final String process;

	public LoggingUtils(String process) {
		this.process = process;
		MDC.put("handler", process);

		boolean logToLoki = Boolean.parseBoolean(System.getenv().getOrDefault("LOG_TO_LOKI", "false"));

		if (logToLoki) {
			logger = LoggerFactory.getLogger("loki-logger");
		} else {
			logger = LoggerFactory.getLogger("stdout-logger");
		}
	}

	public void log(String level, String msg, Object... arguments) {
		switch (level) {
			case "info"  -> { if (logger.isInfoEnabled())  logger.info(callerMsg(msg),  prepend(process, arguments)); }
			case "warn"  -> { if (logger.isWarnEnabled())  logger.warn(callerMsg(msg),  prepend(process, arguments)); }
			case "error" -> { if (logger.isErrorEnabled()) logger.error(callerMsg(msg), prepend(process, arguments)); }
			default      -> { if (logger.isDebugEnabled()) logger.debug(callerMsg(msg), prepend(process, arguments)); }
		}
	}

	public void logException(String msg, Throwable ex) {
		logger.error("[{}] {}", process, msg, ex);
	}

	private String callerMsg(String msg) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
		String shortClass = caller.getClassName();
		shortClass = shortClass.substring(shortClass.lastIndexOf('.') + 1);
		return "[{}][" + shortClass + "." + caller.getMethodName() + "(Line:" + caller.getLineNumber() + ")] - " + msg;
	}

	private Object[] prepend(Object first, Object[] rest) {
		Object[] args = new Object[rest.length + 1];
		args[0] = first;
		System.arraycopy(rest, 0, args, 1, rest.length);
		return args;
	}
}
