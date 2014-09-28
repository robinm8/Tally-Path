package workspace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class GUI extends JFrame {
	private Link parent;

	boolean showingList = false;

	String currentPerson = "";

	JFrame peopleUI = new JFrame("People");

	JFileChooser fc = new JFileChooser();
	JLabel state = new JLabel("Ready");

	JPanel bottom = new JPanel(new BorderLayout());
	JPanel right = new JPanel(new BorderLayout());
	JPanel east = new JPanel(new GridLayout());

	JTabbedPane tabbedPane = new JTabbedPane();

	JPanel rightUp = null;
	JTextArea textArea = null;
	JScrollPane textScroll = null;

	JButton about = new TranslucentButton("About", null);
	JButton importFromTextFile = new TranslucentButton("Edit Profile", null);
	JButton analysis = new TranslucentButton("Analysis", null);
	JButton runStop = new TranslucentButton("Randomize", null);
	JButton callLimit = new TranslucentButton("Call Limit", null);
	JButton hideRankChange = new TranslucentButton("Hide Rank Changes", null);
	JLabel randName = new JLabel("Mark");
	JLabel rank = new JLabel("Rank: ");
	JLabel ans = new JLabel("text");
	JPanel centerInfo = new JPanel();
	JPanel randomizePanel = new JPanel();
	JButton correct = new JButton("Correct");
	JButton incorrect = new JButton("Incorrect");
	JButton skip = new JButton("Skipped");

	Image resizedimage2;
	ImageIcon resizedimage3;
	JLabel content;

	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Dimension dim = toolkit.getScreenSize();

	public GUI(final Link parent) {
		super(parent.app + " by Mark Robinson");

		parent.startApp();

		textArea = new JTextArea();

		textScroll = new JScrollPane(textArea);

		textScroll.setOpaque(false);
		textScroll.setAutoscrolls(false);
		textScroll.setBorder(null);
		textScroll.getViewport().setOpaque(false);

		textArea.setFont(new Font("Arial", Font.BOLD, 20));
		textArea.setFocusable(false);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		textArea.setForeground(Color.LIGHT_GRAY);
		textArea.setVisible(true);

		randName.setVisible(false);
		rank.setVisible(false);
		ans.setVisible(false);
		correct.setVisible(false);
		incorrect.setVisible(false);
		skip.setVisible(false);

		centerInfo.add(correct);
		centerInfo.add(incorrect);
		centerInfo.add(skip);
		centerInfo.setOpaque(false);

		randomizePanel.add(runStop);
		randomizePanel.setOpaque(false);

		if (parent.currentProfile != "") {
			changeState("Current Profile: " + parent.currentProfile);
		}

		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new MyFilter());

		textArea.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				Random rand = new Random();
				float r = rand.nextFloat();
				float g = rand.nextFloat();
				float b = rand.nextFloat();
				Color color = new Color(r, g, b);

				textArea.setForeground(color);
			}
		});

		hideRankChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.showRankChange = !parent.showRankChange;
				rank.setVisible(parent.showRankChange);
				if (parent.showRankChange) {
					hideRankChange.setText("Hide Rank Change");
				} else {
					hideRankChange.setText("Show Rank Change");
				}
			}
		});

		correct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				correct.setVisible(false);
				incorrect.setVisible(false);
				skip.setVisible(false);
				Element person = null;

				for (int ni = 0; ni < getProfileElement().getChildNodes()
						.getLength(); ni++) {
					if (getProfileElement().getChildNodes().item(ni)
							.getTextContent() == currentPerson
							|| getProfileElement().getChildNodes().item(ni)
									.getTextContent().equals(currentPerson)) {
						person = (Element) getProfileElement().getChildNodes()
								.item(ni);
					}
				}

				int saved = Integer.parseInt(person.getAttribute("yes"));

				person.setAttribute("yes", String.valueOf(saved + 1));

				Integer[] results = parent.getInfo(currentPerson);

				rank.setText("Rank: " + results[0]);

				ans.setText(results[1] + " correct; " + results[2]
						+ " incorrect; " + results[3] + " skipped");
				if (parent.showRankChange) {
					textArea.setVisible(true);
					textArea.setText(parent.getComparionOfRanks());
				}
			}
		});

		incorrect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				correct.setVisible(false);
				incorrect.setVisible(false);
				skip.setVisible(false);

				Element person = null;

				for (int ni = 0; ni < getProfileElement().getChildNodes()
						.getLength(); ni++) {
					if (getProfileElement().getChildNodes().item(ni)
							.getTextContent() == currentPerson
							|| getProfileElement().getChildNodes().item(ni)
									.getTextContent().equals(currentPerson)) {
						person = (Element) getProfileElement().getChildNodes()
								.item(ni);
					}
				}

				int saved = Integer.parseInt(person.getAttribute("no"));

				person.setAttribute("no", String.valueOf(saved + 1));

				Integer[] results = parent.getInfo(currentPerson);

				rank.setText("Rank: " + results[0]);

				ans.setText(results[1] + " correct; " + results[2]
						+ " incorrect; " + results[3] + " skipped");
				if (parent.showRankChange) {
					textArea.setVisible(true);
					textArea.setText(parent.getComparionOfRanks());
				}
			}
		});

		skip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				correct.setVisible(false);
				incorrect.setVisible(false);
				skip.setVisible(false);

				Element person = null;

				for (int ni = 0; ni < getProfileElement().getChildNodes()
						.getLength(); ni++) {
					if (getProfileElement().getChildNodes().item(ni)
							.getTextContent() == currentPerson
							|| getProfileElement().getChildNodes().item(ni)
									.getTextContent().equals(currentPerson)) {
						person = (Element) getProfileElement().getChildNodes()
								.item(ni);
					}
				}

				int saved = Integer.parseInt(person.getAttribute("skips"));

				person.setAttribute("skips", String.valueOf(saved + 1));

				Integer[] results = parent.getInfo(currentPerson);

				rank.setText("Rank: " + results[0]);

				ans.setText(results[1] + " correct; " + results[2]
						+ " incorrect; " + results[3] + " skipped");
				if (parent.showRankChange) {
					textArea.setVisible(true);
					textArea.setText(parent.getComparionOfRanks());
				}
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, parent.desc);
			}
		});

		importFromTextFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showList();
			}
		});

		callLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String s = (String) JOptionPane
						.showInputDialog(
								null,
								"Input how many randomizations must pass before the same person can be called again.\n"
										+ "Input a number more than 0 and less than the number of non-absent people in the current profile ("
										+ (parent.numCallable()) + ")",
								String.valueOf(parent.cantCallWithin));
				if ((s != null) && (s.length() > 0)
						&& Integer.parseInt(s) < parent.numCallable()
						&& Integer.parseInt(s) > 0) {
					parent.cantCallWithin = Integer.parseInt(s);
					parent.recent = (String[]) (new String[parent.cantCallWithin]);

					randName.setVisible(false);
					rank.setVisible(false);
					ans.setVisible(false);
					correct.setVisible(false);
					incorrect.setVisible(false);
					skip.setVisible(false);
				}
			}
		});

		runStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println(" Hey " + parent.numCallable());

				System.out.println("Recent length = " + parent.recent.length);

				if (parent.doc.getDocumentElement()
						.getElementsByTagName("profile").getLength() >= 0) {
					if (parent.items.length <= parent.cantCallWithin) {
						parent.cantCallWithin = parent.numCallable() - 1;
						parent.recent = (String[]) (new String[parent
								.numCallable()]);
						System.out.println("can't call within value reset.");
					}

					boolean allowed = false;
					String selected;
					do {
						selected = parent.selectName();
						System.out.println(selected);
						if (selected != null) {
							System.out.println("selected is " + selected
									+ " | absent? = "
									+ parent.isAbsent(selected) + " | Recent? "
									+ parent.isRecent(selected));
							if (!parent.isRecent(selected)) {
								allowed = true;
							} else {
								allowed = false;
							}
						} else {
							allowed = true;
						}
					} while (allowed == false || selected == "absent");

					System.out.println("Selected " + selected);

					if (selected != null) {
						parent.addRecent(selected);

						Integer[] results = parent.getInfo(selected);
						System.out.println("Rank: " + results[0]);

						randName.setVisible(true);
						rank.setVisible(parent.showRankChange);
						ans.setVisible(true);
						correct.setVisible(true);
						incorrect.setVisible(true);
						skip.setVisible(true);
						textArea.setVisible(false);
						randName.setText(selected);
						rank.setText("Rank: " + results[0]);
						ans.setText(results[1] + " correct; " + results[2]
								+ " incorrect; " + results[3] + " skipped");

						parent.oldRanks.clear();
						parent.setOldRanks();

						currentPerson = selected;

						changeState("Current Profile: " + parent.currentProfile);
					} else {
						changeState("This profile needs more people.");
					}
				} else {
					changeState("You must create a profile through the 'Edit Profile' button before trying to randomize.");
				}
			}
		});

		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);
		this.parent = parent;

		this.setBounds((dim.width * 20) / 100, (dim.height * 25) / 100,
				(dim.width * 60) / 100, (dim.height * 50) / 100);

		tabbedPane.setOpaque(false);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setForeground(Color.LIGHT_GRAY);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(-1, -1, -1, 0));

		tabbedPane.setPreferredSize(new Dimension(((dim.width * 20) / 100),
				(dim.height * 50) / 100));

		tabbedPane.setUI(new BasicTabbedPaneUI() {
			@Override
			protected void installDefaults() {
				super.installDefaults();
				highlight = new Color(0, 0, 0, 0);
				lightHighlight = new Color(0, 0, 0, 0);
				shadow = new Color(0, 0, 0, 0);
				darkShadow = new Color(0, 0, 0, 0);
				focus = new Color(0, 0, 0, 0);
			}
		});

		randName.setFont(new Font("sansserif", Font.BOLD, 36));
		randName.setForeground(Color.BLUE);
		randName.setHorizontalAlignment(JTextField.CENTER);

		ans.setHorizontalAlignment(JLabel.CENTER);
		ans.setFont(new Font("sansserif", Font.PLAIN, 17));
		ans.setOpaque(false);

		rank.setFont(new Font("sansserif", Font.BOLD, 20));
		rank.setHorizontalAlignment(JTextField.CENTER);

		runStop.setFont(new Font("sansserif", Font.BOLD, 20));
		runStop.setHorizontalAlignment(JButton.CENTER);

		rightUp = new JPanel(new BorderLayout());
		JPanel rightUpDub = new JPanel(new GridLayout(6, 0));
		JPanel rightDown = new JPanel(new BorderLayout());

		JPanel settings = new JPanel(new GridLayout(7, 0, 0, 5));
		JPanel menu = new JPanel(new GridLayout(7, 0, 0, 5));

		menu.add(about);
		menu.add(importFromTextFile);
		// menu.add(switchProfile);
		// menu.add(analysis);
		// menu.add(runStop);

		tabbedPane.addTab("Menu", null, menu, "Show " + parent.app + "'s Menu");

		settings.add(callLimit);
		settings.add(hideRankChange);

		tabbedPane.addTab("Settings", null, settings, "Modify " + parent.app
				+ "'s Settings");

		rightUpDub.add(new JLabel(""));
		rightUpDub.add(randName);
		rightUpDub.add(rank);
		rightUpDub.add(ans);
		rightUpDub.add(centerInfo);
		rightUpDub.add(randomizePanel);

		rightUp.add(rightUpDub, BorderLayout.CENTER);

		menu.setOpaque(false);
		settings.setOpaque(false);
		east.setOpaque(false);
		right.setOpaque(false);
		rightUp.setOpaque(false);
		rightUpDub.setOpaque(false);
		rightDown.setOpaque(false);
		bottom.setOpaque(false);

		state.setFont(new Font("sansserif", Font.BOLD, 16));
		state.setForeground(Color.GRAY);

		bottom.add(state, BorderLayout.SOUTH);
		right.add(rightUp, BorderLayout.NORTH);
		east.add(textScroll, BorderLayout.EAST);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				parent.resizeRequested = true;
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		peopleUI.setBounds((dim.width * 35) / 100, (dim.height * 30) / 100,
				(dim.width * 30) / 100, (dim.height * 40) / 100);
		peopleUI.setResizable(false);
		peopleUI.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		peopleUI.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				hideList();
			}
		});

		JPanel peoContent = new JPanel(new BorderLayout());
		JPanel peoNorth = new JPanel(new GridLayout(2, 2));
		JPanel peoCenter = new JPanel(new BorderLayout());
		JPanel peoSouth = new JPanel(new BorderLayout());
		JPanel peoSouthNorth = new JPanel(new GridLayout(1, 3));
		final JTextField input = new JTextField(20);
		final JLabel cProfile = new JLabel("Current Profile: "
				+ parent.currentProfile);
		final JList list = new JList(parent.items);
		final JButton makeProfile = new JButton("Make Profile");
		final JButton profile = new JButton("Switch Profile");
		final JButton renameProfile = new JButton("Rename Profile");
		final JButton deleteProfile = new JButton("Delete Profile");
		final JButton remove = new JButton("Remove");
		final JButton add = new JButton("Add");
		final JButton fromText = new JButton("Import From File");
		final JScrollPane listScroller = new JScrollPane(list);

		listScroller.setPreferredSize(new Dimension(200, 80));
		input.setText("Input Name Here!");

		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(0);

		list.setCellRenderer(new MyCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			int lastSelectedIndex;

			public void mouseClicked(MouseEvent e) {
				System.out.println("Mouse clicked list");
				int index = list.locationToIndex(e.getPoint());
				if (index != -1 && index == lastSelectedIndex) {
					for (int in = 0; in < getProfileElement()
							.getElementsByTagName("person").getLength(); in++) {

						System.out.println("Found "
								+ getProfileElement()
										.getElementsByTagName("person")
										.item(in).getTextContent());

						if (parent.items[index].toString().equals(
								getProfileElement()
										.getElementsByTagName("person")
										.item(in).getTextContent())) {

							Boolean currentState = Boolean
									.parseBoolean(getProfileElement()
											.getElementsByTagName("person")
											.item(in).getAttributes()
											.getNamedItem("absent")
											.getTextContent());

							Element person = (Element) getProfileElement()
									.getElementsByTagName("person").item(in);

							person.setAttribute("absent",
									String.valueOf(!currentState));

							if (currentState) {
								if (list.getSelectionForeground() == Color.BLACK) {
									list.setSelectionForeground(Color.RED);
									System.out.println("changed");
									System.out.println(parent.numCallable());
									parent.cantCallWithin = parent
											.numCallable() - 1;
									parent.recent = (String[]) (new String[parent
											.numCallable() - 1]);
								}

							} else if (list.getSelectionForeground() == Color.RED) {
								list.setSelectionForeground(Color.BLACK);
								System.out.println("changed");
								System.out.println(parent.numCallable());
								parent.cantCallWithin = parent.numCallable() - 1;
								parent.recent = (String[]) (new String[parent
										.numCallable() - 1]);
							}
						}
					}
				}
				lastSelectedIndex = list.getSelectedIndex();
			}
		});

		peoNorth.add(profile);
		peoNorth.add(renameProfile);
		peoNorth.add(makeProfile);
		peoNorth.add(deleteProfile);
		peoCenter.add(cProfile, BorderLayout.NORTH);
		peoSouthNorth.add(remove);
		peoSouthNorth.add(input);
		peoSouthNorth.add(add);
		peoSouth.add(peoSouthNorth, BorderLayout.NORTH);
		peoSouth.add(fromText, BorderLayout.SOUTH);
		peoCenter.add(listScroller);
		peoContent.add(peoSouth, BorderLayout.SOUTH);
		peoContent.add(peoCenter, BorderLayout.CENTER);
		peoContent.add(peoNorth, BorderLayout.NORTH);

		peopleUI.add(peoContent, BorderLayout.CENTER);

		profile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(parent.doc.getDocumentElement()
						.getElementsByTagName("profile").getLength());
				Object[] possibilities = (Object[]) new Object[parent.doc
						.getDocumentElement().getElementsByTagName("profile")
						.getLength()];

				for (int ni = 0; ni < parent.doc
						.getElementsByTagName("profile").getLength(); ni++) {
					possibilities[ni] = parent.doc.getDocumentElement()
							.getElementsByTagName("profile").item(ni)
							.getAttributes().getNamedItem("name")
							.getTextContent();
				}

				String s = (String) JOptionPane.showInputDialog(null,
						"Which profile would you like to use?",
						"Select Profile", JOptionPane.PLAIN_MESSAGE, null,
						possibilities, "ham");

				if ((s != null) && (s.length() > 0)) {
					parent.currentProfile = s;

					parent.clearItems();

					parent.switchProfile(s);

					list.setListData(parent.items);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);

					cProfile.setText("Current Profile: "
							+ parent.currentProfile);

					randName.setVisible(false);
					rank.setVisible(false);
					ans.setVisible(false);
					correct.setVisible(false);
					incorrect.setVisible(false);
					skip.setVisible(false);

				}
			}
		});

		renameProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(null,
						"Input the new name for this profile.",
						"Rename Profile", JOptionPane.QUESTION_MESSAGE);

				if (name != null && name != "") {
					boolean duplicate = false;
					for (int ni = 0; ni < parent.doc.getElementsByTagName(
							"profile").getLength(); ni++) {
						if (parent.doc.getElementsByTagName("profile").item(ni)
								.getAttributes().getNamedItem("name")
								.getTextContent() == name
								|| parent.doc.getElementsByTagName("profile")
										.item(ni).getAttributes()
										.getNamedItem("name").getTextContent()
										.equals(name)) {
							duplicate = true;
						}
					}
					if (!duplicate) {

						getProfileElement().setAttribute("name", name);

						parent.currentProfile = name;

						cProfile.setText("Current Profile: "
								+ parent.currentProfile);

						changeState("Current Profile: " + parent.currentProfile);

					}
				}
			}
		});

		makeProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = "0";
				boolean repeat = true;
				do {
					name = String.valueOf(Integer.parseInt(name) + 1);
					boolean found = false;
					for (int ni = 0; ni < parent.doc.getDocumentElement()
							.getElementsByTagName("profile").getLength(); ni++) {
						if (name.equals(parent.doc
								.getElementsByTagName("profile").item(ni)
								.getAttributes().getNamedItem("name")
								.getTextContent())
								|| name == parent.doc

								.getElementsByTagName("profile").item(ni)
										.getAttributes().getNamedItem("name")
										.getTextContent()) {
							found = true;
							break;
						}
					}
					if (found == false) {
						repeat = false;
					}
				} while (repeat == true);

				Element person = parent.doc.createElement("profile");
				person.setAttribute("name", name);
				parent.doc.getDocumentElement().appendChild(person);

				parent.clearItems();

				parent.switchProfile(name);

				list.setListData(parent.items);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);

				randName.setVisible(false);
				rank.setVisible(false);
				ans.setVisible(false);
				correct.setVisible(false);
				incorrect.setVisible(false);
				skip.setVisible(false);

				cProfile.setText("Current Profile: " + parent.currentProfile);
			}
		});

		deleteProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int oneBefore = 0;

				for (int ni = 0; ni < parent.doc
						.getElementsByTagName("profile").getLength(); ni++) {
					if (parent.doc.getDocumentElement()
							.getElementsByTagName("profile").item(ni) == getProfileElement()) {
						oneBefore = ni - 1;
					}
				}

				parent.doc.getDocumentElement()
						.removeChild(getProfileElement());

				parent.clearItems();

				if (oneBefore != -1) {

					parent.switchProfile(parent.doc
							.getElementsByTagName("profile").item(oneBefore)
							.getAttributes().getNamedItem("name")
							.getTextContent());

					list.setListData(parent.items);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);

					cProfile.setText("Current Profile: "
							+ parent.currentProfile);

				} else {
					list.setListData(parent.items);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);

					cProfile.setText("Current Profile: 0");
					changeState("Current Profile: 0");
				}
				randName.setVisible(false);
				rank.setVisible(false);
				ans.setVisible(false);
				correct.setVisible(false);
				incorrect.setVisible(false);
				skip.setVisible(false);
			}
		});

		input.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				input.setText("");
				list.setListData(parent.items);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
				add.setText("Add");
			}

			public void focusLost(FocusEvent arg0) {
				if (input.getText().equals("")) {
					input.setText("Input Name Here!");
					list.setListData(parent.items);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);
				}
			}
		});

		fromText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int returnVal = fc.showOpenDialog(GUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						FileInputStream fstream = new FileInputStream(file);
						DataInputStream in = new DataInputStream(fstream);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(in));
						String strLine;
						while ((strLine = br.readLine()) != null) {
							importName(strLine);
							System.out.println("Imported " + strLine);
						}
						br.close();
						in.close();
						fstream.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					System.out.println("Operation canceled by user.");
				}
				list.setListData(parent.items);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
			}
		});

		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int index = list.getSelectedIndex();
				if (index != -1) {
					if (!(parent.items[index] == null)) {
						System.out.println("Removing " + parent.items[index]);

						for (int in = 0; in < getProfileElement()
								.getElementsByTagName("person").getLength(); in++) {

							System.out.println("Found "
									+ getProfileElement()
											.getElementsByTagName("person")
											.item(in).getTextContent());

							if (parent.items[index].toString().equals(
									getProfileElement()
											.getElementsByTagName("person")
											.item(in).getTextContent())) {

								getProfileElement().removeChild(
										getProfileElement()
												.getElementsByTagName("person")
												.item(in));
								System.out.println("Removed "
										+ parent.items[index]);
								break;
							}
						}
						parent.items[index] = null;

						System.out.println("Saved");

						if (parent.items.length == 0) {
							remove.setEnabled(false);
						} else {
							if (index == parent.items.length) {
								index--;
							}
							remove.setEnabled(true);
							list.setSelectedIndex(0);
							list.ensureIndexIsVisible(0);
						}
						String[] temp = (String[]) (new String[parent.items.length - 1]);
						if (parent.currentItemPos > 0) {
							parent.currentItemPos -= 1;
						}
						int currentpos = 0;
						for (String ob : parent.items) {
							if (ob != null) {
								temp[currentpos] = ob;
								currentpos += 1;
							}
						}
						parent.items = temp;
						list.setListData(parent.items);
						list.requestFocusInWindow();
						System.out.println("Finished removal");

						input.setText("Input Name Here!");
						list.setListData(parent.items);
						list.setSelectedIndex(index - 1);
						list.ensureIndexIsVisible(0);

						if (parent.items.length <= parent.cantCallWithin) {
							parent.cantCallWithin = parent.items.length - 1;
							parent.recent = (String[]) (new String[parent.cantCallWithin]);
						}

					} else {
						list.setSelectedIndex(index - 1);
					}
				}
			}
		});

		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean found = false;
				for (int ni = 0; ni < getProfileElement().getElementsByTagName(
						"person").getLength(); ni++) {
					System.out.println(input.getText() + " is ");
					System.out.println(getProfileElement()
							.getElementsByTagName("person").item(ni)
							.getTextContent());
					if (input.getText().equals(
							getProfileElement().getElementsByTagName("person")
									.item(ni).getTextContent())) {
						found = true;
						break;
					}
				}
				if (input.getText().equals("Input Name Here!")) {
					found = true;
				}
				if (!found) {
					parent.addItem(input.getText());
					Element person = parent.doc.createElement("person");
					person.setTextContent(input.getText());
					person.setAttribute("yes", "0");
					person.setAttribute("no", "0");
					person.setAttribute("skips", "0");
					person.setAttribute("absent", "false");
					getProfileElement().appendChild(person);
				}

				input.setText("Input Name Here!");
				list.setListData(parent.items);
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);

			}
		});
	}

	public void changeState(String to) {
		state.setText(to);
	}

	public Element getProfileElement() {

		NodeList n = parent.doc.getDocumentElement().getElementsByTagName(
				"profile");

		for (int temp = 0; temp < n.getLength(); temp++) {

			if (parent.doc.getDocumentElement().getElementsByTagName("profile")
					.item(temp).getAttributes().getNamedItem("name")
					.getTextContent() == parent.currentProfile
					|| parent.doc.getElementsByTagName("profile").item(temp)
							.getAttributes().getNamedItem("name")
							.getTextContent().equals(parent.currentProfile)) {
				return (Element) parent.doc.getElementsByTagName("profile")
						.item(temp);
			}
		}
		return null;
	}

	public boolean importName(String name) {
		try {
			boolean duplicate = false;
			for (int n = 0; n < parent.doc.getElementsByTagName("person")
					.getLength(); n++) {
				String na = parent.doc.getElementsByTagName("person").item(n)
						.getTextContent();
				if (na.equals(name) || na == name) {
					duplicate = true;
				}
			}
			if (!duplicate) {
				Element ne = parent.doc.createElement("person");
				ne.setTextContent(String.valueOf(name));
				ne.setAttribute("yes", "0");
				ne.setAttribute("no", "0");
				ne.setAttribute("skips", "0");
				getProfileElement().appendChild(ne);
			}

			parent.addItem(name);
			return true;
		} catch (Exception e3) {
			return false;
		}
	}

	public void showList() {
		showingList = true;
		peopleUI.setVisible(true);
	}

	public void hideList() {
		showingList = false;
		peopleUI.setVisible(false);
		System.gc();
	}

	public BufferedImage BackgroundPanel(int width, int height) {
		try {
			BufferedImage br = BackgroundResize(
					width,
					height,
					new File(System.getProperty("user.dir") + "/background.jpg"));

			System.runFinalization();
			System.gc();
			return br;

		} catch (Exception e) {
		}

		return null;

	}

	class MyFilter extends FileFilter {
		public boolean accept(File file) {

			boolean result = (extentionEquals(file, "rtf") ? true
					: extentionEquals(file, "doc") ? true : extentionEquals(
							file, "txt") ? true
							: extentionEquals(file, "docx") ? true : false);

			return result;
		}

		public String getDescription() {
			return ".rtf, .txt, .doc, .docx";
		}

		public boolean extentionEquals(File f, String ext) {
			String exti = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				exti = s.substring(i + 1).toLowerCase();
			}
			if (exti != null) {

				return (exti.equals(ext));
			}
			if (f.isDirectory()) {
				return true;
			}
			return false;
		}
	}

	public BufferedImage BackgroundResize(int dimx, int dimy, File img)
			throws IOException {
		BufferedImage originalImage = ImageIO.read(img);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
				: originalImage.getType();
		BufferedImage resizedImage = new BufferedImage(dimx, dimy, type);
		Graphics g = resizedImage.getGraphics();
		g.drawImage(originalImage, 0, 0, dimx, dimy, this);
		g.dispose();
		originalImage.flush();
		return resizedImage;
	}

	class MyCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);

			for (int in = 0; in < getProfileElement().getElementsByTagName(
					"person").getLength(); in++) {

				if (parent.items[index].toString().equals(
						getProfileElement().getElementsByTagName("person")
								.item(in).getTextContent())) {
					Boolean currentState = Boolean
							.parseBoolean(getProfileElement()
									.getElementsByTagName("person").item(in)
									.getAttributes().getNamedItem("absent")
									.getTextContent());

					if (currentState) {
						c.setForeground(Color.RED);
						parent.cantCallWithin = parent.numCallable() - 1;
						parent.recent = (String[]) (new String[parent
								.numCallable() - 1]);
					} else {
						c.setForeground(Color.BLACK);
						parent.cantCallWithin = parent.numCallable() - 1;
						parent.recent = (String[]) (new String[parent
								.numCallable() - 1]);
					}
					list.repaint();
				}
			}
			return c;
		}
	}
}