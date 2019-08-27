package ru.barabo.total.db.impl;

import org.apache.log4j.Logger;
import ru.barabo.total.db.*;

import java.util.*;

public abstract class AbstractFilterStore<E extends AbstractRowFields> extends AbstractDBStore<E>
implements FilteredStore<E> {
	
	final static transient private Logger logger = Logger.getLogger(AbstractFilterStore.class.getName());
	
	private String[] filters;
	
	private List<E> filterData;
	
	private List<E> oldFilterData;
	
	public AbstractFilterStore() {
		super();
		
		filterData = new ArrayList<>();
		
		oldFilterData = new ArrayList<>();
	}
	
	private E getField() {
		List<E> data = super.getData();
		
		if(data != null && data.size() != 0) {
			return data.get(0);
		}
		
		return createEmptyRow();
	}
	
	private int getFieldIndex(int columnIndex) {
		E field = getField();

		if(field == null) return -1;

		if (columnIndex == -1) {
			return 0;
		}

		
		int indexVis = -1;
		
		for (int index = 0; index < field.fieldItems().size(); index++) {
			
			FieldItem fld = field.fieldItems().get(index);
			
			if(fld.isExistsGrid() ) {
				indexVis++;
			}
			
			if(indexVis == columnIndex) {
				return index;
			}
		}
		
		return indexVis;
	}
	
	private static String getNumberString(String valueFilter) {
		if (valueFilter == null) return null;
		
		StringBuilder res = new StringBuilder();

		for (int index = 0; index < valueFilter.length(); index++) {
			if(valueFilter.charAt(index) >= '0' && valueFilter.charAt(index) <= '9') {
				res.append(valueFilter.charAt(index));
			}
		}

		return res.toString();
	}
	

	private static Long getNumberFromDigits(String digits) {
		if("".equals(digits)) {
			return null;
		}
		
		while("0".equals(digits.substring(0, 1)) ) {
			digits = digits.substring(1);
		}
		
		return Long.parseLong(digits);
	}
	
	private static Long getNumber(String valueFilter) {
		if (valueFilter == null) return null;
		
		String res = getNumberString(valueFilter);
		
		return getNumberFromDigits(res);
	}
	
	private boolean isCriteriaLong(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		if (valueFilter != null && valueFilter.contains(SEPARATOR_VALUE)) {

			return valueFilter.contains(SEPARATOR_VALUE + valueField.toString() + SEPARATOR_VALUE);
		}

		Long number = getNumber(valueFilter);
		
		if(number == null) return true;
		
		return ((Number)valueField).longValue() == number;
	}
	
	private List<String> getPartsString(String value) {
		
		if(value == null) return null;
		
		List<String> parts = new ArrayList<>();
		
		int startIndex = 0;
		int Endindex = 0;
		String part;
				
		while(Endindex >= 0) {
			Endindex = value.indexOf(' ', startIndex);
			
			if(Endindex < 0) {
				part = value.substring(startIndex);

			} else if(Endindex > startIndex) {
				part = value.substring(startIndex, Endindex);
				
			} else {
				part = "";
			}
			
			startIndex = Endindex + 1;

			if(parts.size() == 0 || (!"".equals(part)) ) {
				parts.add(part.toUpperCase());
			}
			
		}
		
		return parts;
	}
	
	private boolean isCriteriaString(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		List<String> parts = getPartsString(valueFilter);
		
		if(parts == null || parts.size() == 0) return true;
		
		String src = ((String)valueField).toUpperCase();
		
		if(src.indexOf(parts.get(0)) != 0) return false;
		
		int priorFindIndex = 0;
		
		int indexFind;
		
		for(int index = 1; index < parts.size(); index++) {

			indexFind = src.indexOf(parts.get(index), priorFindIndex);
			
			if(indexFind <= priorFindIndex) {
				return false;
			}
			
			priorFindIndex = indexFind; 
		}

		return true;
	}
	
	private Date getDate(int day, int month, int year) {
		
		Calendar calendarTmp = new GregorianCalendar();
		
		if(month == -1) {
			calendarTmp.setTime(new Date());
			month = calendarTmp.get(Calendar.MONTH) + 1;
		}

		
		if(year == -1) {
			calendarTmp.setTime(new Date());
			year = calendarTmp.get(Calendar.YEAR);
		}
		
		calendarTmp.set(year, Calendar.JANUARY, 1, 0, 0, 0);
		
		calendarTmp.add(Calendar.MONTH, month - 1);
		calendarTmp.add(Calendar.DAY_OF_YEAR, day - 1);
		
		calendarTmp.add(Calendar.HOUR, -10);

		
		return calendarTmp.getTime();
	}
	
	private DateDiapason getDiapason(int dayStart, int monthStart, int yearStart,
			int dayEnd, int monthEnd, int yearEnd) {
		
		Date min = getDate(dayStart, monthStart, yearStart);
		
		Date max = getDate(dayEnd, monthEnd, yearEnd);
		
		return new DateDiapason(min, max);
	}
	
	private DateDiapason getDiapason(int dayStart, int monthStart,  int dayEnd, int monthEnd) {

		Date min = getDate(dayStart, monthStart, -1);
		
		Date max = getDate(dayEnd, monthEnd, -1);
		
		return new DateDiapason(min, max);
	}
	
	private DateDiapason getMonthDay2(String digit2) {
		
		Long val = getNumberFromDigits(digit2);
		
		if(val == null) return null;
		
		if(val.intValue() <= 12) {
			return getDiapason(1, val.intValue(), 1, val.intValue() + 1);
		} else {
			return getDiapason(val.intValue(), -1, val.intValue() + 1, -1);
		}
	}
	
	private DateDiapason getMonthDay4(String digit4) {

		Long day = getNumberFromDigits(digit4.substring(0, 2));
		
		Long month = getNumberFromDigits(digit4.substring(2, 4));

		assert day != null;
		assert month != null;
		return getDiapason(day.intValue(), month.intValue(), day.intValue() + 1, month.intValue());
	}

	/**
	 * MMYYYY
	 */
	private DateDiapason getMonthDay6(String digit6) {

		Long month = getNumberFromDigits(digit6.substring(0, 2));
		
		Long year = getNumberFromDigits(digit6.substring(2, 6));

		assert month != null;
		assert year != null;
		return getDiapason(1, month.intValue(), year.intValue(),
				1, month.intValue() + 1, year.intValue());
	}
	
	private DateDiapason getMonthDay8(String digit8) {

		Long day = getNumberFromDigits(digit8.substring(0, 2));
		
		Long month = getNumberFromDigits(digit8.substring(2, 4));
		
		Long year = getNumberFromDigits(digit8.substring(4, 8));

		assert day != null;
		assert month != null;
		assert year != null;
		return getDiapason(day.intValue(), month.intValue(), year.intValue(),
				day.intValue() + 1, month.intValue(), year.intValue() );
	}
	
	private DateDiapason getDiapason(String valueFilter) {
		
		String dt = getNumberString(valueFilter);
		
		if(dt == null) return null;
		
		if(dt.length() == 1  || dt.length() == 2 || dt.length() == 3) {
			return getMonthDay2(dt);
		} else if(dt.length() == 4 || dt.length() == 5) {
			return getMonthDay4(dt);
		} else if(dt.length() == 6 || dt.length() == 7) {
			return getMonthDay6(dt);
		} else {
			return getMonthDay8(dt);
		}
	}
	
	private boolean isCriteriaDate(Object valueField, String valueFilter) {
		if(valueField == null) return false;
		
		DateDiapason diapason = getDiapason(valueFilter);
		
		if(diapason == null) return true;
		
		long value = ((Date)valueField).getTime();
		
		return diapason.minDate.getTime() <= value && diapason.maxDate.getTime() > value;
	}
	
	private boolean isCriteriaField(Object valueField, Type clazz, String valueFilter) {
		
		switch(clazz) {
		case LONG:
			return isCriteriaLong(valueField, valueFilter);
			
		case STRING:
			return isCriteriaString(valueField, valueFilter);

		case DATE:
			return isCriteriaDate(valueField, valueFilter);

		case DECIMAL:
		default:
			return false;
		}
	}

	private boolean isCriteriaFilter(E row) {
		
		if(filters == null || filters.length == 0) return true;
		
		List<FieldItem> items = row.fieldItems();

		for(int index = 0; index < filters.length; index++) {
			
			if(filters[index] == null || ("".equals(filters[index].trim()))) continue;
			
			if(!isCriteriaField(items.get(index).getVal(), 
					            items.get(index).getClazz(), 
					            filters[index])) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isEqualFilter(List<E> filterData, List<E> oldFilterData) {
		
		if(filterData.size() != oldFilterData.size()) return false;
		
		for (E row : filterData) {
			if(oldFilterData.indexOf(row) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private void checkAfterFilter(StateRefresh stateRefresh) {
		if(isEqualFilter(filterData, oldFilterData)) return;
		
		
		oldFilterData.clear();
		oldFilterData.addAll(filterData);
		
		sendListenersRefreshAllData(stateRefresh);
	}

	@Override
	public void sendListenersRefreshAllData(StateRefresh stateRefresh) {
		if (!isFiltered()) {
			super.sendListenersRefreshAllData(stateRefresh);
			return;
		}

		for (ListenerStore<E> listenerStore : listenersStore) {
			listenerStore.refreshData(filterData, stateRefresh);
		}
	}

	private final static String SEPARATOR_VALUE = ";";

	@Override
	public String getAllFieldValue(int fieldIndex) {
		if(filterData == null) {
			return "";
		}
		
		StringBuilder result = new StringBuilder(SEPARATOR_VALUE);
		
		for(E row : filterData) {
			result.append(row.fieldItems().get(fieldIndex).getValueField()).append(SEPARATOR_VALUE);
		}

		if (SEPARATOR_VALUE.equals(result.toString())) {
			return "";
		}
		
		return result.toString();
	}

	private void setFiltered(StateRefresh stateRefresh) {
		filterData.clear();
		
		for (E row : super.getData()) {
			if(isCriteriaFilter(row) ) {
				filterData.add(row);
			}
		}
		
		checkAfterFilter(stateRefresh);
	}
	
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isFiltered() {
		
		if(filters == null) return false;
		
		for(String val : filters) {
			if(val == null || "".equals(val.trim())) continue;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<E> getData() {
		
		boolean isUpdateNeed = isMustUpdate();
		
		List<E> data = super.getData();

		if (!isFiltered()) {
			return data;
		}
		
		if(isUpdateNeed) {
			setFiltered(StateRefresh.ALL);
		}

		return filterData;
	}

    @Override
    public Integer getCountUnfilteredData() {

        List<E> data = super.getData();

        return data == null ? 0 : data.size();
    }

	
	@Override
	public E getRow() { 
		E row = super.getRow();

		if((!isFiltered()) || (row == null) ) return row;

		if (filterData.indexOf(row) >= 0) {
			return row;
		}

		if (filterData.size() > 0 && filterData.indexOf(row) < 0) {
			return filterData.get(0);
		}

		return null;
	}

	@Override
	public void setFilterValue(int columnIndex, String value) {
		
		int fieldIndex = getFieldIndex(columnIndex);
		
		if(fieldIndex == -1) return;
		
		if(filters == null) {
			E field = getField();
			filters = new String[field.fieldItems().size()];
		}
		
		filters[fieldIndex] = value;
		
		setFiltered(StateRefresh.FILTER);
	}

	@Override
	public String getFilterValue(int columnIndex) {
		
		if(filters == null || filters.length == 0) return null;
		
		int fieldIndex = getFieldIndex(columnIndex);

		if(fieldIndex == -1) return null;

		return filters[fieldIndex];
	}
	
	
	class DateDiapason {
		
		Date minDate;
		
		Date maxDate;
		
		DateDiapason(Date minDate, Date maxDate) {
			
			this.minDate = minDate;
			
			this.maxDate = maxDate;
		}
	}

}
