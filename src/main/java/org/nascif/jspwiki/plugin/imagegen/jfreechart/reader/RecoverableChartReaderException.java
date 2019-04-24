/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.nascif.jspwiki.plugin.imagegen.jfreechart.reader;

/**
 * Specialized version of {@link ChartReaderException} for those cases,
 * where this is not a fatal error, but some normally missing data or other
 * recoverable case.
 *
 * @author <a href="mailto:aron.gombas@intland.com">Aron Gombas</a>
 */
public class RecoverableChartReaderException extends ChartReaderException {
  private static final long serialVersionUID = 1L;

  public RecoverableChartReaderException(int line, String msg) {
	super(line, msg);
  }

  public RecoverableChartReaderException(int line, Throwable t) {
	super(line, t);
  }
}
