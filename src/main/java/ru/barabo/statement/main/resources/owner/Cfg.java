package ru.barabo.statement.main.resources.owner;

import org.aeonbits.owner.ConfigFactory;

public class Cfg {

	static {
		ConfigFactory.setProperty("cfgpath", "classpath:properties");
	}

	private static class SingletonQuery {
		static final Query query = ConfigFactory.create(Query.class);
	}

	public static Query query() {
		return SingletonQuery.query;
	}
}
