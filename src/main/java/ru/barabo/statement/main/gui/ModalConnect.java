package ru.barabo.statement.main.gui;

import ru.barabo.statement.afina.AfinaConnect;
import ru.barabo.statement.main.resources.owner.Cfg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * @author debara
 * 
 * Модальное окно - подключение к серверу
 * вид
 * 
 * логин:
 * пароль:
 * вход в:
 */
class ModalConnect extends JDialog {
	
	//final static transient private Logger logger = Logger.getLogger(ModalConnect.class.getName());

	static final private String CONNECT_TEXT = "соединение с сервером";
	
	static final private String items[] = new String[]{"BARDV", "SHERBO"};

	private static final String SERVER_NAME[] = new String[] {
			"jdbc:oracle:thin:@" + Cfg.query().afinaIP() + ":" + Cfg.query().port() + ":" + Cfg.query().afinaSID(),
			"jdbc:oracle:thin:@" + Cfg.query().testIP() + ":" + Cfg.query().port() + ":" + Cfg.query().testSID()
	};
	
	static private ModalConnect modalConnect;

	volatile static private boolean isConnected = false;
	private JComboBox cb;
	private JPasswordField  tf;

	private JComboBox servers;

    static private Preferences userPrefs;

    static Boolean initConnect(JFrame mainWin) {

		modalConnect = new ModalConnect(mainWin);

        return isConnected;
	}

	
	private ModalConnect(JFrame mainWin) {
		super(mainWin, CONNECT_TEXT, true);
		
		userPrefs = Preferences.userRoot().node("plan");
        isConnected = false;
		
		modalConnect = this;
		
		buildUI();
	}

	private void buildUI() {
		setMinimumSize(new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width / 5 , Toolkit.getDefaultToolkit().getScreenSize().height / 20));
		setLocationRelativeTo(null);
		
		getContentPane().setLayout( new GridLayout(4, 2, 20, 10) );
		
		tf = new ModPasswordField();
		tf.requestFocus();
		tf.grabFocus();

        JLabel label = new JLabel("Логин:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(label);
		cb = new JComboBox<>(items);
		cb.setEditable(true);
		cb.setSelectedItem(userPrefs.get("login", ""));
		getContentPane().add(cb);
		
		label = new JLabel("Пароль:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(label);
		getContentPane().add(tf);
	
		servers = new JComboBox<>(new String[]{"AFINA", "TEST"});
		servers.setSelectedIndex(userPrefs.getInt("server", 0));
		label = new JLabel("Вход в:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(label);
		getContentPane().add( servers);

        JButton buttonOk = new JButton(new ButtonOk("Ok"));
		JButton buttonCancel = new  JButton(new ButtonCancel("Отмена") );
		
		JPanel panelButton = new JPanel();
		panelButton.setLayout( new GridLayout(1, 2, 10, 0) );
		panelButton.add(buttonOk);
		panelButton.add(buttonCancel);

		getContentPane().add(new JLabel());
		getContentPane().add(panelButton);

		pack();
		
		tf.requestFocusInWindow();
		
		setVisible( true );
	}
	
	private static void saveParam() {
		userPrefs.put("login", (modalConnect.cb.getSelectedItem() == null) ? null : modalConnect.cb.getSelectedItem().toString());
		userPrefs.putInt("server", modalConnect.servers.getSelectedIndex());
	}

	/**
	 * выбрали соединение
	 */
	private void setConnected() {

        isConnected = AfinaConnect.init(SERVER_NAME[servers.getSelectedIndex()],
                (String) Objects.requireNonNull(cb.getSelectedItem()),
                new String(tf.getPassword()));

        if(!isConnected) {
            JOptionPane.showMessageDialog(null,
                    "Неправильно набран пароль или логин!",
                    null, JOptionPane.ERROR_MESSAGE );
        } else {
            saveParam();
            setVisible(false);
        }
	}
	
	class ModPasswordField extends JPasswordField {
		ModPasswordField() {
			this.addKeyListener(    new KeyListener() {
			      
					@Override
					public void keyPressed(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
						if(e.getKeyChar() == KeyEvent.VK_ENTER) {
							ModalConnect.this.setConnected();
						}
					}

					@Override
					public void keyTyped(KeyEvent e) {
						
					}
				}
			    );
		}
	}
	
	class ButtonOk extends AbstractAction {
		
		ButtonOk (String name) {
			putValue(Action.NAME, name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ModalConnect.this.setConnected();
		}
	}

	class ButtonCancel extends AbstractAction {

		ButtonCancel (String name) {
			putValue(Action.NAME, name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			isConnected = false;
			ModalConnect.this.setVisible(false);
		}
	}
}

