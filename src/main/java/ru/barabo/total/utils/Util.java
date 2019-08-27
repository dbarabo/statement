package ru.barabo.total.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Date;

public class Util {
	
	final static transient private Logger logger = Logger.getLogger(Util.class.getName());
	
	static private final String H_CARD_ = "H:/КартСтандарт/"; // "C:/КартСтандарт/";
	
	static private final String H_CARD_IN_ = H_CARD_ + "in/";
	
	static private final String H_CARD_OUT_ = H_CARD_ + "out/";
	
	static public <T> boolean isExistsNullElement(T[] row) {

		if (row == null || row.length == 0) {
			return true;
		}

		for (T item : row) {

			if (item == null) {
				return true;
			}
		}

		return false;
	}

	static public String getPathFileOut() {
		String path = H_CARD_OUT_ + getDayPathDate(new Date());
		
		final File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		return path;
	}
	
	private static String getDayPathDate(Date date) {
		return String.format("%tY/%tm/%td", date, date, date);
	}

	   /**
	    * CLOB -> String Работает нормально!
	    * Convert inputClob to java.lang.String
	 */
	static public java.lang.String clob2string ( java.sql.Clob inputClob )
	   	throws java.sql.SQLException, java.lang.Exception   {
	       long lClobLength = inputClob.length ( );
	    int clobLength = (int)lClobLength;	// длина данных

	    if ( lClobLength != clobLength )
	     throw new Exception ( "Ошибка: Длина данных больше 2 Gb" );

	    // Преобразование. Первый символ - индекс 1 !

		return inputClob.getSubString ( 1, clobLength );
	}
	
	/**
	 * Записывает данные в файл 
	 */
	static public String writeTextFile(String fileName, String data, String encoding) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding/*"Cp866"*/));
	        out.write(data);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("export IOException:" + e.getMessage());
			return e.getMessage();
		}
		return null;
	}
	
	// public static String sftpUploadTest(File file) {
	// String SFTPHOST = "192.168.0.33";
	// int SFTPPORT = 22;
	// String SFTPUSER = "zdo";
	// String SFTPPASS = "zdo";
	// String SFTPWORKINGDIR = "/home/zdo/clients/0226/doc/out/notype"; // сюда
	// складываем
	//
	// Session session = null;
	// Channel channel = null;
	// ChannelSftp channelSftp = null;
	//
	// try{
	// JSch jsch = new JSch();
	// session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
	// session.setPassword(SFTPPASS);
	// java.util.Properties config = new java.util.Properties();
	// config.put("StrictHostKeyChecking", "no");
	// session.setConfig(config);
	// session.connect();
	// channel = session.openChannel("sftp");
	// channel.connect();
	// channelSftp = (ChannelSftp)channel;
	// channelSftp.cd(SFTPWORKINGDIR);
	// //File f = new File(FILETOTRANSFER);
	// channelSftp.put(new FileInputStream(file), file.getName());
	// logger.info("ok " + file.getName() );
	//
	// }catch(Exception ex){
	// ex.printStackTrace();
	// logger.error("sftpUploadTest error=" + ex.getLocalizedMessage());
	// logger.error(ex);
	// return ex.getLocalizedMessage();
	// }
	//
	// return null;
	// }
	/*
	private void sftpDownloadTest(String fileName, String localPath_) {
		String SFTPHOST = "192.168.0.33";
		int    SFTPPORT = 22;
		String SFTPUSER = "zdo";
		String SFTPPASS = "zdo";
		String SFTPWORKINGDIR = "/home/zdo/clients/0226/doc/out/notype/20160408";//"/home/zdo/clients/0226/doc/in/unknown"; // отсюда забираем
		
		Session     session     = null;
		Channel     channel     = null;
		ChannelSftp channelSftp = null;
		
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;
			channelSftp.cd(SFTPWORKINGDIR);
			
			long count = copy( new BufferedInputStream(channelSftp.get(fileName)), 
					        new FileOutputStream(new File(localPath_ + fileName) ) );
			
			channelSftp.rm(fileName);
			
			logger.info(count);
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			logger.error(ex);
		}
	}
	*/
	
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
	
	private void sftpLsTest() {
		String SFTPHOST = "192.168.0.33";
		int    SFTPPORT = 22;
		String SFTPUSER = "zdo";
		String SFTPPASS = "zdo";
		String SFTPWORKINGDIR = "/home/zdo/clients/0226/doc/out/notype/20160408";//"/home/zdo/clients/0226/doc/in/unknown"; // отсюда забираем
		
		String FILETOTRANSFER = "c:/test.tst";
		
		/*Session     session     = null;
		Channel     channel     = null;
		ChannelSftp channelSftp = null;
		
		try{
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp)channel;
			channelSftp.cd(SFTPWORKINGDIR);
			
			Vector<com.jcraft.jsch.ChannelSftp.LsEntry> files = channelSftp.ls(SFTPWORKINGDIR);
			if(files == null) return;
			
			for (com.jcraft.jsch.ChannelSftp.LsEntry obj : files) {
				logger.info( obj.getFilename() );
				//logger.info( obj.getLongname() );
			}
			}catch(Exception ex){
				ex.printStackTrace();
				logger.error(ex);
			}*/
	}

}
