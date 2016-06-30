/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConsoleErrorHandler implements ErrorHandler {

	public final static Logger LOG = Logger.getLogger(ConsoleErrorHandler.class.getName());

	private String getParseExceptionInfo(SAXParseException spe) {
		String systemId = spe.getSystemId();
		if (systemId == null) {
			systemId = "null";
		}
		String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
		return info;
	}

	public void warning(SAXParseException spe) throws SAXException {
		LOG.warning("Warning: " + getParseExceptionInfo(spe));
	}

	public void error(SAXParseException spe) throws SAXException {
		String message = "Error: " + getParseExceptionInfo(spe);
		LOG.log(Level.SEVERE, "XML parse failed.", spe);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		String message = "Fatal Error: " + getParseExceptionInfo(spe);
		LOG.log(Level.SEVERE, "Fatal parsing error.", spe);
		throw new SAXException(message);
	}
}