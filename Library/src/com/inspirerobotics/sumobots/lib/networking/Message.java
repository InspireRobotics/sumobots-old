package com.inspirerobotics.sumobots.lib.networking;

import java.util.HashMap;

import com.inspirerobotics.sumobots.lib.Resources;

/**
 * This class creates and parses messages. Messages always follow the same
 * format. 
 * 
 *<b>Message Format:</b>
 * 
 * <p>[EOB] = End Of Block character 
 * <p>[US] = Unit Seperator
 * <p>[EOT] = End of Transmission
 * <p>
 * Message with no data: NAME[EOB][EOT]
 * <p>
 * Message with two pieces of data:
 * <p>
 * NAME[EOB]key1[US]value1[EOB]key2[US]value[EOB][EOT]
 * 
 * @author Noah
 */
public class Message {

	/**
	 * The Type of the Message
	 */
	private MessageType type;

	/**
	 * The data within the message
	 */
	private HashMap<String, String> data = new HashMap<String, String>();

	/**
	 * Create a message with no data attached
	 * 
	 * @param type
	 */
	public Message(MessageType type) {
		super();
		this.type = type;
	}

	/**
	 * Creates a message from a String
	 * 
	 * @param string
	 *            the string to be parsed
	 * @return the message create
	 */
	public static Message fromString(String string) {
		MessageType m = MessageType.fromString(string);

		// Remove everything except the data
		String dataString = string.substring(string.indexOf(Resources.EOB) + 1);
		HashMap<String, String> data = parseData(dataString);

		// Now that we have the data and type create the message object
		Message message = new Message(m);
		message.data = data;

		return message;
	}

	private static HashMap<String, String> parseData(String dataString) {
		// Split every block of data into a string
		String[] sets = dataString.split(Resources.EOB);
		HashMap<String, String> data = new HashMap<String, String>();

		// Parse every block of data
		for (String string : sets) {
			if(string.isEmpty())
				break;
			
			if (string.contains(Resources.US)) {
				int indexOfUS = string.indexOf(Resources.US);
				String key = string.subSequence(0, indexOfUS).toString();
				String value = string.substring(indexOfUS + 1);
				data.put(key, value);

			} else {
				System.out.println("Malformed Data: " + string);
			}
		}

		return data;
	}

	public HashMap<String, String> getDataSet() {
		return data;
	}

	public void addData(String key, String val) {
		data.put(key, val);
	}

	public Object getData(String key) {
		return data.get(key);
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type.getName() + "  Data: " + data;
	}

	/**
	 * Formats the Data into a single string using the format
	 * 
	 * @return
	 */
	public String getFormatedData() {
		StringBuilder sb = new StringBuilder();

		for (String key : data.keySet()) {
			Object value = data.get(key);
			sb.append(key + Resources.US + value + Resources.EOB);
		}

		return sb.toString();
	}

}
