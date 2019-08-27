package ru.barabo.total.utils;

import kotlin.io.ConstantsKt;
import kotlin.io.FilesKt;
import org.apache.log4j.Logger;
import ru.barabo.total.resources.owner.CfgTotal;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Вспомогательный класс для работы с файлами - дирками
 * @author debara
 *
 */
final public class FileUtil {
	
	final static transient private Logger logger = Logger.getLogger(FileUtil.class.getName());


	public static String moveFileToSubFolder(File file, String subFolderName) {

		if (file == null || subFolderName == null || "".equals(subFolderName.trim())) {
			return "param is empty";
		}

		File dir = new File(file.getParent() + "/" + subFolderName);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File newFile = new File(dir.getAbsoluteFile() + "/" + file.getName());

		if (!file.renameTo(newFile)) {
			FilesKt.copyTo(file, newFile, true, ConstantsKt.DEFAULT_BUFFER_SIZE );
		}
		return null;
	}

	public static File clearDir(String path) {
		File backup = new File(path);

		if (!backup.exists()) {
			backup.mkdirs();
			return backup;
		}

		final File[] list = backup.listFiles();

		if (list == null || list.length == 0) {
			return backup;
		}

		for (File file : list) {
			if (file.isFile()) {
				boolean isDel = file.delete();

				if (!isDel) {
					logger.error("Not Delete=" + file.getName());
				}
			}
		}

		return backup;
	}

	public static String getAppFile() {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.info(e.getLocalizedMessage());
			logger.info(e);
		}
		
		return "IIA_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_0226";
	}
	
   /**
     * CLOB -> String Работает нормально!
     * Convert inputClob to java.lang.String
	 */
	private static java.lang.String clob2string(java.sql.Clob inputClob)
	   	throws java.sql.SQLException, java.lang.Exception   {
	       long lClobLength = inputClob.length ( );
	    int clobLength = (int)lClobLength;	// длина данных

	    if ( lClobLength != clobLength )
	     throw new Exception ( "Ошибка: Длина данных больше 2 Gb" );

	    // Преобразование. Первый символ - индекс 1 !

		return inputClob.getSubString ( 1, clobLength );
	}
	
	/**
	 * Пришла сюда из Timer-а
	 * проверяет регулярное выр-е
	 */
	private static boolean isAccept(String name, Pattern searchPattern) {

		logger.info("!name=" + name);
		final boolean isSearch = searchPattern.matcher(name).matches();
		logger.info("res=" + isSearch);

		return isSearch;
	}
	
	/**
	 * Пришла сюда из Timer-а
	 * список файлов удовл. условию. 
	 */
	private static Vector<File> search(File topDirectory, Pattern searchPattern, long lastTime) {
	        //получаем список всех объектов в текущей директории
	       final  File[] list = topDirectory.listFiles();
	            
	        Vector<File> result = new Vector<File>();
	        if(list == null) return result;
	        
	        
	        for(File file : list ) {
	        	if(file.isDirectory() || 
	        	(file.lastModified() < lastTime ) ) continue;
	        	
			logger.info("search file = " + file);
	        	
	        	if( searchPattern == null || isAccept(file.getName(), searchPattern) ) {
	        		result.add(file);
	        	}
	        }
	        return result;
	}
	
	public static List<File> searchDirectory(File topDirectory, Pattern searchPattern) {
		// получаем список всех объектов в текущей директории
		final File[] list = topDirectory.listFiles();

		List<File> result = new ArrayList<File>();
		if (list == null)
			return result;

		for (File file : list) {
			if (!file.isDirectory()) {
				continue;
			}

			if (searchPattern == null || isAccept(file.getName(), searchPattern)) {
				result.add(file);
			}
		}
		return result;
	}
	
	
	/**
	 * Переносит файлы из дир-ки в др. дир-ку по маске pattern
	 * @return null - все хорошо
	 */
	public static String removeByPattern(String fromDir, String toDir, Pattern pattern ) {
	    Vector<File> files = FileUtil.search(new File(fromDir), pattern, 0);
		
	    String val = null;
		for (File fileRen : files) {
			final File newFile = new File(toDir + "/" + fileRen.getName());
			if (newFile.exists()) {
				newFile.delete();
			}
			final boolean isRen = fileRen.renameTo(newFile);
			if(!isRen) {

				logger.error("extractArj: not Remove file " + fileRen.getAbsolutePath());
				val += CfgTotal.msg().errorNotRemove(fileRen.getAbsolutePath());
			}
		}
		return val;
	}
	
	/**
	 * собственно выполнение любой команды консоли
	 * @param cmd
	 * @return
	 */
	static public String execCmd(String cmd) {
		
		try {
			logger.debug("extractRarByMask=" + cmd);
			
		    Process pr = Runtime.getRuntime().exec(cmd);
			pr.waitFor();
		} catch (IOException e) {
			logger.error("execCmd IOException:", e);
			return e.getMessage();
			
		} catch (InterruptedException e) {
			logger.error("execCmd InterruptedException:", e);
			return e.getMessage();
		}
		return null;
	}
	
	/**
	 * собственно выполнение любой команды консоли
	 * 
	 * @param cmd
	 * @return
	 */
	static public String execCmd(String cmd, File directory) {

		try {
			logger.debug("extractRarByMask=" + cmd);

			Process pr = Runtime.getRuntime().exec(cmd, null, directory);
			pr.waitFor();
		} catch (IOException e) {
			logger.error("execCmd IOException:", e);
			return e.getMessage();

		} catch (InterruptedException e) {
			logger.error("execCmd InterruptedException:", e);
			return e.getMessage();
		}
		return null;
	}

	static public String execScript(String cmd) {

		// new ProcessBuilder("cmd", "/c","start",
		// "C:/OakOwlProject_1.0_BAT.bat").start();

		// return execCmd("cmd /c start " + cmd);

		return execDos("start " + cmd);
	}

	static public String execDosCmd(String cmd, RunnerStoper robot, long waitAfterEnd) {
		String[] execCmd = new String[] { "cmd.exe", "/C", cmd };

		logger.debug("cmd=" + cmd);

		Process process;
		try {
			process = Runtime.getRuntime().exec(execCmd);

			// any error message?
			StreamGobbler errorGobbler = new
					StreamGobbler(process.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new
					StreamGobbler(process.getInputStream(), "OUTPUT");

			errorGobbler.start();
			outputGobbler.start();
			
			if (robot != null) {
				robot.start();
			}

			process.waitFor();

			if (robot != null) {
				Thread.sleep(waitAfterEnd);
				robot.setStop();
			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException execDosCmd=" + e.getMessage());
			return e.getMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("InterruptedException execDosCmd=" + e.getMessage());
			return e.getMessage();
		}

		return null;
	}

	static public String execDos(String cmd) {
		String[] execCmd = new String[] { "cmd.exe", "/C", cmd };


		logger.debug("cmd=" + cmd);

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(execCmd);

			// any error message?
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			process.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException execDosCmd=" + e.getMessage());
			return e.getMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("InterruptedException execDosCmd=" + e.getMessage());
			return e.getMessage();
		}

		return null;
	}


	/**
	 *  convert the filename to a URI
	 * @param zipFilename
	 * @param create
	 * @return
	 * @throws IOException
	 */
	private static FileSystem createZipFileSystem(
			String zipFilename, boolean create) throws IOException { 
		
		final Path path = Paths.get(zipFilename);
		final URI uri = URI.create("jar:file:" + path.toUri().getPath());

		final Map<String, String> env = new HashMap<>();
		if (create) {
			env.put("create", "true");
		}
		return FileSystems.newFileSystem(uri, env);
	}
	
	/**
	 * создание zip-архива со списком файлов
	 * @param zipFileName
	 * @param fileNames
	 * @return
	 */
	static public String createZip(String zipFileName, String... fileNames) {
		
		try (FileSystem zipFileSystem = createZipFileSystem(zipFileName, true)) {
			final Path root = zipFileSystem.getPath("/");
			for(String filename : fileNames) {
			    final Path src = Paths.get(filename);
			    
			    File fileSrc = new File(filename);
			    			    
			    final Path dest = zipFileSystem.getPath(root.toString(),
			    		fileSrc.getName());
			    
			    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
			}      
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("file not zipped:" + zipFileName);
			return e.getMessage();
		}
		return null;
	}

	
	static public String replaceExt(String fileName, String ext) {
		final int pos = fileName.indexOf(".");
		if(pos < 0) {
			return fileName + "." + ext;
		}
		return fileName.substring(0, pos + 1) + ext;
	}
	
	static public String removeExt(String fileName) {
		final int pos = fileName.indexOf(".");
		if (pos < 0) {
			return fileName;
		}

		return fileName.substring(0, pos);
	}

	static public String writeTextFileByClob(java.sql.Clob clob, String fullFileName,
			String encoding) {

		String data = null;
		try {
			data = FileUtil.clob2string(clob);
		} catch (SQLException e) {
			logger.error("writeTextFileByClob SQLException", e);
			return e.getMessage();
		} catch (Exception e) {
			logger.error("writeTextFileByClob Exception", e);
			return e.getMessage();
		}

		return FileUtil.writeTextFile(fullFileName, data, encoding);
	}

	/**
	 * Записывает данные в файл 
	 * @param fileName
	 * @param data
	 * @param encoding
	 * @return
	 */
	static public String writeTextFile(String fileName, String data, String encoding) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding));
	        out.write(data);
	        out.close();
		} catch (IOException e) {
			logger.error("writeTextFile IOException" + fileName, e);
			return e.getMessage();
		}
		return null;
	}
	
    
	static private void searchMoveLocal(Pattern pattern, String pathLocalSearch,
			String pathLocalToMove) {

		if (pathLocalSearch == null) {
			return;
		}

		Vector<File> searches = FileUtil.search(new File(pathLocalSearch), pattern, 0);
		if (searches == null) {
			return;
		}

		for (File file : searches) {
			file.renameTo(new File(pathLocalToMove + "/" + file.getName()));
		}
	}

	/**
	 * поиск файлов в дирках pathLocalMove - если есть - переносим в pathLocal -
	 * потом в pathLocal
	 * 
	 * @param pattern
	 * @param files
	 * @param pathLocal
	 * @param pathLocalMove
	 */
	static private void searchLocal(Pattern pattern, List<String> files, String pathLocal,
			String pathLocalMove) {

		searchMoveLocal(pattern, pathLocalMove, pathLocal);
		
		Vector<File> searches = FileUtil.search(new File(pathLocal), pattern, 0);
		
		for(File file : searches) {
			files.add(file.getName());
		}
	}
    
	
	static private boolean isNewEraJzdo() {
		Calendar calendar = Calendar.getInstance();

		// до 23/01/2016- старое SZDO после новое JZDO
		calendar.set(2016, Calendar.JANUARY, 23);

		return calendar.getTime().getTime() < new Date().getTime();
	}

	
	
   /**
     * Reads all bytes from an input stream and writes them to an output stream.
     */
    private static long copy(InputStream source, OutputStream sink)
        throws IOException   {
    	
    	final int BUFFER_SIZE = 8192;
        long nread = 0L;
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        
        source.close();
        sink.close();
        
        return nread;
    }
    

}
