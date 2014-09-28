package workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Link {
	protected double version = 1;
	protected String app = "Tally Path";
	protected String desc = app
			+ " by Mark Robinson\n"
			+ "This app allows you to track progress as people are randomly selected to answer your questions.\n"
			+ "Alpha Verson " + version + "\n";
	protected String currentProfile = "";
	protected int itemsInitial = 0;
	protected int currentItemPos = 0;
	protected int currentRecentPos = 0;
	protected int cantCallWithin = 3;
	protected boolean showRankChange = true;
	protected boolean resizeRequested = true;

	Random randomGenerator = new Random();

	String[] items = new String[itemsInitial];
	String[] recent = new String[cantCallWithin];

	ArrayList oldRanks = new ArrayList();
	Document doc = null;

	Timer timer = new Timer();
	ManipXML x = new ManipXML(this);
	GUI g = new GUI(this);

	public void startApp() {
		x.Do("create");

		TimerTask t = new TimerTask() {
			public void run() {

				x.Do("save");

				if (resizeRequested && g != null) {
					g.resizedimage2 = (Image) g.BackgroundPanel(g.getWidth(),
							g.getHeight());
					g.resizedimage3 = new ImageIcon(g.resizedimage2);
					g.content = new JLabel(g.resizedimage3);
					g.setContentPane(g.content);
					g.content.setLayout(new BorderLayout());
					g.content.add(g.right, BorderLayout.CENTER);
					g.content.add(g.east, BorderLayout.EAST);
					g.content.add(g.tabbedPane, BorderLayout.WEST);
					g.content.add(g.bottom, BorderLayout.SOUTH);
					g.validate();

					g.textScroll.validate();

					g.east.setPreferredSize(new Dimension(240, g.getHeight()));

					System.out.println(g.getWidth() + "x" + g.getHeight());

					resizeRequested = false;
				}
			}
		};

		timer.scheduleAtFixedRate(t, 0, 1500);

	}

	public void setOldRanks() {

		for (int p = 0; p < getProfileElement().getElementsByTagName("person")
				.getLength(); p++) {
			Integer[] result = getInfo(getProfileElement()
					.getElementsByTagName("person").item(p).getTextContent());
			oldRanks.add(result[0]);
		}
	}

	public String getComparionOfRanks() {
		String result = "";

		for (int p = 0; p < getProfileElement().getElementsByTagName("person")
				.getLength(); p++) {

			if (!Boolean.parseBoolean(getProfileElement()
					.getElementsByTagName("person").item(p).getAttributes()
					.getNamedItem("absent").getTextContent())) {
				Integer[] Newresult = getInfo(getProfileElement()
						.getElementsByTagName("person").item(p)
						.getTextContent());

				int oldRank = (Integer) oldRanks.get(p);
				int newRank = Newresult[0];

				result = result.concat(
						getProfileElement().getElementsByTagName("person")
								.item(p).getTextContent()).concat("'s rank");

				System.out.println(getProfileElement()
						.getElementsByTagName("person").item(p)
						.getTextContent());
				System.out.println("old rank: " + oldRank);
				System.out.println("new rank: " + newRank);

				if (oldRank == newRank) {
					result = result.concat(" is the same.");
				} else if (oldRank < newRank) {
					result = result.concat(" has increased by ")
							.concat(String.valueOf(newRank - oldRank))
							.concat(".");
				} else if (oldRank > newRank) {
					result = result.concat(" has decreased by ")
							.concat(String.valueOf(oldRank - newRank))
							.concat(".");
				}
				result = result.concat("\n");
			}
		}

		System.out.println("comparison of ranks: " + result);

		oldRanks.clear();

		return result;
	}

	public Integer[] getInfo(String name) {
		double yes = 0;
		double no = 0;
		double skips = 0;
		int rank = getProfileElement().getElementsByTagName("person")
				.getLength();

		for (int p = 0; p < getProfileElement().getElementsByTagName("person")
				.getLength(); p++) {
			if (getProfileElement().getElementsByTagName("person").item(p)
					.getTextContent() == name
					|| getProfileElement().getElementsByTagName("person")
							.item(p).getTextContent().equals(name)) {
				yes = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("yes").getTextContent());
				no = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("no").getTextContent());
				skips = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("skips").getTextContent());
				rank = p + 1;
			}
		}

		double score = yes / (no + skips == 0 ? 1 : (no + skips));
		System.out.println(name + " My score is " + score);
		rank = 1;

		for (int p = 0; p < getProfileElement().getElementsByTagName("person")
				.getLength(); p++) {
			if (getProfileElement().getElementsByTagName("person").item(p)
					.getTextContent() != name
					&& !getProfileElement().getElementsByTagName("person")
							.item(p).getTextContent().equals(name)) {
				double c = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("yes").getTextContent());
				double inC = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("no").getTextContent());
				double sk = Integer.valueOf(getProfileElement()
						.getElementsByTagName("person").item(p).getAttributes()
						.getNamedItem("skips").getTextContent());
				double sco = c / (inC + sk == 0 ? 1 : (inC + sk));

				if (sco > score) {
					rank++;
				} else {
					if (sco == score) {
						System.out.println("EQUAL SCORE");
						System.out.println(getProfileElement()
								.getElementsByTagName("person").item(p)
								.getTextContent()
								+ " has " + c + " yes and I have " + yes);
						if (c > yes) {
							System.out
									.println("I am increasing a rank based on number of correct answers.");
							rank++;
						} else if (c < yes) {

						} else if ((inC + sk == 0 ? 1 : (inC + sk)) < (no
								+ skips == 0 ? 1 : (no + skips))) {
							System.out
									.println("I am increasing a rank based on number of incorrect answers.");
							rank++;
						}
					}
				}

			}
		}
		Integer[] ret = { rank, (int) yes, (int) no, (int) skips, (int) score };
		return ret;
		/*
		 * else { rank = (rank == defaultRank ? defaultRank : 1); for (int p =
		 * 0; p < getProfileElement().getElementsByTagName(
		 * "person").getLength(); p++) { if
		 * (getProfileElement().getElementsByTagName("person").item(p)
		 * .getTextContent() != name &&
		 * !getProfileElement().getElementsByTagName("person")
		 * .item(p).getTextContent().equals(name)) { int c =
		 * Integer.valueOf(getProfileElement()
		 * .getElementsByTagName("person").item(p)
		 * .getAttributes().getNamedItem("yes") .getTextContent()); if (c > yes)
		 * { rank++; } } }
		 * 
		 * Integer[] ret = { rank, yes, no, skips }; return ret;
		 * 
		 * }
		 */
	}

	public void clearItems() {
		currentItemPos = 0;
		String[] temp = (String[]) (new String[itemsInitial]);
		items = temp;
	}

	public void syncItemsWithProfile() {
		if (getProfileElement() != null) {
			System.out.println("Persons in profile "
					+ getProfileElement().getElementsByTagName("person")
							.getLength());

			for (int n = 0; n < getProfileElement().getElementsByTagName(
					"person").getLength(); n++) {
				addItem(getProfileElement().getElementsByTagName("person")
						.item(n).getTextContent());

			}
		} else {
			System.out.println("Profile is empty");
		}
	}

	public void switchProfile(String name) {
		currentProfile = name;
		g.changeState("Current Profile: " + currentProfile);
		syncItemsWithProfile();
		if (numCallable() - 1 > 0) {
			cantCallWithin = numCallable() - 1;
			recent = (String[]) (new String[cantCallWithin]);
		}
	}

	public int numCallable() {
		int num = getProfileElement().getElementsByTagName("person")
				.getLength();
		for (int n = 0; n < getProfileElement().getElementsByTagName("person")
				.getLength(); n++) {
			if (Boolean.parseBoolean(getProfileElement()
					.getElementsByTagName("person").item(n).getAttributes()
					.getNamedItem("absent").getTextContent())) {
				num--;
			}
		}

		return num;
	}

	public boolean isAbsent(String name) {
		for (int n = 0; n < getProfileElement().getElementsByTagName("person")
				.getLength(); n++) {
			if (getProfileElement().getElementsByTagName("person").item(n)
					.getTextContent() == name
					|| getProfileElement().getElementsByTagName("person")
							.item(n).getTextContent().equals(name)) {
				if (Boolean.parseBoolean(getProfileElement()
						.getElementsByTagName("person").item(n).getAttributes()
						.getNamedItem("absent").getTextContent())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRecent(String name) {

		for (String itm : recent) {
			if (itm != null && name != null
					&& (itm.equals(name) || itm == name)) {

				return true;

			}
		}

		return false;
	}

	public void addRecent(String name) {
		try {
			if (currentRecentPos >= recent.length) {
				currentRecentPos = 0;
			}
			System.out.println("Saved recent[" + currentRecentPos + "] as "
					+ name);
			recent[currentRecentPos] = name;

			currentRecentPos += 1;

		} catch (NullPointerException e) {
		}
	}

	public void addItem(String num) {
		try {
			boolean duplicateFound = false;
			for (String itm : items) {
				if (itm != null && num != null
						&& (itm.equals(num) || itm == num)) {
					duplicateFound = true;
					System.out.println("Duplicate of " + itm + " found.");
				}
			}
			if (!duplicateFound) {
				if (currentItemPos >= items.length) {
					String[] temp = (String[]) (new String[items.length + 1]);
					System.arraycopy(items, 0, temp, 0, items.length);
					items = temp;
				}
				System.out.println("Saved items[" + currentItemPos + "] as "
						+ num);
				items[currentItemPos] = num;

				currentItemPos += 1;

				if (numCallable() - 1 > 0) {
					cantCallWithin = numCallable() - 1;
					recent = (String[]) (new String[cantCallWithin]);
				}

			}
		} catch (NullPointerException e) {
		}
	}

	public String selectName() {
		randomGenerator.setSeed(System.nanoTime());

		Collections.shuffle(Arrays.asList(items),
				new Random(System.currentTimeMillis()));
		if (numCallable() - 1 > 0) {
			int r = randomGenerator.nextInt(items.length - 1);

			System.out.println(r);

			System.out.println(items[r]);

			System.gc();

			if (isAbsent(items[r])) {
				return "absent";
			}
			return items[r];
		}
		return null;

	}

	public Element getProfileElement() {

		NodeList n = doc.getDocumentElement().getElementsByTagName("profile");

		for (int temp = 0; temp < n.getLength(); temp++) {
			if (doc.getDocumentElement().getElementsByTagName("profile")
					.item(temp).getAttributes().getNamedItem("name")
					.getTextContent() == currentProfile
					|| doc.getDocumentElement().getElementsByTagName("profile")
							.item(temp).getAttributes().getNamedItem("name")
							.getTextContent().equals(currentProfile)) {
				return (Element) doc.getDocumentElement()
						.getElementsByTagName("profile").item(temp);
			}
		}
		return null;
	}

	public static void main(String[] args) {
		new Link();
		System.runFinalization();
		System.gc();
	}
}
