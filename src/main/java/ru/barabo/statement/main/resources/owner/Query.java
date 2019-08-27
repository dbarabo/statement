package ru.barabo.statement.main.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "${cfgpath}/query.properties" })
public interface Query extends Config {

	@DefaultValue("select classified.nextval from dual")
	String getClassifiedNextVal();

	@DefaultValue("select PartData from od.BlankFilePart where BlankCmd = ? order by OrderNum")
	String selectReadBlobFile();

	@DefaultValue("out.rtf")
	String outRtf();

	@DefaultValue("out.txt")
	String outTxt();

	@DefaultValue("192.168.1.20")
	String afinaIP();

	@DefaultValue("192.168.1.21")
	String testIP();

	@DefaultValue("AFINA")
	String afinaSID();

	@DefaultValue("TEST")
	String testSID();

	@DefaultValue("1521")
	String port();
}
