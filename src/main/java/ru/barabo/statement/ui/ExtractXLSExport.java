package ru.barabo.statement.ui;

import ru.barabo.statement.data.IDataExtractExportXLS;
import ru.barabo.xls.Record;
import ru.barabo.xls.Var;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;

/**
 * экспорт выписок в xls
 * @author debara
 *
 */
public class ExtractXLSExport extends AbstractAction {
    private IDataExtractExportXLS data;
    private JFormattedTextField dateFrom;
    private JFormattedTextField dateTo;
    private JTextArea account;
    private JTextField path;
    private JCheckBox isTurn;
    private JComboBox<String> rurCombo;
    private JList<String> accounts;
    private JFrame frame;
    private JCheckBox isOpened;
    private JCheckBox isShowRestEveryDay;

    private Var varClient;

    public ExtractXLSExport(IDataExtractExportXLS data,
                            JTextArea account, JFormattedTextField dateFrom,
                            JFormattedTextField dateTo, JTextField path,
                            JCheckBox isTurn, JComboBox rurCombo,
                            JList<String> accounts,
                            JFrame frame,
                            JCheckBox isOpened,
                            JCheckBox isShowRestEveryDay) {
        super();
        this.data = data;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.account = account;
        this.path = path;
        this.isTurn = isTurn;
        this.isOpened = isOpened;
        this.isShowRestEveryDay = isShowRestEveryDay;
        this.rurCombo = rurCombo;
        this.accounts = accounts;
        this.frame = frame;
        putValue(Action.NAME, "Выгрузить");
    }

    public ExtractXLSExport(IDataExtractExportXLS data,
                            JTextArea account, JFormattedTextField dateFrom,
                            JFormattedTextField dateTo, JTextField path,
                            JCheckBox isTurn, JComboBox rurCombo,
                            JList<String> accounts,
                            JFrame frame,
                            JCheckBox isOpened,
                            JCheckBox isShowRestEveryDay,
                            Var varClient) {

        this(data, account, dateFrom, dateTo, path, isTurn, rurCombo, accounts, frame, isOpened, isShowRestEveryDay);

        this.varClient = varClient;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String error = data.checkAccount(account.getText());
        if(error != null) {
            JOptionPane.showMessageDialog(null,
                    error, null,
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        final List<String> accountList = (data.startExport(account.getText(), (Date)dateFrom.getValue(),
                (Date)dateTo.getValue(), path.getText(),
                "",
                "",
                "",
                isTurn.isSelected(),
                rurCombo.getSelectedIndex() == 0,
                isOpened.isSelected(),
                isShowRestEveryDay.isSelected(),
                (Record) varClient.getResult().getValue()
                )) ;

        accounts.removeAll();

        for(String item : accountList ) {
            ((DefaultListModel<String>)accounts.getModel()).addElement(item);
        }

        final String report = !accountList.isEmpty()
                ? "Выписки успешно выгружены в файл!"
                : "Во время формирования выписки произошла ошибка!";

        JOptionPane.showMessageDialog(null,
                report, null,
                !accountList.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE );
    }
}
