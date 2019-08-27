package ru.barabo.statement.jexel;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import ru.barabo.total.xls.loader.jexel.ExcelFileType;


public class ImportExelData {
	
	transient static private final Logger logger = Logger.getLogger(ImportExelData.class.getName());
	
	transient static private Vector<Integer> cellValue = null;
	transient static private Vector<Integer> cellValueAdd = null;
	
	public Vector<Object[]> doImportExcel(File file, ExcelFileType excelType){
		Workbook workbook = null;
		Sheet sheet = null;
		
		if(excelType == null) return null;
		
		try {
			workbook = Workbook.getWorkbook(file);
			if(workbook == null) return null;
			
			sheet = workbook.getSheet(0);
			if(sheet == null) return null;
			
		} catch (BiffException e) {
			e.printStackTrace();
			logger.error(e);
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
			logger.error(e.getMessage());
		}
		
		Point startPoint = getStartPoint(sheet, excelType);
		if(startPoint.x == -1) return null;
		
		Vector<Object[]> data = fillData(sheet, excelType, startPoint);
		
		if(workbook != null) {
			workbook.close();
		}
		
		return data;
	}
	
	/**
	 * @param sheet
	 * @return начало поиска переменных 
	 */
	private Point getStartPoint(Sheet sheet, ExcelFileType excelType) {
		Point result = new Point(-1, -1);
		
		for (int y = 0; y < sheet.getRows(); y++) {
			for (int x = 0; x < sheet.getColumns(); x++) {
				final Cell cell = sheet.getCell(x, y);
				final String value = cell.getContents();
				if(value == null || "".equals(value.trim())) continue;
				
				for (int index = 0; index < excelType.getCountHeaderName(); index++) {
					if(excelType.getHeaderName(index).equalsIgnoreCase(value.trim()) ) {
						result.x = x;
						result.y = y;
						return result;
					}
				}
			}
		}
		
		return result;
	}
	
	private Point getSecondPoint(Sheet sheet, String findString, int yy) {
		Point result = null;
		
		for (int y = yy; y < sheet.getRows(); y++) {
			for (int x = 0; x < sheet.getColumns(); x++) {
				final Cell cell = sheet.getCell(x, y);
				final String value = cell.getContents();
				if(value == null || "".equals(value.trim())) continue;
				
				if(findString.equalsIgnoreCase(value.trim()) ) {
						/*result.x = x;
						result.y = y;*/
						return new Point(x, y);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @param sheet
	 * @param excelType
	 * @param startPoint
	 * Бежит по ячейкам и считывает данные
	 */
	private Vector<Object[]> fillData(Sheet sheet, 
			ExcelFileType excelType, Point startPoint) {
		int countBlank = 0;
		int prefixIndex = 1;
		ProcessResult result = new ProcessResult(ProcessType.Error);
		String prefix = "";
		
		Vector<Object[]> data = new Vector<Object[]>();
		cellValue = null;
		cellValueAdd = null;
		
		for (int y = startPoint.y + 1; y < sheet.getRows(); y++) {
			
			switch (excelType) {
			case CBForm134:
				result = processF134(sheet, new Point(startPoint.x, y), 
							countBlank, prefixIndex);
				break;
				
			case CBForm806:
				result = processF806(sheet, new Point(startPoint.x, y), 
							countBlank, prefixIndex);
				break;
				
			case CBForm135:
				result = processF135(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				if(result.getProcessType() == ProcessType.Break) {
					startPoint = getSecondPoint(sheet, "Краткое наименование норматива (требования)", y);
					logger.debug("startPoint:" + startPoint);
					if(startPoint != null) {
						y = startPoint.y;
						this.cellValue.set(0, new Integer(2));
						result = new ProcessResult(ProcessType.Continue, 0, 1);
					}
				}
				break;
				
			case CBForm115:
			case CBForm155:
				result = processF115(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm125:
				result = processF125(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm501:
				result = processF501(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm101:
				result = processF101(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm157:
				result = processF157(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm102:
				result = processF102(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm807:
				result = processF807(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			case CBForm110:
				result = processF110(sheet, new Point(startPoint.x, y), 
						countBlank, prefixIndex);
				break;
				
			default:
				break;
			}
			
			if(result.getProcessType() == ProcessType.Error) {
				return null;
			}
			
			countBlank = result.getCountBlank();
			prefixIndex = result.getPrefixIndex();
			
			if(result.getProcessType() == ProcessType.Break) {
				break;
			}else if(result.getProcessType() == ProcessType.Continue) {
				continue;
			}
			
			String nameVar = result.getNameVariable();
			String descVar = result.getDescVariable();
			Double[] values = result.getValueVariable();
			
			for (int index = 0; index < values.length; index++) {
				Double cellValue = values[index];
				
				String name = (values.length == 1) ? nameVar : (nameVar + "_" + (index+1));
				
				logger.debug("Name:" + name);
				logger.debug("Desc:" + descVar);
				logger.debug("Value:" + cellValue);
				
				Object[] row = new Object[3];
				
				row[0] = name;
				row[1] = descVar;
				row[2] = cellValue;
				
				data.add(row);
			}
		}
		
		return data;
	}
	
	private ProcessResult processF157(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0 || nameVar.length() > 7) {
			countBlank++;
			return new ProcessResult((countBlank > 2) 
					? ProcessType.Break : ProcessType.Continue, countBlank, prefixIndex);
		}
		countBlank = 0;
		String descVar = null;
		if(cellValue == null) {
			initPartCells(sheet, new Point(point.x + 3, point.y));
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		descVar = getDescVariable(sheet, point);

		Double[] cellValue = getValueVariableF157(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF101(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		countBlank = 0;
		String descVar = null;
		if(cellValue == null) {
			initPartCells(sheet, new Point(point.x + 1, point.y));
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		
		final int parseVarIndex = checkVar(nameVar);
		if(parseVarIndex == 0) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		Double[] cellValue = getValueVariableF115FirstPart(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF110(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		String nameVar = null;
		Cell cell = null;
		if(cellValue == null) {
			cell = sheet.getCell(point.x, point.y);
			nameVar = cell.getContents();
			if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
			
			initPartCells(sheet, new Point(point.x, point.y) );
			if(cellValue != null && cellValue.size() > 1) {
				cellValueAdd = new Vector<Integer>();
				cellValueAdd.add(cellValue.get(0));
				cellValueAdd.add(cellValue.get(1));
				cellValue.remove(1);
				cellValue.remove(0);
			}
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		cell = sheet.getCell(cellValueAdd.get(0), point.y);
		nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
			countBlank++;
			if(countBlank == 2) {
				return new ProcessResult(ProcessType.Break, countBlank, prefixIndex);
			} else {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
		}
		countBlank = 0;
		cell = sheet.getCell(cellValueAdd.get(1), point.y);
		String descVar = cell.getContents();
				
		Double[] cellValue = getValueVariableF157(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF807(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		String nameVar = null;
		Cell cell = null;
		if(cellValue == null) {
			cell = sheet.getCell(point.x, point.y);
			nameVar = cell.getContents();
			if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
			
			initPartCells(sheet, new Point(point.x, point.y) );
			if(cellValue != null && cellValue.size() > 1) {
				cellValueAdd = new Vector<Integer>();
				cellValueAdd.add(cellValue.get(0));
				cellValueAdd.add(cellValue.get(1));
				cellValue.remove(1);
				cellValue.remove(0);
				cellValue.remove(cellValue.size() - 1);
			}
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		cell = sheet.getCell(cellValueAdd.get(0), point.y);
		nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
			countBlank++;
			if(countBlank == 2) {
				return new ProcessResult(ProcessType.Break, countBlank, prefixIndex);
			} else {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
		}
		countBlank = 0;
		cell = sheet.getCell(cellValueAdd.get(1), point.y);
		String descVar = cell.getContents();
				
		Double[] cellValue = getValueVariableF157(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF102(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		String nameVar = null;
		Cell cell = null;
		if(cellValue == null) {
			cell = sheet.getCell(point.x, point.y);
			nameVar = cell.getContents();
			if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
			
			initPartCells(sheet, new Point(point.x + 1, point.y) );
			if(cellValue != null && cellValue.size() > 1) {
				cellValueAdd = new Vector<Integer>();
				cellValueAdd.add(cellValue.get(0));
				cellValueAdd.add(cellValue.get(1));
				cellValue.remove(1);
				cellValue.remove(0);
			}
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		cell = sheet.getCell(cellValueAdd.get(1), point.y);
		nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		cell = sheet.getCell(cellValueAdd.get(0), point.y);
		String descVar = cell.getContents();
				
		Double[] cellValue = getValueVariableF157(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF501(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		countBlank = 0;
		if(cellValue == null) {
			initPartCells(sheet, new Point(point.x + 1, point.y));
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		if(nameVar.length() > 3) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex + 1);
		}
		nameVar = (prefixIndex - 1) + "*" + nameVar;
		
		String descVar = getDescVariable(sheet, point);
		Double[] cellValue = getValueVariableFind(sheet, new Point(point.x + 4, point.y));
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF125(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		countBlank = 0;
		String descVar = null;
		if(cellValue == null) {
			initPartCells(sheet, new Point(point.x + 1, point.y));
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		
		final int parseVarIndex = checkVar(nameVar);
		if(parseVarIndex == 0) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		descVar = nameVar.substring(parseVarIndex).trim();
		nameVar = nameVar.substring(0, parseVarIndex - 1).trim();
		Double[] cellValue = getValueVariableF115FirstPart(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private ProcessResult processF135(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		String nameVar = null;
		Cell cell = sheet.getCell(point.x, point.y);
		nameVar = cell.getContents();
		if((nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) && (prefixIndex != 2) ) {
			countBlank++;
			if(countBlank == 2 && cellValue != null) {
				countBlank = 0;
				prefixIndex = 2;
			}
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		if(cellValue == null) {
			initPartCells(sheet, new Point(point.x+1, point.y) );
			countBlank = 0;
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		if(prefixIndex == 1) {
			Double[] cellValue = getValueVariableF157(sheet, point);
			return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
					nameVar, null, cellValue);
		/*}else if(prefixIndex == 3) {
			cell = sheet.getCell(point.x + 2, point.y);
		*/	
		}else{
			cell = sheet.getCell(point.x + 1, point.y);
			nameVar = cell.getContents();
			if(nameVar == null || nameVar.length() < 1 || nameVar.length() > 8) {
				countBlank++;
				if(countBlank > 8) {
					return new ProcessResult(ProcessType.Break, countBlank, prefixIndex);
				} else {
					return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
				}
			}
			nameVar = nameVar.substring(0, nameVar.length() - 1);
			Double[] cellValue = getValueVariable(sheet, new Point(point.x, point.y));
			return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
					nameVar, null, cellValue);
		}
	}
	
	private ProcessResult processF115(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0) {
			ProcessResult result;
			result = new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);

			return result;
		}
		countBlank = 0;
		String descVar = null;
		if(nameVar.length() > 8) {
			
			if(checkPart(nameVar)) {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex+1);
			}
			
			int parseVarIndex = 0;
			
			if(prefixIndex > 3) {
				parseVarIndex = checkVar(nameVar);
			}
			
			if(parseVarIndex != 0) {
				descVar = nameVar.substring(parseVarIndex).trim();
				nameVar = nameVar.substring(0, parseVarIndex - 1).trim();
			} else {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
		}
		
		if(prefixIndex > 1) {
			nameVar = prefixIndex + "*" + nameVar; 
		} 
		if(cellValue == null && prefixIndex < 4) {
			initPartCells(sheet, new Point(point.x + 4, point.y) );
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		
		if(descVar == null) {
			descVar = getDescVariable(sheet, point);
		}
				
		Double[] cellValue = getValueVariableF115(sheet, point, prefixIndex);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private boolean checkPart(String namePart) {
		boolean result = (namePart.indexOf("Раздел ") > -1) || namePart.equals("Справочно:");
		
		if(result) {
			cellValue = null;
		}
		return result;
	}
	
	private int checkVar(String nameVar) {
		int pos = 0;
		for (int index = 0; index < nameVar.length(); index++) {
			char val = nameVar.charAt(index);
			
			if(val == '.' || val == '1' || val == '2' || val == '3' || val == '4' ||
					val == '5' || val == '6' || val == '7' || val == '8' || val == '9' || val == '0'){
				pos++;
			} else {
				break;
			}
		}
		return pos;
	}
	
	private ProcessResult processF806(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0 || nameVar.length() > 7) {
			countBlank++;
			if(countBlank > 2) {
				return new ProcessResult(ProcessType.Break, countBlank, prefixIndex);
			} else {
				return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
		}
		countBlank = 0;
		String descVar = getDescVariable(sheet, point);
		if(descVar == null) {
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		
		Double[] cellValue = getValueVariable(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				trimNoneDigits(nameVar), descVar, cellValue);
	}
		
	private ProcessResult processF134(Sheet sheet, Point point, int countBlank, int prefixIndex) {
		Cell cell = sheet.getCell(point.x, point.y);
		String nameVar = cell.getContents();
		if(nameVar == null || nameVar.length() == 0) {
			ProcessResult result;
			countBlank++;
			if(countBlank == 3) {
				result = new ProcessResult(ProcessType.Break, countBlank, prefixIndex);
			} else {
				result = new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
			}
			return result;
		}
		countBlank = 0;
		if(nameVar.length() > 7) {
			prefixIndex++;
			return new ProcessResult(ProcessType.Continue, countBlank, prefixIndex);
		}
		
		if(prefixIndex > 1) {
			nameVar = prefixIndex + "." + nameVar; 
		}
		String descVar = getDescVariable(sheet, point);
		Double[] cellValue = getValueVariable(sheet, point);
		
		return new ProcessResult(ProcessType.Default, countBlank, prefixIndex,
				nameVar, descVar, cellValue);
	}
	
	private String getDescVariable(Sheet sheet, Point point) {
		String descVar = null;
		for (int x = point.x + 1; x < sheet.getColumns(); x++) {
			Cell cell = sheet.getCell(x, point.y);
			if(cell.getType() == CellType.LABEL) {
				descVar  = ((LabelCell)cell).getString();
				if(descVar != null && descVar.length() > 0) {
					break;
				}
			}
		}
		
		return descVar;
	}
	
	/**
	 * Инициализируем в 1-м разделе номера ячеек со значениями
	 */
	private void initPartCells(Sheet sheet, Point point) {
		cellValue = new Vector<Integer>();
		boolean isFirst = true;
		for (int x = point.x; x < sheet.getColumns(); x++) {
			Cell cell = sheet.getCell(x, point.y);
			if((cell.getType() == CellType.LABEL) || 
				(cell.getType() == CellType.NUMBER)) {
				if(isFirst) {
					isFirst = false;
				} else {
				  cellValue.add(new Integer(x));
				}
				logger.debug("x:" + x);
			}
		}
	}
	
	private Double[] getValueVariableF115(Sheet sheet, Point point, int partIndex) {
	
		if(partIndex < 4) {
			return getValueVariableF115FirstPart(sheet, point);
		} else {
			return getValueVariableFind(sheet, point);
		}
	}
	
	private Double[] getValueVariableFind(Sheet sheet, Point point) {
		Double cellValue = null;
		Vector<Double> cellsValue = new Vector<Double>();
		for (int x = point.x + 2; x < sheet.getColumns(); x++) {
			Cell cell = sheet.getCell(x, point.y);
			if(cell.getType() == CellType.NUMBER) {
				cellValue  = ((NumberCell )cell).getValue();
				cellsValue.add(cellValue);
			} else if (cell.getType() == CellType.NUMBER_FORMULA) {
				cellValue  = ((NumberFormulaCell)cell).getValue();
				cellsValue.add(cellValue);
			}
		}
		return cellsValue.toArray(new Double[]{null});
	}
	
	private Double[] getValueVariableF157(Sheet sheet, Point point) {
		
		Double cellValue = null;
		Vector<Double> cellsValue = new Vector<Double>();
		String cellStr;
		
		for (Integer x : this.cellValue) {
			Cell cell = sheet.getCell(x, point.y);
			if(cell.getType() == CellType.NUMBER) {
				cellValue  = ((NumberCell )cell).getValue();
				cellsValue.add(cellValue);
			} else if (cell.getType() == CellType.NUMBER_FORMULA) {
				cellValue  = ((NumberFormulaCell)cell).getValue();
				cellsValue.add(cellValue);
			} else if (cell.getType() == CellType.LABEL) {
				cellStr  = ((LabelCell)cell).getString();
				if(cellStr == null || cellStr.trim().length() == 0) {
					cellValue = null;
				} else {
					try {
						cellValue = Double.parseDouble(cellStr);
					} catch (NumberFormatException e) {
						cellValue = null;
					}
				}
				cellsValue.add(cellValue);
			} 
			else {
				try {
					cellStr = 	cell.getContents();
				cellValue = Double.parseDouble(cellStr);
				} catch (NumberFormatException e) {
					cellValue = null;
				}
				//cellValue = null;
				cellsValue.add(cellValue);
			}
		}
		return cellsValue.toArray(new Double[]{null});
	}
	
	private Double[] getValueVariableF115FirstPart(Sheet sheet, Point point) {
		
		Double cellValue = null;
		Vector<Double> cellsValue = new Vector<Double>();
		
		for (Integer x : this.cellValue) {
			Cell cell = sheet.getCell(x, point.y);
			if(cell.getType() == CellType.NUMBER) {
				cellValue  = ((NumberCell )cell).getValue();
				cellsValue.add(cellValue);
			} else if (cell.getType() == CellType.NUMBER_FORMULA) {
				cellValue  = ((NumberFormulaCell)cell).getValue();
				cellsValue.add(cellValue);
			} else {
				cellValue = null;
				cellsValue.add(cellValue);
			}
		}
		return cellsValue.toArray(new Double[]{null});
	}
	
	private Double[] getValueVariable(Sheet sheet, Point point) {
		Double cellValue = null;
		for (int x = point.x + 2; x < sheet.getColumns(); x++) {
			Cell cell = sheet.getCell(x, point.y);
			if(cell.getType() == CellType.NUMBER) {
				cellValue  = ((NumberCell )cell).getValue(); 
				break;
			} else if (cell.getType() == CellType.NUMBER_FORMULA) {
				cellValue  = ((NumberFormulaCell)cell).getValue(); 
				break;
			}
		}
		return new Double[]{cellValue};
	}
	
	static public String trimNoneDigits(String value) {
		if(value == null) return null;
		
		int index = 0;
		while((index < value.length()) && ("1234567890".indexOf(value.charAt(index)) < 0)) index++;
		
		if(index == value.length() ) return null;
		
		int end = value.length() - 1;
		while((end >= 0) && ("1234567890".indexOf(value.charAt(end)) < 0)) end--;
		
		if(end < 0 ) return null;
		
		return value.substring(index, end + 1);
	}
}

class ProcessResult {
	private ProcessType processType;
	private int countBlank;
	private int prefixIndex;
	private String NameVariable;
	private String DescVariable;
	private Double[] ValueVariable;
	
	ProcessResult(ProcessType processType) {
		this.processType = processType;
	}
	
	private ProcessResult(ProcessType processType, int countBlank) {
		this(processType);
		this.countBlank = countBlank;
	}
	
	ProcessResult(ProcessType processType, int countBlank, int prefixIndex) {
		this(processType, countBlank);
		this.prefixIndex = prefixIndex;
	}
	
	ProcessResult(ProcessType processType, int countBlank, int prefixIndex, 
			String NameVariable, String DescVariable, Double[] ValueVariable) {
		this(processType, countBlank, prefixIndex);
		this.NameVariable = NameVariable;
		this.DescVariable = DescVariable;
		this.ValueVariable = ValueVariable;
	}

	protected ProcessType getProcessType() {
		return processType;
	}

	protected int getCountBlank() {
		return countBlank;
	}

	protected int getPrefixIndex() {
		return prefixIndex;
	}

	protected String getNameVariable() {
		return NameVariable;
	}

	protected String getDescVariable() {
		return DescVariable;
	}

	protected Double[] getValueVariable() {
		return ValueVariable;
	}
}

enum ProcessType {
	Default,
	Continue,
	Break,
	Error
}
