package ru.barabo.total.report.rtf;

import kotlin.io.ConstantsKt;
import kotlin.io.FilesKt;
import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.statement.afina.AfinaQuery;
import ru.barabo.statement.main.resources.owner.Cfg;
import ru.barabo.total.resources.owner.CfgTotal;
import ru.barabo.total.utils.FileUtil;

import javax.swing.*;
import java.io.File;

public final class RtfReport {

	final static transient private Logger logger = Logger.getLogger(RtfReport.class.getName());

	static public String build(RtfAfinaData rtfAfinaData) {
		
		File directoryTo = getDefaultToDirectory();
		
		ResultFile txtFile = getReportData(rtfAfinaData, directoryTo);
		if(txtFile.error != null) {
			return txtFile.error;
		}
		
		ResultFile rtfTemplate = getRftTemplate(rtfAfinaData, directoryTo);
		if(rtfTemplate.error != null) {
            //noinspection ResultOfMethodCallIgnored
            txtFile.file.delete();
			return rtfTemplate.error;
		}
		
		File outFile = getOutFileName(directoryTo);
		
		String error = executeConverter(txtFile.file, rtfTemplate.file, outFile, directoryTo);
        //noinspection ResultOfMethodCallIgnored
		txtFile.file.delete();

		if (error != null) {
            //noinspection ResultOfMethodCallIgnored
			rtfTemplate.file.delete();
		}

		return error;
	}


	static private String prepareConverter(File directoryTo) {

		File toConverter = CfgTotal.name().fullPathConverterExe(directoryTo);

		if (toConverter.exists()) {
			return null;
		}

		File sourceConverter = CfgTotal.name().fullPathConverterExe(
				CfgTotal.name().getDirectoryLib());

        if (!sourceConverter.exists()) {
            sourceConverter.getParentFile().mkdirs();

            copyFromModules(sourceConverter);
        }

		if (!sourceConverter.exists()) {
			return CfgTotal.msg().fileNotFound(sourceConverter.getAbsolutePath());
		}

		FilesKt.copyTo(sourceConverter, toConverter, true, ConstantsKt.DEFAULT_BUFFER_SIZE );

		return null;
	}

	static private String executeConverter(File textFile, File rtfTemplate, File outRtf,
			File directoryTo) {

		String error = prepareConverter(directoryTo);
		if (error != null) {
			return error;
		}

		String cmd = CfgTotal.name().cmdConverterXp(directoryTo, textFile, rtfTemplate, outRtf);

		return FileUtil.execCmd(cmd, directoryTo);
	}

    static private void copyFromModules(File converter) {
	    File modulesConverter = new File("\\\\192.168.0.35\\work2\\Modules\\java\\lib\\" + converter.getName());

	    if(modulesConverter.exists()) {
            FilesKt.copyTo(modulesConverter, converter, true, ConstantsKt.DEFAULT_BUFFER_SIZE );
        }
    }
	
	static private ResultFile getRftTemplate(RtfAfinaData rtfAfinaData, File directoryTo) {

		File rtfTemplate = new File(directoryTo.getAbsolutePath() + "/"
				+ rtfAfinaData.procedureName()
				+ ".rtf");

		if (rtfTemplate.exists()) {
			return new ResultFile(rtfTemplate);
		}

        try {
            rtfTemplate = AfinaQuery.INSTANCE.selectBlobToFile(Cfg.query().selectReadBlobFile(),
                    new Object[] { rtfAfinaData.bbrId() }, rtfTemplate);
        } catch (SessionException e) {
            return new ResultFile(e.getMessage(), rtfTemplate);
        }

        return new ResultFile(rtfTemplate);
	}

	static private ResultFile getReportData(RtfAfinaData rtfAfinaData, File directoryTo) {

		File textFile = new File(directoryTo.getAbsolutePath() + "/" + rtfAfinaData.procedureName() + ".txt");

        try {
            textFile = AfinaQuery.execBbrRtf(rtfAfinaData.procedureName(), rtfAfinaData.procedureCallSql(),
                    rtfAfinaData.paramCall(), textFile, "cp1251");
        } catch (SessionException e) {
            return new ResultFile(e.getMessage(), textFile);
        }

        return new ResultFile(textFile);
	}

	static public File getDefaultToDirectory() {
		return new JFileChooser().getFileSystemView().getDefaultDirectory();
	}

	static private File getOutFileName(File directoryTo) {
		return new File(directoryTo.getAbsolutePath() + "/out" + System.currentTimeMillis()
				+ ".rtf");
	}
}

class ResultFile {
	
	String error;
	
	File file;

	ResultFile(String error, File file) {
		this.error = error;
		this.file = file;
	}

	ResultFile(File file) {
		this.file = file;
		this.error = null;
	}
}
