package ru.barabo.statement.jexel;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Экспорт данных в эксель книгу
 * @author debara
 *
 */
public class ExportExtract {
	
	transient static private final Logger logger = Logger.getLogger(ExportExtract.class.getName());

	private String valuta = "";

	public void export(File file, Vector<Object[]> data, String newFile,
			String fnsName, String fnsAddress, String fnsRequest, boolean isShowRestEveryDay) {
		WritableSheet sheet = null;

		WritableWorkbook copy = null;
		Workbook workbook0 = null;
		try {
			workbook0 = Workbook.getWorkbook(file);

			copy = Workbook.createWorkbook(new File(newFile), workbook0); //создание копии файла 4read.xls
			
			if(copy == null) {
				logger.error("ExportExtract export copy = null ");
			}

			assert copy != null;
			sheet = copy.getSheet(0);

		} catch (Exception e) {
			logger.error("export IOException " + e.getMessage());

			JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);

		}

		assert sheet != null;
		fillHead(sheet, data.get(0), fnsName, fnsAddress, fnsRequest);
		
		exportRows(sheet, data, isShowRestEveryDay);

		workbook0.close();
		try {
			copy.write();
			copy.close();
		} catch (Exception e) {
			logger.error("export workbook0.close IOException " + e.getLocalizedMessage());

			JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Заполняем шапку
	 */
	private void fillHead(WritableSheet sheet, Object[] headRow,
			String fnsName, String fnsAddress, String fnsRequest) {

		valuta = (String) headRow[2];

		//установка шрифта
		WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
		try {
			arial12BoldFormat.setAlignment(Alignment.LEFT); //выравнивание по центру
			arial12BoldFormat.setWrap(false); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			//arial12BoldFormat.setBorder(Border., BorderLineStyle.MEDIUM); //рисуем рамку
		
			String text = (headRow[8] == null) ? "" : headRow[8].toString(); // наименование
			Label label = new Label(1, 17, text, arial12BoldFormat); 
			sheet.addCell(label);
			
			// Дата с
			text = (headRow[1] == null) ? "" : headRow[1].toString(); 
			label = new Label(7, 29, text, arial12BoldFormat); 
			sheet.addCell(label);
			
			// Дата по
			text = (headRow[4] == null) ? "" : headRow[4].toString(); 
			label = new Label(13, 29, text, arial12BoldFormat); 
			sheet.addCell(label);
			
			// Дата и номер запроса
			label = new Label(17, 15, fnsRequest, arial12BoldFormat); 
			sheet.addCell(label);
			
			arial12BoldFormat = new WritableCellFormat(arial12ptBold);
			arial12BoldFormat.setAlignment(Alignment.LEFT); //выравнивание по центру
			arial12BoldFormat.setWrap(false); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.THIN); //рисуем рамку
						
			text = (headRow[9] == null) ? "" : headRow[9].toString(); // ИНН
			for(int col = 0; col < text.length(); col++) {
				label = new Label(3 + col, 21, text.substring(col, col+1), arial12BoldFormat);
				sheet.addCell(label);
			}
			
			text = (headRow[10] == null) ? "" : headRow[10].toString(); // КПП
			for(int col = 0; col < text.length(); col++) {
				label = new Label(17 + col, 21, text.substring(col, col+1), arial12BoldFormat);
				sheet.addCell(label);
			}
			
			text = (headRow[11] == null) ? "" : headRow[11].toString(); // СЧЕТ
			for(int col = 0; col < text.length(); col++) {
				label = new Label(3 + col, 25, text.substring(col, col+1), arial12BoldFormat);
				sheet.addCell(label);
			}
			
			// Валюта
			for(int col = 0; col < 3; col++) {
				label = new Label(col + 2, 27, text.substring(col+5, col+6), arial12BoldFormat);
				sheet.addCell(label);
			}
						
			arial12BoldFormat = new WritableCellFormat(arial12ptBold);
			arial12BoldFormat.setAlignment(Alignment.RIGHT); //выравнивание по центру
			arial12BoldFormat.setWrap(false); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.NONE, BorderLineStyle.THIN); //рисуем рамку
			// ФНС
			label = new Label(34, 3, fnsName, arial12BoldFormat); 
			sheet.addCell(label);
			
			// ФНС адрес
			label = new Label(34, 4, fnsAddress, arial12BoldFormat); 
			sheet.addCell(label);
		
		} catch (WriteException e) {
			logger.error("fillHead WriteException ", e);
		}
	}
	
	static final transient private String ROW_REST_OUT = "%s Входящий остаток: %s %s";

	private Label showRestEveryDay(String operDate, String priorOperDate, Object restValue, int column, int row, WritableCellFormat formatFont) {
		if(operDate == null || (operDate.equals(priorOperDate))) {
			return null;
		}

		String txtRest = String.format(ROW_REST_OUT, operDate, restValue, valuta);

		return new Label(column, row, txtRest, formatFont);
	}
	
	private void exportRows(WritableSheet sheet, Vector<Object[]> data, boolean isShowRestEveryDay) {
		
		//установка шрифта
		WritableFont arial12ptBold =
			new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
		WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
		
		WritableFont arial12ptBoldBold =
			new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
		WritableCellFormat arial12BoldBoldFormat = new WritableCellFormat(arial12ptBoldBold);
		try {
			arial12BoldBoldFormat.setAlignment(Alignment.LEFT); //выравнивание по центру
			arial12BoldBoldFormat.setWrap(false); //перенос по словам если не помещается
			arial12BoldBoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldBoldFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM); //рисуем рамку
			
			arial12BoldFormat.setAlignment(Alignment.CENTRE); //выравнивание по центру
			arial12BoldFormat.setWrap(true); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM); //рисуем рамку
			//arial12BoldFormat.setOrientation(Orientation.PLUS_90); //поворот текста
			
			int dCol = 1;
			int dRow = 35;
			double sumDeb = 0;
			double sumCred = 0;
			
			int rowRest = 0; // кол-во строк остатков на дату
			String priorOperDate = null;

			int[] columns = new int[]{0, 1, 5, 7, 10, 13, 21, 26, 27, 28, 29, 30, 31, 32, 33};
			for (int rw = 1; rw < data.size(); rw++) {
				Object[] row = data.get(rw);
				
				String oper = (String)row[1];

				// выводим остаток если нужно
				if(isShowRestEveryDay) {
					Label lab = showRestEveryDay(oper, priorOperDate, row[row.length - 1], dCol, rw + dRow + rowRest, arial12BoldBoldFormat);

					if(lab != null) {
						sheet.addCell(lab);
						priorOperDate = oper;
						rowRest++;
					}
				}

				for(int col = 0; col < row.length - 1; col++) {
					String text = (row[col] == null) ? "" : row[col].toString();
					Label label = new Label(columns[col] + dCol, rw + dRow + rowRest, text, arial12BoldFormat);
					sheet.addCell(label);
					if(col > 0 && col < 7) {
						sheet.mergeCells(columns[col] + dCol, rw + dRow + rowRest, columns[col+1], rw + dRow + rowRest);
					}
					
					if(col == 12 || col == 13) {
						double val;
						//logger.debug("row[" + col + "]=" + text);
						try {
							val = (row[col] == null || "".equals(text)) 
							?  0 : Double.parseDouble(text.replace(',', '.'));
						} catch (NumberFormatException e) {
							val = 0.0;
							//logger.debug("Exception is double");
						}
						if(col == 12) {
							sumDeb += val;
						} else {
							sumCred += val;
						}
					}
				}
			}
			
			logger.debug("sumDeb=" + sumDeb);
			logger.debug("sumCred=" + sumCred);
			fillTail(sheet, data.get(0), sumDeb, sumCred, data.size() + 36 + rowRest);
		} catch (WriteException e) {
			logger.error("exportRows WriteException ", e);
		}
	}
	
	private void fillTail(WritableSheet sheet, Object[] headRow, Double sumDeb, Double sumCred, int row) {
		//установка шрифта
		WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
		WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
		try {
			arial12BoldFormat.setAlignment(Alignment.CENTRE); //выравнивание по центру
			arial12BoldFormat.setWrap(true); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM); //рисуем рамку
		
			// Шапка
			String text = "Остаток по счету на начало периода";
			Label label = new Label(1, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(1, row, 5, row);
			
			text = "Сумма по дебету счета за период";
			label = new Label(6, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(6, row, 12, row);
			
			text = "Сумма по кредиту счета за период";
			label = new Label(13, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(13, row, 19, row);
			
			text = "Остаток по счету на конец периода";
			label = new Label(20, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(20, row, 26, row);
			
			row++;
			
			arial12BoldFormat = new WritableCellFormat(arial12ptBold);
			arial12BoldFormat.setAlignment(Alignment.RIGHT); //выравнивание по центру
			arial12BoldFormat.setWrap(true); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM); //рисуем рамку
			// Данные
			text = (headRow[12] == null) ? "" : headRow[12].toString();
			label = new Label(1, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(1, row, 5, row);
			
			NumberFormat numbFormatter = NumberFormat.getNumberInstance();
			text = numbFormatter.format(sumDeb);
			label = new Label(6, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(6, row, 12, row);
			
			text = numbFormatter.format(sumCred);
			label = new Label(13, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(13, row, 19, row);
			
			text = (headRow[13] == null) ? "" : headRow[13].toString();
			label = new Label(20, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			sheet.mergeCells(20, row, 26, row);
			
			row += 3;

			arial12BoldFormat = new WritableCellFormat(arial12ptBold);
			arial12BoldFormat.setAlignment(Alignment.LEFT); //выравнивание по центру
			arial12BoldFormat.setWrap(false); //перенос по словам если не помещается
			arial12BoldFormat.setBackground(Colour.WHITE); //установить цвет
			arial12BoldFormat.setBorder(Border.NONE, BorderLineStyle.MEDIUM); //рисуем рамку
			
			Date dt = new Date();
			DateFormat formatter = DateFormat.getDateInstance();
			
			text = " Дата / " + formatter.format(dt) + " /";
			label = new Label(1, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			
			row += 2;
			text = ""; //Cfg.path().extractSigner();
			label = new Label(1, row, text, arial12BoldFormat); 
			sheet.addCell(label);
			
		} catch (WriteException e) {
			logger.error("fillTail WriteException ", e);
		}
	}
}
