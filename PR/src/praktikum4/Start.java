package praktikum4;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * Praktikumsaufgabe 4 Programmieren 2 WI4 Wdh.
 * 
 * @author Richard Schulze-Luther
 * 
 */

public class Start {

	private JButton alarm; 
	private JLabel uhrzeit; 
	private SimpleDateFormat sdf;
	private JTextField textField;
	private JButton abbrechen;
	private JSpinner stunden;
	private JSpinner minuten;
	private JLabel lblNewLabel; // Stunden
	private JLabel lblNewLabel_1; // Minuten
	private JPanel contentPanel;
	private JPanel contentPanel1;
	private JButton bestaetigung;
	private JFrame abfrage;
	private Thread t;
	private Date wecker; // speichert die Alarmuhrzeit
	private SimpleDateFormat weck;
	private JPopupMenu menu;
	private static int clickCounter = 0;
	private boolean alarmgesetzt;
	private JButton anhalten;
	private JFrame jf;
	private JMenuItem beenden;
	private JMenuItem info;
	private Font schrift = new Font("Arial", Font.BOLD, 12); 
	private Font schrift2 = new Font("Arial", Font.BOLD, 25);

	/**
	 * Konstruktor erstellt JFrame, JPanel und fügt diesem JLabel und den Alarm
	 * JButton hinzu. Erstellt Anhalten JButton, dieser wird aber nicht zum
	 * JPanel hinzugefügt.
	 * 
	 * @throws InterruptedException
	 */
	public Start() throws InterruptedException {
		this.jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Definiert wie das Frame geschlossen wird
		jf.setBounds(5, 5, 200, 120); // Setzt das Fenster (Koordinate x-Achse, y-Achse, Länge, Breite) 
		jf.setTitle("Wecker"); // Setzt den Titel des Fensters

		contentPanel = new JPanel(); // initialisiert neuen content Panel
		jf.addMouseListener(new MouseAdapter() { // fuegt den Mouselistner hinzu
			public void mouseClicked(MouseEvent e) { // Methode um MausEvent zu untersuchen
				clickCounter++;
				if (clickCounter % 2 == 0) { 
					menu.setVisible(false); // bei 2 klicks geht das menü wieder zu
				} else {
					popMenu(); // das popMenu wird aufgerufen
				}
			}
		});

		contentPanel.setBackground(Color.WHITE); // setzt die Hintergrundfarbe
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // setzt die Abstände zu den Rändern(oben, unten, links, rechts)
		jf.setContentPane(contentPanel); // setzt das JFrame mit dem contentPanel

		this.sdf = new SimpleDateFormat("HH:mm"); // setzt das Zeitformat

		this.uhrzeit = new JLabel(); // macht den JLabel für die Zeitanzeige
		uhrzeit.setFont(schrift2); // Schritftart der Zeit
		uhrzeit.setHorizontalAlignment(JTextField.CENTER); // setzt die Zeit centriert

		this.setDate(); // setzt die Zeit

		this.alarm = new JButton("Alarm setzen"); // erzeugt den Button alarm
		alarm.setFont(schrift); 

		alarm.addActionListener(new ActionListener() { // fuegt dem Button dem ActionListener hinzu
			@Override
			public void actionPerformed(ActionEvent e) { // ueberprueft das Event fuer den Button
				if (e.getSource() == alarm 
						&& alarm.getText().equals("Alarm setzen")) { // schaut ob der geklickte Button den gleichen Text hat wieder der Alarmbutton
					abfrageFenster(); // bei Uebereinstimmung wird Abfragefenster aufgerufen
				}
			}
		});

		this.anhalten = new JButton(""); // erzeugt den anhalten Button
		anhalten.setFont(schrift);
		anhalten.addActionListener(new ActionListener() {


			// ueberprueft was passiert wenn man den anhalten Button drueckt, wenn ja setzt er alles wieder zurueck
			@Override
			public void actionPerformed(ActionEvent e) { // ueberprueft das Event fuer den Button
				if (e.getSource() == anhalten) { // vergleicht ob gleiches Objekt
					alarmgesetzt = false; 
					contentPanel.setBackground(Color.WHITE); 
					contentPanel.remove(anhalten); 
					contentPanel.add(alarm); 
					jf.validate();
				}
			}

		});
		// fuegt uhrzeit, alarm, Layout dem Panel hinzu
		contentPanel.add(uhrzeit);
		contentPanel.add(alarm);
		contentPanel.setLayout(new GridLayout(2, 1));
		jf.setVisible(true);
	}

	/**
	 * Startet einen Thread der die Uhrzeit aktualsiert(wenn Alarm gestellt wird
	 * (checkTime()) überprüft ob die Uhrzeit im Frame der Weckzeit entspricht),
	 * eine Minute schläft, Uhrzeit aktualisiert usw....
	 * 
	 * @throws InterruptedException
	 */
	public void setDate() throws InterruptedException {
		this.t = new Thread() {
			
			// setzt einen Thread für das Datum, solange dieses nicht unterborchen wird die while-Schleife ausgeführt
			@Override
			public void run() {
				while (!isInterrupted()) {
					uhrzeit.setText(sdf.format(new Date()));
					if (alarmgesetzt == true) {
						checkTime();
					}
					try {
						TimeUnit.MINUTES.sleep(1); // setzt dem Thread eine Pause von 1 Minute
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start(); // starte den Thread
	}

	/**
	 * Öffnet ein neues Frame wo die Weckzeit angegeben werden muss. Wenn
	 * Weckzeit gestellt wird der Alarm JButton gegen einen Anhalten JButton
	 * ausgetauscht
	 */
	public void abfrageFenster() {
		this.abfrage = new JFrame();
		this.abfrage.setBounds(5, 5, 400, 200);
		abfrage.setTitle("Bitte Alarmzeit eingeben:");
		contentPanel1 = new JPanel();
		contentPanel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		abfrage.setContentPane(contentPanel1);

		textField = new JTextField("Test");
		textField.setColumns(10);

		JLabel lblBeschreibung = new JLabel("Beschreibung:");

		this.bestaetigung = new JButton("OK");
		bestaetigung.addActionListener(new ActionListener() {
			// ueberprueft was passiert wenn man den OK Button drueckt
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == bestaetigung) {
					contentPanel.setBackground(Color.RED);
					
					int hours = (int) stunden.getValue();
					int minutes = (int) minuten.getValue();
					// wandelt die Zeit von eine Integer zu einem Dateobject um
					SimpleDateFormat wecker = new SimpleDateFormat("HH:mm");
					Date da = new Date();
					Calendar cal = new GregorianCalendar();
					cal.set(Calendar.HOUR_OF_DAY, hours);
					cal.set(Calendar.MINUTE, minutes);
					da = cal.getTime();
					// Formatiert das Datum, zu einer Zeitangabe
					anhalten.setText("Alarm " + wecker.format(da)
							+ " anhalten!");
					// wenn Alarmausgefuehrt wurde wird alles wieder zum Ausgangszustand gesetzt
					alarmgesetzt = true;
					contentPanel.remove(alarm);
					contentPanel.add(anhalten);
					jf.validate();
					abfrage.dispose();
					setTime(da);
					checkTime();
				}
			}
		});
		// initalisiert den Abbrechen Button
		this.abbrechen = new JButton("Abbrechen");
		abbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == abbrechen) {
					abfrage.dispose();
				}
			}
		}); 
		// Eingabe fuer Stunden
		stunden = new JSpinner();
		SpinnerModel modelStunden = new SpinnerNumberModel(0, 0, 23, 1);
		stunden.setModel(modelStunden);
		// Eingabe fuer Minuten
		minuten = new JSpinner();
		SpinnerModel modelMinuten = new SpinnerNumberModel(0, 0, 59, 1);
		minuten.setModel(modelMinuten);
		// Textangabe
		lblNewLabel = new JLabel("Stunde:");
		lblNewLabel_1 = new JLabel("Minute:");
		// fuegt alles dem contenPanel1 hinzu
		contentPanel1.add(lblNewLabel);
		contentPanel1.add(stunden);

		contentPanel1.add(lblNewLabel_1);
		contentPanel1.add(minuten);

		contentPanel1.add(lblBeschreibung);
		contentPanel1.add(textField);

		contentPanel1.add(bestaetigung);
		contentPanel1.add(abbrechen);

		contentPanel1.setLayout(new GridLayout(4, 2));
		abfrage.setVisible(true);
	}

	/**
	 * Setzt die Weckzeit, wird von abfrageFenster aufgerufen.
	 * 
	 * @param date
	 */
	public void setTime(Date date) {
		this.wecker = date;
	}

	/**
	 * Überprüft ob die Uhrzeit im JFrame der Weckzeit entspricht
	 */
	public void checkTime() {
		this.weck = new SimpleDateFormat("HH:mm"); // wird zu einem Zeitformat initalisiert
		Date da = null;
		// versucht den geholten Zeit zu einem Zeitformat zu parsen
		try {
			da = sdf.parse(uhrzeit.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/* vergleicht Alarmzeit mit der Uhrzeit, 
		   wenn uebereinstimmung dann wird alles wieder zum Ausgangszustand und ein Dialogfenster wird geoeffnet 
		 */
		if (weck.format(wecker).equals(weck.format(da))) {
			contentPanel.setBackground(Color.WHITE);
			alarmgesetzt = false;
			contentPanel.remove(anhalten);
			contentPanel.add(alarm);
			JOptionPane.showMessageDialog(null, textField.getText(), "Wecker",
					JOptionPane.WARNING_MESSAGE);
			uhrzeit.setText(sdf.format(new Date()));
		}
	}

	/**
	 * Erstellt ein JPopMenu mit zwei Items: "Info", "Beenden"
	 */
	public void popMenu() {
		this.menu = new JPopupMenu();
		menu.setLocation(jf.getLocation());
		this.beenden = new JMenuItem("Beenden");
		this.info = new JMenuItem("Info");
		menu.add(beenden);
		beenden.addActionListener(new ActionListener() {
			// vergleicht ob gleiches Objekt, bei ActionEvent fuer Beenden
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == beenden) {
					System.exit(0);
				}
			}

		});
		menu.add(info);
		info.addActionListener(new ActionListener() {
			//vergleicht ob gleiches Objekt, ActionEvent fuer Info
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == info) {
					menu.setVisible(false);
					JOptionPane
							.showMessageDialog(null,
									"Dies ist eine Uhr mit einstellbaren Wecker\nVersion 1.0");
				}
			}

		});
		menu.setVisible(true);
	}

	/**
	 * Ruft nur den Konstruktor auf und legt das Design fest
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException, InterruptedException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Start();
	}
}