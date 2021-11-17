package AnjiIntegration;

import ai.utilitySystem.*;
import com.anji.integration.ChromosomeToNetworkXml;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;


public class anjiConverter {

    public static UtilitySystem toUtilitySystemFromXMLString(String rawXML) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        UtilitySystem returnSystem = null;
        try {
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            Document doc = docBuilder.parse(new InputSource(new StringReader(rawXML)));

            if (doc.hasChildNodes()) {
                returnSystem = buildUtilitySystemFromNodeList(doc.getChildNodes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSystem;
    }


    public static UtilitySystem toUtilitySystemFromChromosome(long chromId) throws Exception {
        ChromosomeToNetworkXml converter = new ChromosomeToNetworkXml();

        // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        UtilitySystem returnSystem = null;
        try (InputStream is = readXmlFileIntoInputStream("./db/chromosome/chromosome" + chromId + ".xml")) {

            // parse XML file
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            // read from a project's resources folder
            Document doc = docBuilder.parse(is);

            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            System.out.println("------");

            if (doc.hasChildNodes()) {
                printNote(doc.getChildNodes());
                returnSystem = buildUtilitySystemFromNodeList(doc.getChildNodes());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return returnSystem;
    }

    private static UtilitySystem buildUtilitySystemFromNodeList(NodeList nodeList) {
        // Structures and values used
        Map<String, USNode> nodeMap = new HashMap<>();
        // Map where the key is the src and the value the destination
        Map<String, List<String>> connectionMap = new HashMap<>();
        // list of output nodes from the ANJI network
        List<String> finalFeaturesNodes = new LinkedList<>();
        // action index counter
        int nextActionIndex = 0;

        // lists used for the final building of the utility system
        List<USAction> actionList = new LinkedList<>();
        List<USFeature> featureList = new LinkedList<>();
        List<USVariable> varibleList = new LinkedList<>();


        // first layer is a chromosome, so get its children which is all the neurons and connections
        if (nodeList.item(0).getNodeName() == "chromosome") {
            nodeList = nodeList.item(0).getChildNodes();
        }

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempXMLNode = nodeList.item(count);

            // make sure it's an element node.
            if (tempXMLNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                String nodeName = tempXMLNode.getNodeName();
                //System.out.println("\nNode Name = " + nodeName + " [OPEN]");

                // switch on the node name
                switch (nodeName) {
                    case "neuron":
                        // handle neurons, need to handle based on input, hidden or output neuron
                        String neuronType = tempXMLNode.getAttributes().getNamedItem("type").getNodeValue();
                        String neuronId = tempXMLNode.getAttributes().getNamedItem("id").getNodeValue();
                        switch (neuronType) {
                            case "in":
                                // inputs neurons -> utility system variable nodes
                                // convert neuron ID to a GameStateVariable. The first Neurons in the chromosome are always the ids 0-X where X is the amount of input neurons
                                USVariable.GameStateVariable gameStateVariable = USVariable.GameStateVariable.values()[Integer.parseInt(neuronId)];
                                var usVariable = new USVariable(neuronId, gameStateVariable);
                                nodeMap.put(neuronId, usVariable);
                                varibleList.add(usVariable);
                                break;
                            case "out":
                                // outputs neurons -> utility system "final features layer" nodes
                                // creates a new feature node, and a action node.
                                // The connection going to a "out" node will then go to this feature node that can handle multiple connections

                                // create a "final feature layer" node.
                                USFeature finalFeature = new USFeature(neuronId, USFeature.Operation.SUM, null, null);


                                USAction.UtilAction actionEnum = USAction.UtilAction.values()[nextActionIndex];
                                if (nextActionIndex <= USAction.UtilAction.values().length - 2) {
                                    nextActionIndex++;
                                }

                                USAction actionNode = new USAction(actionEnum.toString(), finalFeature, actionEnum);

                                nodeMap.put(neuronId, finalFeature);
                                featureList.add(finalFeature);
                                actionList.add(actionNode);
                                break;
                            case "hid":
                                // hidden neurons -> utility system features nodes
                                // read the operation from the XMLnode
                                String operation = tempXMLNode.getAttributes().getNamedItem("activation").getNodeValue();
                                //create new feaure and add to node map and feature list
                                USFeature feature = new USFeature(neuronId, USFeature.Operation.valueOf(operation), null, null);
                                nodeMap.put(neuronId, feature);
                                featureList.add(feature);
                                break;
                            default:
                                // ignore
                                break;
                        }
                        break;
                    case "connection":
                        // Handle connections,
                        String srcId = tempXMLNode.getAttributes().getNamedItem("src-id").getNodeValue();
                        String destId = tempXMLNode.getAttributes().getNamedItem("dest-id").getNodeValue();

                        //see if there already exist a list.
                        if (connectionMap.containsKey(srcId)) {
                            List tmpList = connectionMap.get(srcId);
                            tmpList.add(destId);
                        } else // if key don't exist, put a new list in the map
                        {
                            List newList = new LinkedList();
                            newList.add(destId);
                            connectionMap.put(srcId, newList);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // use the connections map to assign nodes

        for (var entry : connectionMap.entrySet()) {
            var src = entry.getKey();
            var destList = entry.getValue();
            USNode srcNode = nodeMap.get(src); // lookup src node
            for (var destNodeId : destList) {
                //System.out.println("Connection: " + src + " --> " + destNodeId);

                // look up destNode in map
                USNode destNode = nodeMap.get(destNodeId);

                if (!allowedToMakeConnection(srcNode, destNode)) {
                    continue;
                }

                // switch on src node type, this is for updating all the object relations.
                // The utility system is based on a "top down" approach, it builds relations such that the destNode has a ref to srcNode
                switch (destNode.getType()) {
                    case US_CONSTANT:
                    case US_VARIABLE:

                        // only allow a connection with variable as dest, if its to a action
                        if (srcNode.getType() == USNode.NodeType.US_ACTION) {
                            USAction tmpFeature = (USAction) srcNode;
                            tmpFeature.addFeature(destNode);
                            System.out.println("Made a connection from a Variable to a Action");

                        } else {
                            System.out.println("VARIABLE or CONSTANT Node should never be the dest of a connection");
                        }

                        break;
                    case US_FEATURE:
                        USFeature tmpFeature = (USFeature) destNode;
                        tmpFeature.addParam(srcNode);
                        break;
                    case US_ACTION:
                        USAction tmpAction = (USAction) destNode;
                        tmpAction.addFeature(srcNode);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + srcNode.getType());
                }
            }
        }

        // == Handle empty values in feature nodes ==

        for (USFeature node : featureList) {
            if (node.getParam2() == null) {
                //todo not sure this is good enough
                node.addParam(new USConstant(1.0f));
            }
        }

        UtilitySystem US = new UtilitySystem(varibleList, featureList, actionList);
        return US;
    }

    private static boolean allowedToMakeConnection(USNode srcNode, USNode destNode) {
        if (srcNode.getType() == USNode.NodeType.US_ACTION && destNode.getType() == USNode.NodeType.US_ACTION) {
            return false;
        }

        if (srcNode.getType() == USNode.NodeType.US_VARIABLE && destNode.getType() == USNode.NodeType.US_VARIABLE) {
            return false;
        }

        // check for circular connection.
        if (srcNode.getName() == destNode.getName()) {
            return false;
        }

        //TODO Maybe more cases


        return true;
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
