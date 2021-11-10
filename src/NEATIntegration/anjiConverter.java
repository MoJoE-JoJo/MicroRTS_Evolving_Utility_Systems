package NEATIntegration;

import ai.utilitySystem.USAction;
import ai.utilitySystem.USFeature;
import ai.utilitySystem.USVariable;
import ai.utilitySystem.UtilitySystem;
import com.anji.integration.ChromosomeToNetworkXml;
import org.jgap.Chromosome;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class anjiConverter {

    // TODO need a method that can go from a chromosome to a utility system.

    public static UtilitySystem toUtilitySytemFromChromosome(long chromId) throws Exception {
        ChromosomeToNetworkXml converter = new ChromosomeToNetworkXml();

        // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        UtilitySystem returnSystem = null;
        try (InputStream is = readXmlFileIntoInputStream("./db/chromosome/chromosome" + chromId + ".xml")) {

            // parse XML file
            DocumentBuilder docbuilder = dbf.newDocumentBuilder();
            // read from a project's resources folder
            Document doc = docbuilder.parse(is);

            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            System.out.println("------");



            if (doc.hasChildNodes()) {
                printNote(doc.getChildNodes());



                LinkedList<USVariable> variables = new LinkedList<USVariable>();
                LinkedList<USFeature> features = new LinkedList<USFeature>();
                LinkedList<USAction> actions = new LinkedList<USAction>();


                NodeList nodeList = doc.getChildNodes();
                for (int count = 0; count < nodeList.getLength(); count++) {
                    Node current = nodeList.item(count);

                        switch (current.getNodeName()) {
                            case "neuron":
                                // create new input and output nodes
                                break;
                            case "connection":
                                //

                                break;
                            default:
                                break;

                }


            } else {
                throw new Exception("loaded in chromosome without child xml tags");
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }


        return returnSystem;
    }

    private static void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                System.out.println("\nNode Name = " + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value = " + tempNode.getTextContent());

                if (tempNode.hasAttributes()) {

                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());
                    }

                }

                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());
                }

                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

            }
        }
    }

    private static InputStream readXmlFileIntoInputStream(final String fileName) throws FileNotFoundException {
        InputStream targetStream = new FileInputStream(fileName);
        return targetStream;
    }
}
