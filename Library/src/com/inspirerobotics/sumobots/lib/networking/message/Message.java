package com.inspirerobotics.sumobots.lib.networking.message;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
		Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();  
		HashMap<String, String> json = new Gson().fromJson(string, mapType);
		
		Message m = new Message(MessageType.fromString(json.get("message_type")));
		
		for (Entry<String, String> o : json.entrySet()) {
			m.addData(o.getKey(), o.getValue());
		}
		
		return m;
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
	public String toJSONString() {
		HashMap<String, String> jsonValues = new HashMap<String, String>();
		Gson gson = new Gson();
		
		jsonValues.putAll(data);
		
		jsonValues.put("message_type", type.getName());
		
		return gson.toJson(jsonValues);
	}

}
