package workspace;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ManipXML {
	private Link parent;
	
	public ManipXML(final Link parent){
		this.parent = parent;
	}

	public void Do(String w) {
		File fXmlFile = new File(System.getProperty("user.dir")
				+ "\\Tally_Data.xml");
		if (w == "load") {
			try {
				if (fXmlFile.exists()) {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
					parent.doc = doc;

					System.out.println(doc.getDocumentElement().getElementsByTagName("profile").getLength());
					if (doc.getDocumentElement().getElementsByTagName("profile").getLength() >= 1) {
						NodeList n = doc.getDocumentElement().getElementsByTagName("profile")
								.item(0).getChildNodes();
						for (int temp = 0; temp < n.getLength(); temp++) {
							if (doc.getElementsByTagName("profile").item(0)
									.getChildNodes().item(temp).getNodeType() == Node.ELEMENT_NODE) {
								String name = doc
										.getElementsByTagName("profile")
										.item(0).getChildNodes().item(temp)
										.getTextContent();
								parent.addItem(name);
								System.out.println("added "+name);
							}
						}
						System.out.println("Loaded profile: "
								+ doc.getElementsByTagName("profile").item(0)
										.getAttributes().getNamedItem("name")
										.getTextContent());

						parent.currentProfile = doc
								.getElementsByTagName("profile").item(0)
								.getAttributes().getNamedItem("name")
								.getTextContent();
						
						parent.cantCallWithin = parent.numCallable()-1;
						parent.recent = (String[]) (new String[(parent.numCallable()-1)]);
						
						System.out.println("Non-absent people = "+(parent.numCallable()-1));
					}
					this.Do("save");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (w == "create") {
			try {
				if (fXmlFile.exists()) {
					this.Do("load");
				} else {
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();

					Document doc = docBuilder.newDocument();
					Element rootElement = doc.createElement("Data");
					
					Element history = doc.createElement("History");
					
					rootElement.appendChild(history);
					
					Element profiles = doc.createElement("Profiles");
					
					rootElement.appendChild(profiles);
					
					doc.appendChild(rootElement);
					
					parent.doc = doc;
					OutputFormat format = new OutputFormat(this.parent.doc);
					format.setIndenting(true);

					XMLSerializer serializer;
					serializer = new XMLSerializer(new FileOutputStream(
							new File(System.getProperty("user.dir")
									+ "\\Tally_Data.xml")), format);
					serializer.serialize(parent.doc);

					docFactory = null;
					docBuilder = null;
					rootElement = null;
					doc = null;
					format = null;
					serializer = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (w == "save") {
			OutputFormat format = new OutputFormat(parent.doc);
			format.setIndenting(true);

			XMLSerializer serializer;
			try {
				serializer = new XMLSerializer(new FileOutputStream(new File(
						System.getProperty("user.dir") + "\\Tally_Data.xml")),
						format);
				serializer.serialize(parent.doc);
				serializer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			format = null;

			System.runFinalization();
			System.gc();
		}
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}