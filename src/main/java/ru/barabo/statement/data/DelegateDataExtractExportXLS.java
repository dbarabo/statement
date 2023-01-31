package ru.barabo.statement.data;

import org.apache.log4j.Logger;
import ru.barabo.db.SessionException;
import ru.barabo.db.SessionSetting;
import ru.barabo.statement.afina.AfinaQuery;
import ru.barabo.statement.jexel.ExportExtract;
import ru.barabo.statement.main.resources.ResourcesManager;
import ru.barabo.xls.Record;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class DelegateDataExtractExportXLS implements IDataExtractExportXLS {

	private int selectedRow;
	private Vector<Object[]> fns;
	
	static private final transient String[] FNS_CLOUMNS = new String[] {"Наименование", "Адрес"};
	
	static private final String EXEC_EXTRACT = "call od.PTKB_EXPORT_EXTRACT(?, ?, ?, ?, ?)";
	
	static private final String SEL_SID = //"select sid from v$mystat where rownum = 1";
			"select classified.nextval from dual";
		//"select od.PTKB_PLAN_TYPEBANK.nextval from dual";

	private final String SEL_ACCOUNT = "select a.code from od.account a, od.doctree dt "
			+ " where dt.classified = a.doc and a.code like ? "
			+ " and a.sysfilial = 1 and a.foldaccount is null and dt.docstate in (1000000035, 1000000039)";
	
	
	private final String SEL_TURN_ONLY = " and exists (select * from bbook b where a.doc in (b.debaccount, b.credaccount) "
			+ " and b.operdate >= ? and b.operdate < ? + 1 )";

	private final String SEL_CLIENT = " and a.client = ?";

	private final String SEL_OPEN_ONLY = " and a.opened <= ? and a.closed >= ? "; // кОНЕЦ начало

	static private final String SEL_DATA =
		"select ORD, to_char(OPER, 'dd.mm.yy'), SHIFR, NUMBER_DOC, to_char(DATE_DOC, 'dd.mm.yy'), BANK_ACCOUNT, BANK_NAME, BANK_BIK, PAY_NAME, " + 
		" PAY_INN, PAY_KPP, PAY_ACCOUNT, to_char(SUM_DEB, '99999999990d00'), to_char(SUM_CRED, '99999999990d00'), DESCRIPTION,  to_char(REST_IN, '99999999990d00')" +
		" from od.PTKB_TMP_EXTRACT where SID = ? order by ORD";
	
	
	static private final String SEL_FNS = "select distinct c.label, c.address " +
			"from client c " +
			", ObjProp op " +
			"where c.classified = op.Obj " +
			"and op.Status is not null and op.Status != 0 " +
			"and op.Prop = 1000002575 ";
	
	static private final String DEL_SID = "delete from od.PTKB_TMP_EXTRACT where sid = ?";
	
	transient static private final Logger logger = Logger.getLogger(DelegateDataExtractExportXLS.class.getName());
		
	
	public DelegateDataExtractExportXLS() {
		this.selectedRow = 0;
	}

	@Override
	public int getFNSColumnCount() {
		return FNS_CLOUMNS.length;
	}

	@Override
	public String getFNSColumnName(int columnIndex) {
		return FNS_CLOUMNS[columnIndex];
	}

	@Override
	public int getFNSRowCount() {
		return fns.size();
	}
	
	@Override
	public Object getFNSValue(int rowIndex, int columnIndex) {
		if(rowIndex >= fns.size() || fns.get(rowIndex) == null 
				|| fns.get(rowIndex).length <= columnIndex ) return null;
		
		return fns.get(rowIndex)[columnIndex];
	}

	@Override
	public void setFNSSelectedRow(int row) {
		selectedRow = row;
	}
	
	public String checkAccount(String accounts) {

		String accountsOra = accounts.trim().replace('*', '%').replace('?', '_');

		String[] accountMasks = Pattern.compile("[,;\\s\\n]").split(accountsOra);

		boolean isFind = false;

		for (String accountMask : accountMasks) {

			if(accountMask.isEmpty()) continue;

			if((accountMask.length() < 10) &&
					(accountMask.indexOf('%') < 0)) {
				accountMask = accountMask + "%";
			}

			if (!AfinaQuery.INSTANCE.select(SEL_ACCOUNT, new Object[] { accountMask }).isEmpty()) {
				return null;
			}

		}
		return "Открытые или закрытые счет(а) не найдены для маски счета " + accounts;
	}

	private Object getSid() {

		return AfinaQuery.INSTANCE.selectValue(SEL_SID, null);
	}

	@Override
	public List<String> startExport(String account, Date dateFrom, Date dateTo,
									String path, String fnsName, String fnsAddress, String fnsRequest, boolean isTurn,
									boolean isRur, boolean isOpened, boolean isShowRestEveryDay, Record clientId) {

		logger.error("startExport account=" + account);

		String select = SEL_ACCOUNT;
		Object[] params = new Object[]{account};
		if(isTurn) {
			select += SEL_TURN_ONLY;
			params = new Object[]{account, new java.sql.Date(dateFrom.getTime()), new java.sql.Date(dateTo.getTime()) };
		}

		String accountsOra = account.trim().replace('*', '%').replace('?', '_');

		String[] accountMasks = Pattern.compile("[,;\\s\\n]").split(accountsOra);

		logger.error("accountsOra=" + accountsOra);

		if(isOpened) {
			select += SEL_OPEN_ONLY;

            params = Arrays.copyOf(params, params.length + 2);
            params[params.length - 2] =  new java.sql.Date(dateTo.getTime());
            params[params.length - 1] =  new java.sql.Date(dateFrom.getTime());
		}

		Object clientIdValue = clientId == null ? null : clientId.columnByName("ID").getResult().getValue();

		if(clientIdValue != null) {
			select += SEL_CLIENT;

			params = Arrays.copyOf(params, params.length + 1);

			params[params.length - 1] =  clientIdValue;
		}

		List<String> result = new ArrayList<>();

		for (String accountMask : accountMasks) {

			logger.error("accountMask=" + accountMask);

			if(accountMask.isEmpty()) continue;

			if((accountMask.length() < 10) &&
					(accountMask.indexOf('%') < 0)) {
				accountMask = accountMask + "%";
			}
			params[0] = accountMask;

			logger.error("accountMask=" + accountMask);

			List<Object[]> values = AfinaQuery.INSTANCE.select(select, params);

			Number rur = isRur ? 1 : 0;

			for (Object[] acc : values) {

				Object sid = getSid();
				account = (String) acc[0];

				SessionSetting unicSession = AfinaQuery.INSTANCE.uniqueSession();

				try {
					AfinaQuery.INSTANCE.execute(EXEC_EXTRACT,
							new Object[]{new java.sql.Date(dateFrom.getTime()), new java.sql.Date(dateTo.getTime()), account, sid, rur},
							unicSession, null);

					Vector<Object[]> data = new Vector<>(AfinaQuery.INSTANCE.select(SEL_DATA, new Object[]{sid}, unicSession));

					AfinaQuery.INSTANCE.execute(DEL_SID, new Object[]{sid}, unicSession, null);

					AfinaQuery.INSTANCE.rollbackFree(unicSession);

					File pathFile = new File(path);

					if (!pathFile.exists()) {
						pathFile.mkdirs();
					}

					result.add(exportXLS(data, path, account, fnsName, fnsAddress, fnsRequest, isShowRestEveryDay));

				} catch (SessionException e) {
					logger.error(EXEC_EXTRACT, e);

					AfinaQuery.INSTANCE.rollbackFree(unicSession);
				}
			}
		}
		return result;
	}
	
	/**
	 * Экспортирует данные в эксель книгу
	 */
	private String exportXLS(Vector<Object[]> data, String path,
			String account, String fnsName, String fnsAddress, String fnsRequest, boolean isShowRestEveryDay) {
		String maket = "/maketv.xls";
		
		String newMaket = path + "/" + account + ".xls";
		
		File oldFile = new File(path + maket);
		if(!oldFile.exists()) {
			ResourcesManager.INSTANCE.copyFromJar(new File(path), maket);
		}

		ExportExtract exp = new ExportExtract();
		
		exp.export(oldFile, data, newMaket, fnsName, fnsAddress, fnsRequest, isShowRestEveryDay);

		oldFile.delete();
		
		return newMaket;
	}

	@Override
	public String getFNSSelected(int columnIndex) {
		return (String)getFNSValue(selectedRow, columnIndex);
	}
}
