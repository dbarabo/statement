package ru.barabo.total.resources.owner;

import org.aeonbits.owner.ConfigFactory;

public class CfgTotal {

	static {
		ConfigFactory.setProperty("cfgtotal", "classpath:ru/barabo/resources/properties");
	}

	private static class SingletonQuery {
		public static final Query query = ConfigFactory.create(Query.class);
	}

	private static class SingletonMsg {
		public static final Msg msg = ConfigFactory.create(Msg.class);
	}

	private static class SingletonName {
		public static final Name name = ConfigFactory.create(Name.class);
	}

	public static Name name() {
		return SingletonName.name;
	}

	public static Query query() {
		return SingletonQuery.query;
	}

	public static Msg msg() {
		return SingletonMsg.msg;
	}
}

