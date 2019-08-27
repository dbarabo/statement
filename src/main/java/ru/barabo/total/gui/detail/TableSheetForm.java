package ru.barabo.total.gui.detail;

import org.apache.log4j.Logger;
import ru.barabo.total.db.*;
import ru.barabo.total.db.impl.AbstractRowFields;
import ru.barabo.total.db.impl.DetailField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;
import java.util.List;

public class TableSheetForm<E extends AbstractRowFields> extends JPanel implements Comparator<DetailFieldItem>, ListenerStore<E> {
	
	final static private int COLUMN_COUNT = 100; // максимальное ч-ло столбцов

	final transient private static Logger logger = Logger
			.getLogger(TableSheetForm.class.getName());

	private int maxRowHeight = 0; // максимальное ч-ло строк, кот. располагается на панели 
	
	private List<DetailFieldItem> fields;
	
	private DBStore<E> store;
	
	private String focusedComponentName;
	

	public String getFocusedComponentName() {
		return focusedComponentName;
	}

	public TableSheetForm(DBStore<E> store, List<DetailFieldItem> fields) {
		
		super(new GridBagLayout());
		
		this.store = store;

		this.fields = fields;

		createForm();

		store.addListenerStore(this);
	}

	private void createForm() {
		if (fields == null) {
			List tmp = store.getFields();
			fields = initFields(tmp);
		}

		Collections.sort(this.fields, this);

		setToForm();
	}


	
	
	private List<DetailFieldItem> initFields(List<DetailFieldItem> subFields) {

		fields = new ArrayList<DetailFieldItem>();

		for (DetailFieldItem field : subFields) {
			if(field.isExistsGrid() && field.getGroupLabel() == null) {
				fields.add(field);
				continue;
			}
			
			if(field.getGroupLabel() != null) {
				fields = checkAddGroup(field, fields);
			}
		}
		
		return fields;
	}
	
	private List<DetailFieldItem> checkAddGroup(DetailFieldItem findField,
			List<DetailFieldItem> fields) {
		
		for(FieldItem field : fields) {
			if(field.getLabel().equals(findField.getGroupLabel() ) ) {
				return fields;
			}
		}
		
		DetailFieldItem group = new GroupFieldItem(findField.getGroupLabel(),
				(List) store.getFields());
		
		fields.add(group);

		return fields;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * Сортирует филды
	 */
	@Override
	public int compare(DetailFieldItem o1, DetailFieldItem o2) {
		
		return o1.getPosY() < o2.getPosY() ||
			  (o1.getPosY() == o2.getPosY() && 
			   o1.getPosX() < o2.getPosX() )
				
				?  -1 : 1;
	}
	
	/**
	 * размещает на форме филды - уже отсортированные
	 */
	private void setToForm() {
		int[] maxRow = new int[COLUMN_COUNT];
		Arrays.fill(maxRow, 0);
		
		for (DetailFieldItem field : fields) {
			int rowPosNext = addComp(field, maxRow);
			checkMaxRow(maxRow, rowPosNext, field);
		}
		
		maxRowHeight = getMaxValue(maxRow);
		setDownEmptyLine(maxRowHeight);
	}
	
	/**
	 * @param field
	 * @param maxRow - содержит номера строк кот. свободны в заданном столбце
	 * @return добавляет компонент на форму
	 */
	private int addComp(DetailFieldItem field, final int[] maxRow) {
		return field.getLabel().equals(field.getGroupLabel()) 
			? addCompGroup(field, maxRow)
			: addCompField(field, maxRow);
	}
	
	private int addCompGroup(DetailFieldItem fieldGroup, final int[] maxRow) {
		
		/*
		 * logger.info("fieldGroup.getSubFields()=" +
		 * fieldGroup.getSubFields().size());
		 */
		JPanel groupComp = new TableSheetForm<E>(store,
				fieldGroup.getSubFields());
		groupComp.setBorder(new TitledBorder(fieldGroup.getLabel()));
		
		fieldGroup.setComponent(groupComp);
		
			final GridBagConstraints gridConstComp = new GridBagConstraints(
				fieldGroup.getPosX() * 2, 
				maxRow[fieldGroup.getPosX()], 
				fieldGroup.getWidth() * 2,
				fieldGroup.getHeight() + 1, // плюс заголовок
				1.0, 1.0, 
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0);
		
		add(groupComp, gridConstComp);
		
		return maxRow[fieldGroup.getPosX()] + fieldGroup.getHeight() + 1;
	}
	
	/**
	 * @param field
	 * @param maxRow
	 * @return добавляет компонент на форму - если компонент НЕ группа
	 */
	private int addCompField(DetailFieldItem field, final int[] maxRow) {
		final GridBagConstraints gridConstLabel = new GridBagConstraints(field.getPosX() * 2, 
				maxRow[field.getPosX()], 
				1, 1, 0.0, 0.0, 
				GridBagConstraints.PAGE_START, //EAST, 
				GridBagConstraints.HORIZONTAL/*NONE*/, 
				new Insets(2, 2, 2, 2), 0, 0);
		
		add(new JLabel(field.getLabel()), gridConstLabel);

		final GridBagConstraints gridConstComp = new GridBagConstraints(field.getPosX() * 2 + 1, 
				maxRow[field.getPosX()], 
				field.getWidth() * 2 - 1,
				field.getHeight(),
				1.0, (field.getHeight() > 1 ? 1.0 : 0.0), 
				GridBagConstraints.PAGE_START, //CENTER,
				(field.getHeight() == 1) ? GridBagConstraints.HORIZONTAL : GridBagConstraints.BOTH/*GridBagConstraints.HORIZONTAL*/,
				new Insets(2, 2, 2, 2), 0, 0);
			
		
		add(createComponent(field), gridConstComp);
		
		for(int rowIndex = 1; rowIndex < field.getHeight(); rowIndex++) {
			add(new JLabel(" "), new GridBagConstraints(field.getPosX() * 2, 
					maxRow[field.getPosX()] + rowIndex, 
					1, 1, 0.0, 0.0, GridBagConstraints.PAGE_START/*EAST*/, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		}
		
		return maxRow[field.getPosX()] + field.getHeight();
	}
	
	
	/**
	 * Устанавливает пустой элемент, чтобы он все пространство забирал
	 */
	private void setDownEmptyLine(int row ) {
		final GridBagConstraints gridConstComp = new GridBagConstraints(
				0, 
				row, 
				1,
				1, // плюс заголовок
				0, 5.0, 
				GridBagConstraints.PAGE_END,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0);
		
		add(new JLabel(""), gridConstComp);
	}
	
	static public int getMaxValue(int[] array) {
		int val = array[0];
		for (int index = 1; index < array.length; index++) {
			if(val < array[index]) {
				val = array[index];
			}
		}
		return val;
	}
	
	private JComponent createComponent(DetailFieldItem field) {
		
		JComponent component = FactoryComponent.create(field);

		if (field.getLabel() != null) {
			component.setName(field.getLabel());
		}

		component.addFocusListener(getFocusListenerComponent());

		if (component instanceof JComboBox &&
				field instanceof DetailField) {

			((DetailField) field).setStore(store);
		}

		return component;
	}

	private FocusListener getFocusListenerComponent() {

		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
				TableSheetForm<E> main = TableSheetForm.this;

				while (main.getParent() instanceof TableSheetForm) {
					main = (TableSheetForm<E>) main.getParent();
				}
				
				main.focusedComponentName = ((JComponent) e.getSource()).getName();
			}

		    
			@Override
		    public void focusLost(FocusEvent e) {
				JComponent comp = (JComponent) e.getSource();

				E row = store.getRow();

				if (e.getSource() instanceof JTextField) {
					JTextField text = (JTextField) e.getSource();
					if (text.isEditable()) {
						row.getFieldByLabel(text.getName()).setValueField(text.getText());
					}
				} else if (e.getSource() instanceof JComboBox) {
					// для комбобокса отключаем установку значений по потере
					// фокуса - по селекту только
					/*
					 * JComboBox combo = (JComboBox) e.getSource();
					 * 
					 * row.getFieldByLabel(combo.getName()).setValueField(
					 * (String) combo.getSelectedItem());
					 */
				} else if (e.getSource() instanceof JCheckBox) {
					JCheckBox box = (JCheckBox) e.getSource();

					row.getFieldByLabel(box.getName()).setValueField(
							box.isSelected() ? "1" : "0");
				}

			}
		};
	}

	/**
	 * @param maxRow
	 * @param rowPosNext
	 * @param field
	 * Проверяет максимумы строковых позиций и обновляет их в случае надобности
	 */
	private void checkMaxRow(int[] maxRow, int rowPosNext, DetailFieldItem field) {
		 for(int index = 0; index < field.getWidth(); index++) {
			 if(maxRow[field.getPosX() + index] < rowPosNext) {
				 maxRow[field.getPosX() + index] = rowPosNext;
			 }
		 }
	}

	
	public int nvl(Integer val) {
		return (val == null) ? 0 : val;
	}


	public int getMaxRowHeight() {
		return maxRowHeight;
	}


	
	@Override
	public void setCursor(E row) {
		updataValues(store.getRow());
	}

	public DetailFieldItem getFieldItemByLabel(String label) {
		for (DetailFieldItem field : fields) {

			if(label.equals(field.getLabel())) {
				return field;
			}

			if(field.getSubFields() != null) {
				for (DetailFieldItem subField : field.getSubFields()) {
					if(label.equals(subField.getLabel())) {
						return subField;
					}
				}
			}
		}
		return null;
	}

	
	/**
	 * обновляет текущие данные у компонент
	 */
	private void updataValues(E row) {
		
		for (DetailFieldItem field : fields) {

			if (field.getIndex() < 0) {
				continue;
			}

			String value = (row == null || row.fieldItems().size() <= field
					.getIndex()) ? null :
					row.fieldItems().get(field.getIndex()).getValueField();
			
			field.setComponentValue(value);
		}

		for (DetailFieldItem field : fields) {
			if (!(field.getComponent() instanceof JComboBox)) {
				continue;
			}

			ActionListener[] listeners = ((JComboBox) field.getComponent())
						.getActionListeners();

			for (ActionListener listener : listeners) {
				listener.actionPerformed(new ActionEvent(field.getComponent(),
							ActionEvent.ACTION_PERFORMED, "comboBoxChanged"));
			}
		}
	}


	@Override
	public void refreshData(List<E> allData, StateRefresh stateRefresh) {

		updataValues(store.getRow());
	}
}
