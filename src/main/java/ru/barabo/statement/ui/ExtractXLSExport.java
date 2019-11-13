package ru.barabo.statement.ui;

import ru.barabo.statement.data.IDataExtractExportXLS;

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
    private JTextField account;
    private JTextField path;
    private JCheckBox isTurn;
    private JComboBox<String> rurCombo;
    private JList<String> accounts;
    private JFrame frame;
    private JCheckBox isOpened;

    public ExtractXLSExport(IDataExtractExportXLS data,
                            JTextField account, JFormattedTextField dateFrom,
                            JFormattedTextField dateTo, JTextField path,
                            JCheckBox isTurn, JComboBox rurCombo,
                            JList<String> accounts,
                            JFrame frame,
                            JCheckBox isOpened) {
        super();
        this.data = data;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.account = account;
        this.path = path;
        this.isTurn = isTurn;
        this.isOpened = isOpened;
        this.rurCombo = rurCombo;
        this.accounts = accounts;
        this.frame = frame;
        putValue(Action.NAME, "Выгрузить");
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
                /*fnsName.getText(),
                fnsAddress.getText(),
                fnsRequest.getText(),*/
                isTurn.isSelected(),
                rurCombo.getSelectedIndex() == 0,
                isOpened.isSelected())) ;

        accounts.removeAll();

        for(String item : accountList ) {
            ((DefaultListModel<String>)accounts.getModel()).addElement(item);
        }

        frame.pack();

        final String report = !accountList.isEmpty()
                ? "Выписки успешно выгружены в файл!"
                : "Во время формирования выписки произошла ошибка!";

        JOptionPane.showMessageDialog(null,
                report, null,
                !accountList.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE );
    }
}
