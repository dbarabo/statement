package ru.barabo.total.xls.loader.jexel;

public enum ExcelFileType {

	CBForm134("134", new String[]{"Номер строки", "Номер п/п"}),
	CBForm135("135", new String[]{"Код обозначения"}),
	CBForm806("806", new String[]{"Номер строки", "Номер п/п"}),
	CBForm115("115", new String[]{"Номер строки", "Номер п/п"}),
	CBForm155("155", new String[]{"Номер строки", "Номер п/п"}),
	CBForm125("125", new String[]{"Наименование показателя"}),
	CBForm501("501", new String[]{"Номер строки", "Номер п/п"}),
	CBForm101("101", new String[]{"Номер счета второго порядка"}),
	CBForm157("157", new String[]{"Номер строки", "Номер п/п"}),
	CBForm102("102", new String[]{"N п/п", "N строки", "Номер стро- ки", "Номер строки"}),
	CBForm807("807", new String[]{"Номер п/п", "Номер строки"}),
	CBForm110("110", new String[]{"Номер п/п", "Номер строки"});
	
	
	private String nameForm;
	private String[] startHeader;
	
	private ExcelFileType(String nameForm, String[] startHeader) {
		this.nameForm = nameForm;
		this.startHeader = startHeader;
	}
	
	public String getHeaderName(int index) {
		return startHeader[index];
	}
	
	public int getCountHeaderName() {
		return startHeader.length;
	}

	public String getNameForm() {
		return nameForm;
	}
}
