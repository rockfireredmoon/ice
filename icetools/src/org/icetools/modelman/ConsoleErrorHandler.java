package org.icetools.modelman;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConsoleErrorHandler implements ErrorHandler {
	private Console console;

	public ConsoleErrorHandler(Console console) {
		this.console = console;
	}

	private String getParseExceptionInfo(SAXParseException spe) {
		String systemId = spe.getSystemId();
		if (systemId == null) {
			systemId = "null";
		}
		String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
		return info;
	}

	public void warning(SAXParseException spe) throws SAXException {
		console.message("Warning: " + getParseExceptionInfo(spe));
	}

	public void error(SAXParseException spe) throws SAXException {
		String message = "Error: " + getParseExceptionInfo(spe);
		console.error("XML parse failed.", spe);
//		throw new SAXException(message);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		String message = "Fatal Error: " + getParseExceptionInfo(spe);
		console.error("Fatal parsing error.", spe);
		throw new SAXException(message);
	}
}