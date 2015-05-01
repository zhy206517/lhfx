package com.youa.mobile.parser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class JsonBool extends JsonValue {

	private boolean value = false;

	public JsonBool() {
	}

	public JsonBool(boolean val) {
		value = val;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toJsonString() {
		return String.valueOf(value);
	}

	protected static JsonBool parseBool(String s) {
		s = s.toLowerCase();
		if (s.equals("true")) {
			return new JsonBool(true);
		} else if (s.equals("false")) {
			return new JsonBool(false);
		} else {
			System.err.println("Boolean cast error!\n" + s);
		}
		return null;
	}

	@Override
	protected void read(DataInputStream dis) throws IOException {
		value = dis.readBoolean();
	}

	@Override
	protected void write(DataOutputStream dos) throws IOException {
		dos.writeByte(TYPE_BOOL);
		dos.writeBoolean(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
