/**
 * 
 */
package log4dstm2;

import java.util.List;

/**
 * Interface for logging classes.
 * 
 * @author Qingbao Guo
 * @version 1.0
 */
public interface LoggingFactory {

	/**
	 * @param version
	 *            the @atomic object
	 * @return the list that contains all primitive elements (e.g. int)of the @atomic
	 *         object
	 */
	public List<Object> getVersionFields(Object version);

	/**
	 * Check the modification of the given @atomic object
	 */
	/**
	 * @param preVersion
	 *            previous version of the @atomicc object
	 * @param curVersion
	 *            current version of the @atomic object
	 */
	public void checkModify(Object preVersion, Object curVersion);

}
