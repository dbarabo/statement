package ru.barabo.total.db.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.FieldItem;
import ru.barabo.total.db.Type;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Field implements FieldItem {
	
	final static transient private Logger logger = Logger.getLogger(Field.class.getName());
	
	private Type clazz; // 1 - Str, 0-Long, 2-Decimal;
	private Object val; 
	private String label; 
	private boolean isGrid;
	private String[] list;
	private String column; 
	private int width;
	private int index;
	private boolean isReadOnly;
	private Integer[] maps;
	
	private Format formatter;
	
	@Override
	public String getColumn() {
		return column;
	}

	public Field(String label, Type clazz, int width, int index) {

	    this.label = label;
        this.isGrid = true;
        this.clazz = clazz;
        this.width = width;
        this.index = index;
        this.isReadOnly = true;
    }

    public Field(String label, Type clazz, int width, int index, Format formatter) {

        this(label, clazz, width,index );
        this.formatter = formatter;
    }

	public Field(String label, boolean isGrid, Type clazz,
			String[] list, String column, int width, int index, boolean isReadOnly) {
		this.label = label;
		this.isGrid = isGrid;
		this.clazz = clazz;
		this.list = list;
		this.column = column;
		this.width = width;
		this.index = index;
		this.isReadOnly = isReadOnly;
	}

    public Field(String label, boolean isGrid, Type clazz,
                 String[] list, String column, int width, int index, boolean isReadOnly, Format formatter) {
	    this(label, isGrid, clazz, list, column, width, index, isReadOnly);

        this.formatter = formatter;
    }

	public Field(String label, boolean isGrid, Type clazz,
			String[] list, String column, int width, int index, boolean isReadOnly, Integer[] maps) {
		this(label, isGrid, clazz, list, column, width, index, isReadOnly);
		this.maps = maps;
	}
	
	public Field(String label, boolean isGrid, Type clazz,
			String[] list, String column, int width, int index, boolean isReadOnly,
				 Integer[] maps, Format formatter) {
		this(label, isGrid, clazz, list, column, width, index, isReadOnly, maps);
		this.formatter = formatter;
	}
	

	@Override
	public String getLabel() {
		
		return label;
	}
	
	@Override
	public Object getVal() {
		return val;
	}

	@Override
	public boolean isExistsGrid() {
		return isGrid;
	}
	
	private int mapStrToInt(String value) {
		for (int index = 0; index < list.length; index++) {
			if(value.trim().equalsIgnoreCase(list[index].trim() )) {
				return maps[index];
			}
		}
		
		return -1;
	}
	
	private String mapIntToStr(int val) {
		for (int index = 0; index < maps.length; index++) {
			if(maps[index] != null && maps[index] == val) {
				return list[index];
			}
		}
		
		return null;
	}

	@Override
	public void setValueField(String value) {
		if (value == null || "".equals(value.trim())) {
			val = null;
			return;
		}

		Object parseVal = null;

		if (formatter != null) {
			try {
				logger.info(" value=" + value);
				parseVal = formatter.parseObject(value);
				logger.info("parseVal=" + parseVal);
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error(e);
				logger.error(e.getMessage());
			}
		} else {
			parseVal = value;
		}
		
		switch (clazz) {
		case LONG:
			if(list != null && maps != null) {
			  val = mapStrToInt(value);
			} else {
			  val = Long.parseLong(value.trim());
			}
			break;
			
		case STRING:
			val = (formatter == null) ? value : parseVal;
			break;
			
		case DECIMAL:
			val = Double.parseDouble(value.trim());
			break;
			
			
		case DATE:
			val = (formatter == null) ? parseDefaultDate(value) /*java.util.Date.parse(value)*/ : parseVal;
			break;

		default:
			break;
		}
	}

	private Object parseDefaultDate(String value) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parseObject(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }
	
	@Override
	public void setValueFieldObject(Object value) {

		if(value == null) {
			val = null;
			return;
		} 
		
		switch (clazz) {
		case LONG:
			val = ((Number)value).longValue();
			break;
			
		case STRING:
			val = value;
			break;
			
		case DECIMAL:
			val =  ((Number)value).doubleValue();
			break;
			
		case DATE:
			val = value;
			break;	
			
		default:
			break;
		}
	}

	@Override
	public String[] getListField() {
		
		return list;
	}

	@Override
	public void setListField(String[] valueList) {
		list = valueList;
	}

	@Override
	public String getValueField() {
		if (val == null) {
			return null;
		}

		if(formatter != null) {
			return formatter.format(getVal());
		}
		
		if (list != null && maps != null && clazz == Type.LONG) {

			return mapIntToStr(((Number)val).intValue());
		}
		
		return val.toString();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	public Type getClazz() {

		return clazz;
	}
}

