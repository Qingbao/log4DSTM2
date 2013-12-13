/**
 * 
 */
package log4dstm2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import dstm2.atomic;

/**
 * The class that checks the @atomic object has modified
 * 
 * @author Qingbao Guo
 * @version 1.0
 */
public class AtomicObjectVersion implements LoggingFactory {

	private static final Logger logger = Logger
			.getLogger(AtomicObjectVersion.class); // simple log4j

	private static List<Object> previousVersion;

	// the list that stores elements of the current @atomic object
	List<Object> currentVersion;

	Object node; // node of the @atomic object
	Object nodeValue; // the primitive type of the node (e.g. int)

	/**
	 * Constructor
	 */
	public AtomicObjectVersion() {
	}

	/**
	 * @param version
	 *            the @atomic object
	 * @return the list that contains all primitive elements (e.g. int)of the @atomic
	 *         object
	 */
	public List<Object> getVersionFields(Object version) {

		currentVersion = new LinkedList<Object>();

		if (version != null) {
			// get all methods
			for (Method method : version.getClass().getMethods()) {
				method.setAccessible(true);
				// get "getXXX" method
				if (method.getName().substring(0, 3).equals("get")) {
					// only the primitive type can be added to the list
					if (method.getReturnType().isPrimitive()
							|| isRegularType(method)) {

						try {
							// invoke "getXXX" method
							nodeValue = method.invoke(version);

							if (nodeValue != null)
								currentVersion.add(nodeValue);

						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
						// get @atomic object
					} else if (method.getReturnType().isAnnotationPresent(
							atomic.class)) {

						try {
							// invoke "getXXX" method
							node = method.invoke(version);

							while (node != null) {
								for (Method method1 : node.getClass()
										.getMethods()) {
									method1.setAccessible(true);
									if (method1.getName().substring(0, 3)
											.equals("get")) {
										// only the primitive type can be added
										// to the list
										if (method1.getReturnType()
												.isPrimitive()
												|| isRegularType(method)) {

											nodeValue = method1.invoke(node);

											if (nodeValue != null)
												currentVersion.add(nodeValue);

										} else if (method1.getReturnType()
												.isAnnotationPresent(
														atomic.class)) {

											node = method1.invoke(node);
											if (node == null)
												break;
										}
									}
								}

							}
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}

					}
				}
			}
			// [0] list should be ignored
			if (!currentVersion.contains(0)) {
				
				// check the modification of the current @atomic object
				if (previousVersion != null) {
					// if an item has inserted
					if (currentVersion.size() > previousVersion.size()) {
						for (Object element : currentVersion) {
							if (!previousVersion.contains(element)) {
								int i = currentVersion.indexOf(element);
								logger.info("Insert item:"
										+ currentVersion.get(i)
										+ "  at position: " + "[" + i + "]");
							}
						}
					} else if (previousVersion.size() > currentVersion.size()) {
						// if an item has removed
						for (Object element : previousVersion) {
							if (!currentVersion.contains(element)) {
								int i = previousVersion.indexOf(element);
								logger.info("remove item:"
										+ previousVersion.get(i)
										+ "  at position: " + "[" + i + "]");
							}
						}
					}
				}
				logger.info(currentVersion);
				// for the next @atomic object
				previousVersion = currentVersion;
				return currentVersion;

			}
		}
		return currentVersion;
	}

	/**
	 * Check the modification of the given @atomic object
	 */
	public void checkModify(Object preVersion, Object curVersion) {

	}

	/**
	 * Checks whether a method return type is scalar.
	 * 
	 * @param method
	 *            reflect method to check
	 * @return whether field is legit type for an atomic object
	 */
	private boolean isRegularType(Method method) {
		return method.getReturnType().isEnum()
				|| method.getReturnType().equals(Boolean.class)
				|| method.getReturnType().equals(Character.class)
				|| method.getReturnType().equals(Byte.class)
				|| method.getReturnType().equals(Short.class)
				|| method.getReturnType().equals(Integer.class)
				|| method.getReturnType().equals(Long.class)
				|| method.getReturnType().equals(Float.class)
				|| method.getReturnType().equals(Double.class)
				|| method.getReturnType().equals(String.class);
	}
}
