package org.hypergraphdb.app.sail.exceptions;

import org.openrdf.sail.SailException;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * created Jan 29, 2010  - 2:45:13 PM
 *
 * @author IanHolsman
 *         Copyright (C) 2010 by Aol. All Rights Reserved.
 */
public class HyperGraphException extends SailException {
	//private static Logger logger = LoggerFactory.getLogger(HyperGraphException.class);

	public HyperGraphException(String msg) {
		super(msg);
	}

	public HyperGraphException(String msg, Exception e) {
		super(msg, e);
	}

	public HyperGraphException(Exception e) {
		super(e);
	}

}
