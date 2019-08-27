package ru.barabo.total.db.impl;

import ru.barabo.total.db.DBStore;
import ru.barabo.total.db.ListenerStore;
import ru.barabo.total.db.StateRefresh;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDBStore<E extends AbstractRowFields> implements DBStore<E> {
	
	//final static transient private Logger logger = Logger.getLogger(AbstractDBStore.class.getName());

	volatile private List<E> data; 
	private boolean isMustUpdate;

	boolean isMustUpdate() {
		return isMustUpdate;
	}

	public void setMustUpdate(boolean isMustUpdate) {
		this.isMustUpdate = isMustUpdate;
	}

	volatile private int cursor;
	private E oldCursorData; // старые данные по курсору

	final List<ListenerStore<E>> listenersStore = new ArrayList<>();

	abstract protected List<E> initData();
	
	/**
	 * 
	 * @return создает копию строки E row
	 */
	abstract protected E cloneRow(E row);
		
	abstract protected void insertRow(E row);
	
	abstract protected void updateRow(E oldData, E newData);
	
	abstract protected void remove(E row);
	
	abstract protected E createEmptyRow();
	
	public AbstractDBStore() {
		isMustUpdate = true;
		cursor = -1;
	}
	
	protected void setMustUpdate() {
		
		isMustUpdate = true;
	}

    @Override
    public void updateAllData() {
        setMustUpdate();

        getData();
    }

	@Override
	public void searchTo(List<E> filterData) {
		if (data == null || filterData == null) {
			return;
		}

		for (E row : data) {

			if (row.fieldItems().get(0).getValueField() == null) {
				continue;
			}

			for (E findRow : filterData) {
				if (row.fieldItems().get(0).getValueField()
						.equals(findRow.fieldItems().get(0).getValueField())) {
					setRow(row);
					return;
				}
			}
		}
	}

	private long getID(E row) {

		if(row == null) return -1;
		
		Number id = row.getId();
		
		return id == null ? -1 : id.longValue();
	}

	public String getName(E row) {
		if(row == null) return null;
		
		return (row).getName();
	}

	private boolean changeData(E oldData, E changeData) {
		if(oldData == null || changeData == null) return false;
		
		// вставка должна быть непустой иначе удаляем ее 
		if( getID(changeData) == -1 && 
			getName(changeData) == null) {
			
			data.remove(changeData);
			return false;
		}
		
		return !oldData.equals(changeData);
	}
	
	private void save(E oldData, E newData) {
		if(getID(newData) == -1) {
			insertRow(newData);
			setViewType( getTypeSelect() );

		} else {
			updateRow(oldData, newData);
		}
	}
	
	private int findByID(E oldCursor) {
		if(oldCursor == null || data == null || 
		   data.size()  == 0) {
			
			return -1;
		}
		
		long oldCur = getID(oldCursor);
		
		for (int index = 0; index < data.size(); index++) {
			 
			if(getID(data.get(index)) == oldCur) {
				return index;
			}
		}
		
		return 0;
	}
	
	private void changeCursor(E oldCursor) {
		if(data == null ||
			      data.size()  == 0) {
			
			cursor = -1;
		} else {
			cursor = findByID(oldCursor);
			
			if(cursor == -1) {
				cursor = 0;
			}
		}
	}

	@Override
	public List<E> getData() {
		if(isMustUpdate) {
			isMustUpdate = false;
			
			E oldCursor = (cursor == -1 || 
				      data == null || 
				      data.size()  <= cursor ) ? null : data.get(cursor);
			
			data = initData();
			
			//logger.info(" DBStoreSmsPacketContent data.size() = " + data.size());

			changeCursor(oldCursor);
			
			sendListenersRefreshAllData(StateRefresh.ALL);
			
			sendListenersCursor(getRow());
		}
		
		return data;
	}

	@Override
	public E getRow() {
		if(isMustUpdate) {
			isMustUpdate = false;
			data = initData();
			cursor = -1;
		}
		
		if(data != null && data.size() > 0 && cursor == -1) {
			cursor = 0;
		}

		return (data == null || data.size() == 0) ?
				null : data.get(cursor);
	}
	
	/**
	 * сообщает всем листенерам об изменении всех данных
	 */
    void sendListenersRefreshAllData(StateRefresh stateRefresh) {
		for (ListenerStore<E> listenerStore : listenersStore) {
			listenerStore.refreshData(data, stateRefresh);
		}
	}
	
	/**
	 * сообщает всем листенерам об изменении всех данных
	 */
	protected void sendListenersCursor(E row) {
		for (ListenerStore<E> listenerStore : listenersStore) {
			listenerStore.setCursor(row);
		}
	}
	

	private boolean isEqual(E row1, E row2) {
		if(row1 == row2) {
			return true;
		}
		
		if(row1 == null ||
		   row2 == null) {
			return false;
		}
		
		return row1.equals(row2);
	}

	@Override
	public void setRow(E row) {
		E oldCursor = (cursor == -1 || 
				      data == null || 
				      data.size()  == 0 ) ? null : data.get(cursor);
				
		if(isEqual(oldCursor, row))  return;
		
		if(changeData(oldCursorData, oldCursor)) {
			save(oldCursorData, oldCursor);
		}
		
		cursor = data.indexOf(row);
		if(cursor == -1 && getID(row) == -1) {
			data.add(row);
			cursor = data.indexOf(row);
		}
		
		//logger.info("setRow cursor=" + cursor);
		
		oldCursorData = cloneRow(row);
		
		sendListenersCursor(row);
	}

	@Override
	public void addListenerStore(ListenerStore<E> listenerStore) {
		synchronized (listenersStore) {
			listenersStore.add(listenerStore);
		}
	}

	@Override
	public void removeRow() {
		E oldCursor = (cursor == -1 || 
			      data == null || 
			      data.size()  == 0 ) ? null : data.get(cursor);
		
		if(oldCursor == null) return;
		
		remove(oldCursor);
		
		data.remove(oldCursor);
		
		if(data.size() <= cursor) {
			cursor = data.size() - 1;
		}
		
		oldCursor = (cursor == -1 || 
			      data == null || 
			      data.size()  == 0 ) ? null : data.get(cursor);
		
		oldCursorData = cloneRow(oldCursor);
		
		sendListenersRefreshAllData(StateRefresh.REMOVE_ITEM);
	}
	
}
