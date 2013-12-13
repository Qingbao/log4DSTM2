/**
 * 
 */
package log4dstm2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import dstm2.Transaction;
import dstm2.benchmark.List;
import dstm2.benchmark.List.INode;

/**
 * @author Qingbao Guo
 * @version 1.0
 */
public class TestListLogAction {

	private String listLog;
	private static final Logger logger = Logger
			.getLogger(TestListLogAction.class);

	public String basicLogInfo(Object version, Transaction me) {

		List.INode currentVersion = (INode) version; // get Version
		LinkedList list = new LinkedList(); // save <type> int </type> object
											// version to a LinkedList

		while (currentVersion != null) {
			list.add(currentVersion.getValue());
			currentVersion = currentVersion.getNext();
		}

		listLog = me.toString();

		if (list.size() != 1) {
			logger.info(listLog);
			logger.info(list);
		}

		return listLog;
	}

	public String reflectObject(Object version) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		// get object class type
		Class<? extends Object> classType = version.getClass();

		// make a copy of the object
		// Object objectCopy = classType.getConstructor(new
		// Class[]{}).newInstance(new Object[]{});
		Object objectCopy = version;
		// get all attributes of the object
		Field[] fields = classType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {

			// get corresponding attribute
			Field field = fields[i];

			field.setAccessible(true);

			String fieldName = field.getName();
			String stringLetter = fieldName.substring(0, 1).toUpperCase();

			if (fieldName.contains("$") == false && stringLetter.contains("V")) {

				// get corresponding method names "setXXX();" and "getXXX();"
				String getName = "get" + stringLetter + fieldName.substring(1);
				logger.info(getName);
				// String setName = "set" + stringLetter+
				// fieldName.substring(1);

				// get corresponding methods
				Method getMethod = classType.getMethod(getName, new Class[] {});
				// Method setMethod = classType.getMethod(setName, new
				// Class[]{});

				final Method method = version.getClass().getMethod(getName);
				// invoke the methods
				Object value1 = method.invoke(objectCopy);

				logger.info(value1);

			}

		}

		return listLog;
	}

	// Constructor
	public TestListLogAction() {
	}

	public String reflectObject(Object version, Object value)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	public void log4dstm2(Object version) {

		if (version == null)
			return;

		Field[] fields = version.getClass().getDeclaredFields();
		for (int j = 0; j < fields.length; j++) {
			fields[j].setAccessible(true);

			System.out.print(fields[j].getName() + ",");

			if (fields[j].getType().getName()
					.equals(java.lang.String.class.getName())) {
				// String type
				try {
					System.out.print(fields[j].get(version));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (fields[j].getType().getName()
					.equals(java.lang.Integer.class.getName())
					|| fields[j].getType().getName().equals("int")) {
				// Integer type
				try {
					System.out.println(fields[j].getInt(version));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		System.out.println();
	}

}
