/*
 * Copyright by Intland Software
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Intland Software. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Intland.
 */

package org.nascif.jspwiki.plugin.imagegen;

public class GraphvizIsNotAvailableException extends Error {
	private static final long serialVersionUID = 1L;

	public GraphvizIsNotAvailableException(String msg, Throwable t) {
		super(msg, t);
	}
}
