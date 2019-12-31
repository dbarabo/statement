package ru.barabo.statement.main.gui;

import ru.barabo.statement.afina.AfinaQuery;
import ru.barabo.statement.data.DelegateDataExtractExportXLS;
import ru.barabo.statement.data.IDataExtractExportXLS;
import ru.barabo.statement.main.resources.ResourcesManager;
import ru.barabo.statement.ui.ExtractXLSExport;
import ru.barabo.total.report.rtf.RtfReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Objects;

public class Start extends JFrame{
	
	//final static transient private Logger logger = Logger.getLogger(Start.class.getName());

	public Start() {

		if(!ModalConnect.initConnect(this)) {
			System.exit(0);
		}

        try {
            buildUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),null, JOptionPane.ERROR_MESSAGE );
            System.exit(0);
        }

	}

   /**
	 * рисуем интерфейс
	 */
	private void buildUI() {

		IDataExtractExportXLS data = new DelegateDataExtractExportXLS();
		JComponent mainPanel = buildExtractXLS(data);

		getContentPane().setLayout( new BorderLayout() );
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		setTitle( title() );

        setIconImage(Objects.requireNonNull(ResourcesManager.getIcon("exportXLS")).getImage());

		pack();
	    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setExtendedState(JFrame.NORMAL);
		setVisible( true );
	    // setExtendedState(JFrame.MAXIMIZED_BOTH);
	    // setVisible( true );
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Рисуем окно запуска отчета экспорта в эксиль выписки
	 * @param data
	 */
	private JComponent buildExtractXLS(IDataExtractExportXLS data) {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints gridConstLabel = new GridBagConstraints(
				0, 0, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		GridBagConstraints gridConstComp = new GridBagConstraints(
				1, 0, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		JLabel label = new JLabel("Счет:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JTextField account = new JTextField();
		panel.add(account, gridConstComp);
		/*
		 *
		 */

		gridConstLabel = new GridBagConstraints(
				0, 1, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 1, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("только с оборотами");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JCheckBox isTurn = new JCheckBox("за указанный период");
		panel.add(isTurn, gridConstComp);
		/*
		 *
		 */

		gridConstLabel = new GridBagConstraints(
				0, 2, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 2, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("Дата с:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		DateFormat dateFormat = DateFormat.getDateInstance();
		JFormattedTextField dateFrom = new JFormattedTextField(dateFormat);
		panel.add( label, gridConstLabel);
		panel.add( dateFrom, gridConstComp );


		gridConstLabel = new GridBagConstraints(
				0, 3, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 3, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("Дата по:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JFormattedTextField dateTo = new JFormattedTextField(dateFormat);
		panel.add( label, gridConstLabel);
		panel.add( dateTo, gridConstComp);

		gridConstLabel = new GridBagConstraints(
				0, 4, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 4, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		////
		label = new JLabel("отображать обороты:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JComboBox<String> rurCombo = new JComboBox<String>(new String[] {"в руб. эквиваленте", "в номинале"});
		rurCombo.setSelectedIndex(0);
		panel.add( label, gridConstLabel);
		panel.add( rurCombo, gridConstComp);

		gridConstLabel = new GridBagConstraints(
				0, 5, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 5, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		/////

		label = new JLabel("Путь к xls-файлу:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField path = new JTextField( RtfReport.getDefaultToDirectory().getAbsolutePath() );
		panel.add( label, gridConstLabel);
		panel.add( path, gridConstComp);

		gridConstLabel = new GridBagConstraints(
				0, 6, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 6, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);


		label = new JLabel("только открытые");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JCheckBox isOpened = new JCheckBox("в указанный период");
		panel.add(isOpened, gridConstComp);


		gridConstLabel = new GridBagConstraints(
				0, 7, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 7, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("Ежедневные остатки");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JCheckBox isShowEveryDay = new JCheckBox("показывать строкой");
		panel.add(isShowEveryDay, gridConstComp);


		/*gridConstLabel = new GridBagConstraints(
				0, 6, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 6, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);
*/
		/*
		label = new JLabel("Дата и номер запроса ФНС:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField fnsRequest = new JTextField("");
		panel.add( label, gridConstLabel);
		panel.add( fnsRequest, gridConstComp);


		gridConstLabel = new GridBagConstraints(
				0, 7, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 7, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("ИФНС:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField fnsName = new JTextField();
		panel.add( label, gridConstLabel);
		panel.add( fnsName, gridConstComp);

		gridConstLabel = new GridBagConstraints(
				0, 8, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 8, 4, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("Адрес ИФНС:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField fnsAddress = new JTextField();
		panel.add( label, gridConstLabel);
		panel.add( fnsAddress, gridConstComp);

		gridConstComp = new GridBagConstraints(
				0, 9, 4, 1, // плюс заголовок
				1.0, 1.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0);

		TableFNS tableFNS = new TableFNS(data, fnsName, fnsAddress);
		panel.add( new JScrollPane(tableFNS), gridConstComp);
*/


		DefaultListModel<String> model = new DefaultListModel<>();
		JList<String> accounts = new JList<String>(model);
		accounts.addMouseListener(getJListClicker(accounts));

		gridConstComp = new GridBagConstraints(
				0, 8, 4, 12, // плюс заголовок
				1.0, 1.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0);

		panel.add( new JScrollPane(accounts), gridConstComp);


		JButton buttonOk = new  JButton(new ExtractXLSExport(data, account,
				dateFrom, dateTo, path,  isTurn, rurCombo, accounts, this, isOpened, isShowEveryDay) );

		gridConstComp = new GridBagConstraints(
				3, 22, 1, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_END,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		panel.add( buttonOk, gridConstComp);


		return panel;
	}

	MouseListener getJListClicker(JList list ) {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String selectedItem = (String) list.getSelectedValue();

					if(selectedItem == null || selectedItem.trim().isEmpty()) return;

					try {
						Desktop.getDesktop().open(new File(selectedItem));
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null,
								"Не удалось открыть файл, попробуйте сами :(", null,
								JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		};
	}

	private String title() {
       String db = AfinaQuery.isTestBaseConnect() ? "TEST" : "AFINA";

        String user = AfinaQuery.getUser();

        return String.format(TITLE, db, user);
    }

    final private static String TITLE = "Выписки в Excel: [%s] [%s]";
}
