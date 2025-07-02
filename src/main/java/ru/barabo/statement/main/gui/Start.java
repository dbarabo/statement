package ru.barabo.statement.main.gui;

import ru.barabo.statement.afina.AfinaQuery;
import ru.barabo.statement.data.DelegateDataExtractExportXLS;
import ru.barabo.statement.data.IDataExtractExportXLS;
import ru.barabo.statement.main.Statement;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class Start extends JFrame{

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
	}

	/**
	 * Рисуем окно запуска отчета экспорта в эксиль выписки
	 * @param data
	 */
	private JComponent buildExtractXLS(IDataExtractExportXLS data) {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints gridConstLabel = new GridBagConstraints(
				0, 0, 1, 3, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		GridBagConstraints gridConstComp = new GridBagConstraints(
				1, 0, 3, 3, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		JLabel label = new JLabel("Счет:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JTextArea account = new JTextArea();
		account.setRows(3);
		//account.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		panel.add(new JScrollPane(account, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), gridConstComp);


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

		label = new JLabel("только с оборотами");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JCheckBox isTurn = new JCheckBox("за указанный период");
		panel.add(isTurn, gridConstComp);
		/*
		 *
		 */

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

		label = new JLabel("Дата с:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");//
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_YEAR, 1);

		JFormattedTextField dateFrom = new JFormattedTextField(dateFormat);
		//dateFrom.setText(dateFormat.format(cal.getTime()));
		dateFrom.setValue(cal.getTime());

		panel.add( label, gridConstLabel);
		panel.add( dateFrom, gridConstComp );


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

		label = new JLabel("Дата по:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		JFormattedTextField dateTo = new JFormattedTextField(dateFormat);
		//dateTo.setText(dateFormat.format(new Date()));
		dateTo.setValue(new Date());

		panel.add( label, gridConstLabel);
		panel.add( dateTo, gridConstComp);

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

		////
		label = new JLabel("отображать обороты:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JComboBox<String> rurCombo = new JComboBox<String>(new String[] {"в руб. эквиваленте", "в номинале"});
		rurCombo.setSelectedIndex(0);
		panel.add( label, gridConstLabel);
		panel.add( rurCombo, gridConstComp);

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

		/////

		label = new JLabel("Путь к xls-файлу:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField path = new JTextField( RtfReport.getDefaultToDirectory().getAbsolutePath() );
		panel.add( label, gridConstLabel);
		panel.add( path, gridConstComp);

		gridConstLabel = new GridBagConstraints(
				0, 8, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 8, 3, 1, // плюс заголовок
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
				0, 9, 1, 1, // плюс заголовок
				0.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		gridConstComp = new GridBagConstraints(
				1, 9, 3, 1, // плюс заголовок
				1.0, 0.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 0, 0);

		label = new JLabel("Ежедневные остатки");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label, gridConstLabel);
		JCheckBox isShowEveryDay = new JCheckBox("показывать строкой");
		panel.add(isShowEveryDay, gridConstComp);

		Statement.INSTANCE.addSearchClients(panel, 10);

		Statement.INSTANCE.addSignPosition1(panel, 11);

		Statement.INSTANCE.addSignFio1(panel, 12);

		Statement.INSTANCE.addSignPosition2(panel, 13);

		Statement.INSTANCE.addSignFio2(panel, 14);

		DefaultListModel<String> model = new DefaultListModel<>();
		JList<String> accounts = new JList<String>(model);
		accounts.addMouseListener(getJListClicker(accounts));

		gridConstComp = new GridBagConstraints(
				0, 15, 4, 10, // плюс заголовок
				1.0, 1.0,
				GridBagConstraints.PAGE_START,
				GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0);

		panel.add( new JScrollPane(accounts), gridConstComp);


		JButton buttonOk = new  JButton(new ExtractXLSExport(data, account,
				dateFrom, dateTo, path,  isTurn, rurCombo, accounts, this, isOpened, isShowEveryDay, Statement.INSTANCE.getClientVar()) );

		gridConstComp = new GridBagConstraints(
				3, 26, 1, 1, // плюс заголовок
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
