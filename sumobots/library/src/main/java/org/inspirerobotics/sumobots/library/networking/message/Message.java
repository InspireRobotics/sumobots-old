package org.inspirerobotics.sumobots.library.networking.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.inspirerobotics.sumobots.library.InternalLog;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class Message {

	private static final Logger logger = InternalLog.getLogger();

	private MessageType type;

	private HashMap<String, String> data = new HashMap<String, String>();

	public Message(MessageType type) {
		super();
		this.type = type;
	}

	public static Message fromString(String string) {
		Type mapType = new TypeToken<HashMap<String, String>>() {
		}.getType();
		HashMap<String, String> json = new Gson().fromJson(string, mapType);

		Message m = new Message(getMessageTypeFromData(json));

		for (Entry<String, String> o : json.entrySet()) {
			m.addData(o.getKey(), o.getValue());
		}

		return m;
	}

	private static MessageType getMessageTypeFromData(HashMap<String, String> data) {
		try {
			return MessageType.fromString(data.get("message_type"));
		} catch (UnknownMessageTypeException e) {
			logger.severe("Failed to recognize message type! Exception: " + e.getMessage());
		}

		return MessageType.UNKNOWN;
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
		return type.getName() + " Data: " + data;
	}

	public String toJSONString() {
		HashMap<String, String> jsonValues = new HashMap<String, String>();
		Gson gson = new Gson();

		jsonValues.putAll(data);

		jsonValues.put("message_type", type.getName());

		return gson.toJson(jsonValues);
	}

}
