package ru.barabo.total.resources.owner;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

import java.io.File;

@Sources({ "${cfgtotal}/name.properties" })
public interface Name extends Config {


	@DefaultValue("Convert.exe")
	String converterExe();

	default File fullPathConverterExe(File directory) {
		return new File(directory.getAbsolutePath() + "/" + converterExe());
	}

	@DefaultValue("lib")
	String lib();

	default File getDirectoryLib() {
		return new File(getDirectoryJar() + "/" + lib());
	}

	default String getDirectoryJar() {
		return new File(Name.class.getProtectionDomain().getCodeSource().getLocation()
				.getPath()).getParentFile().getPath();
	}

	default String cmdConverter(File directory, File txtFile, File templateRtf, File outRtf) {
		
		return fullPathConverterExe(directory) + " \"" + txtFile.getAbsolutePath() + "\" \"" +
				templateRtf.getAbsolutePath() + "\" \"" + outRtf.getAbsolutePath() + "\" /p";
	}

	/**
	 * ковертер не понимает длинных путей, поэтому меняем текущую дирку ему
	 * 
	 * @param txtFile
	 * @param templateRtf
	 * @param outRtf
	 * @return
	 */
	default String cmdConverterXp(File directory, File txtFile, File templateRtf, File outRtf) {

		return fullPathConverterExe(directory) + " " + txtFile.getName() + " "
				+ templateRtf.getName() + " "
				+ outRtf.getName() + " /p";
	}
}
