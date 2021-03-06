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

    public anjiConverter()
    {

    }

    public UtilitySystem toUtilitySystemFromXMLString(String rawXML) {
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

    public UtilitySystem toUtilitySystemFromInputStream(InputStream stream) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        UtilitySystem returnSystem = null;
        try {

            // parse XML file
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            // read from a project's resources folder
            Document doc = docBuilder.parse(stream);

            if (doc.hasChildNodes()) {
                //printNote(doc.getChildNodes());
                returnSystem = buildUtilitySystemFromNodeList(doc.getChildNodes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSystem;
    }


    public UtilitySystem toUtilitySystemFromChromosome(long chromId) throws Exception {
        ChromosomeToNetworkXml converter = new ChromosomeToNetworkXml();

        // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/

        // Instantiate the Factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        UtilitySystem returnSystem = null;
        try (InputStream is = readXmlFileIntoInputStream("./db/chromosome/chromosome" + chromId + ".xml")) {

            // parse XML file
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            // read from a project's resources folder
            Document doc = docBuilder.parse(is);

            if (doc.hasChildNodes()) {
                //printNote(doc.getChildNodes());
                returnSystem = buildUtilitySystemFromNodeList(doc.getChildNodes());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return returnSystem;
    }

    private UtilitySystem buildUtilitySystemFromNodeList(NodeList nodeList) {
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
        List<USConstant> constantsList = new LinkedList<>();


        // first layer is a chromosome, so get its children which is all the neurons and connections
        if (nodeList.item(0).getNodeName().equals("chromosome")) {
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
                                // convert neuron ID to a GameStateVariable.
                                // The first Neurons in the chromosome are always the ids 1-X where X is the amount of input neurons
                                int neuronIdInt = Integer.parseInt(neuronId);
                                // The neuron with id 0 we use for creating constants, so it is skipped here
                                if (neuronIdInt == 0) {
                                    break;
                                }

                                USVariable.GameStateVariable gameStateVariable = USVariable.GameStateVariable.values()[neuronIdInt - 1];
                                var usVariable = new USVariable(neuronId + "_" + gameStateVariable, gameStateVariable);
                                nodeMap.put(neuronId, usVariable);
                                varibleList.add(usVariable);
                                break;
                            case "out":
                                // outputs neurons -> utility Action node
                                // creates a new action node.

                                // create a "final feature layer" node.
                                //USFeature finalFeature = new USFeature(neuronId, USFeature.Operation.SUM);

                                // choose the next action in line
                                USAction.UtilAction actionEnum = USAction.UtilAction.values()[nextActionIndex];
                                // if statement to prevent problems
                                if (nextActionIndex <= USAction.UtilAction.values().length - 2) {
                                    nextActionIndex++;
                                }

                                USAction actionNode = new USAction(actionEnum.toString(), null, actionEnum);

                                nodeMap.put(neuronId, actionNode);
                                actionList.add(actionNode);
                                break;
                            case "hid":
                                // hidden neurons -> utility system features nodes
                                // read the operation from the XMLnode
                                String operation = tempXMLNode.getAttributes().getNamedItem("activation").getNodeValue();
                                //create new feaure and add to node map and feature list
                                USFeature feature = new USFeature(neuronId, USFeature.Operation.valueOf(operation));
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
                        String connId = tempXMLNode.getAttributes().getNamedItem("id").getNodeValue();
                        String srcId = tempXMLNode.getAttributes().getNamedItem("src-id").getNodeValue();
                        String destId = tempXMLNode.getAttributes().getNamedItem("dest-id").getNodeValue();

                        // each time there is a connection that has id: 0 as src create a new constant node
                        // The value is based on the weight of the connection
                        if (srcId.equals("0")) {
                            float conVal = Float.parseFloat(tempXMLNode.getAttributes().getNamedItem("weight").getNodeValue());
                            USConstant constant = new USConstant(connId, conVal);

                            constantsList.add(constant); // add node to list for building
                            nodeMap.put(connId, constant); // add node to map to look up

                            //put in the connection map, but under the connection id instead of neuron id.

                            List newList = new LinkedList();
                            newList.add(destId);
                            connectionMap.put(connId, newList);
                            break;
                        }


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

            if (srcNode == null) {
                System.out.println("Stop");
            }

            for (var destNodeId : destList) {
                //System.out.println("Connection: " + src + " --> " + destNodeId);

                // look up destNode in map
                USNode destNode = nodeMap.get(destNodeId);

                if (!allowedToMakeConnection(srcNode, destNode)) {
                    continue;
                }
                // switch on dest node type, this is for updating all the object relations.
                // The utility system is based on a "top down" approach, it builds relations such that the destNode has a ref to srcNode
                switch (destNode.getType()) {
                    case US_CONSTANT:
                    case US_VARIABLE:

                        // only allow a connection with variable as dest, if its to a action
                        if (srcNode.getType() == USNode.NodeType.US_ACTION) {
                            USAction tmpFeature = (USAction) srcNode;
                            tmpFeature.addParam(destNode);
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
                        tmpAction.addParam(srcNode);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + srcNode.getType());
                }
            }
        }

        UtilitySystem US = new UtilitySystem(varibleList, featureList, actionList, constantsList);
        return US;
    }

    private boolean allowedToMakeConnection(USNode srcNode, USNode destNode) {
        if (srcNode.getType() == USNode.NodeType.US_ACTION && destNode.getType() == USNode.NodeType.US_ACTION) {
            return false;
        }

        if (srcNode.getType() == USNode.NodeType.US_VARIABLE && destNode.getType() == USNode.NodeType.US_VARIABLE) {
            return false;
        }

        // check for circular connection.
        if (srcNode.getName().equals(destNode.getName())) {
            return false;
        }
        return true;
    }

    private void printNote(NodeList nodeList) {

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

    public InputStream readXmlFileIntoInputStream(final String fileName) throws FileNotFoundException {
        InputStream targetStream = new FileInputStream(fileName);
        return targetStream;
    }

    public String toXMLStringFromUtilitySystem(UtilitySystem utilitySystem) {

        // constant id  = 0
        // variable id  = 1 + variable enum index
        // action id    = 1 + variable enum.length + action enum index
        // feature id   = 1 + variable enum.length + action enum.length + featurelist index
        // connection id= continue from last feature id.
        int neuronId = 0;
        int constantLength = 1;
        int variableLength = USVariable.GameStateVariable.values().length;
        int actionLength = USAction.UtilAction.values().length;

        Map<USNode, Integer> neuronIDMap = new HashMap<>();

        StringBuilder builder = new StringBuilder();

        builder.append("<chromosome id=\"" + 1 + "\">\n");

        // <neuron id="0" type="in" activation="sigmoid"/>


        // create in node "0" which is the constant node
        builder.append("<neuron id=\"" + neuronId + "\" type=\"in\" activation=\"sigmoid\"/>\n");
        neuronId++;

        // then for each variable node -> <neuron id="0" type="in" activation="sigmoid"/>
        for (USVariable variable : utilitySystem.getVariables()) {
            neuronId = constantLength + variable.getGameStateVariable().ordinal();
            builder.append("<neuron id=\"" + neuronId + "\" type=\"in\" activation=\"sigmoid\"/>\n");
            neuronIDMap.put(variable, neuronId);
        }

        // then for each action node -> <neuron id="0" type="out" activation="sigmoid"/>
        for (USAction action : utilitySystem.getActions()) {
            neuronId = constantLength + variableLength + action.getAction().ordinal();
            builder.append("<neuron id=\"" + neuronId + "\" type=\"out\" activation=\"sigmoid\"/>\n");
            neuronIDMap.put(action, neuronId);
        }

        // then for each feature node -> <neuron id="0" type="hid" activation="feature.operation"/>
        neuronId = constantLength + variableLength + actionLength;
        for (USFeature feature : utilitySystem.getFeatures()) {
            builder.append("<neuron id=\"" + neuronId + "\" type=\"hid\" activation=\"" + feature.getOperation() + "\"/>\n");
            neuronIDMap.put(feature, neuronId);
            neuronId++;
        }

        // then we need to find all connections: starts with actions
        for (USAction action : utilitySystem.getActions()) {
            for (USNode param : action.getParams()) {
                if (param.getType() == USNode.NodeType.US_CONSTANT) { // if a constant src is always 0 and the constant is stored in the weight
                    builder.append("<connection id=\"" + neuronId + "\" src-id=\"" + 0 + "\" dest-id=\"" + neuronIDMap.get(action) + "\" weight=\"" + ((USConstant) param).getConstant() + "\"/>\n");
                } else {
                    builder.append("<connection id=\"" + neuronId + "\" src-id=\"" + neuronIDMap.get(param) + "\" dest-id=\"" + neuronIDMap.get(action) + "\" weight=\"1\"/>\n");
                }
                neuronId++;
            }
        }

        for (USFeature feature : utilitySystem.getFeatures()) {
            for (USNode param : feature.getParams()) {
                if (param.getType() == USNode.NodeType.US_CONSTANT) { // if a constant src is always 0 and the constant is stored in the weight
                    builder.append("<connection id=\"" + neuronId + "\" src-id=\"" + 0 + "\" dest-id=\"" + neuronIDMap.get(feature) + "\" weight=\"" + ((USConstant) param).getConstant() + "\"/>\n");
                } else {
                    builder.append("<connection id=\"" + neuronId + "\" src-id=\"" + neuronIDMap.get(param) + "\" dest-id=\"" + neuronIDMap.get(feature) + "\" weight=\"1\"/>\n");
                }
                neuronId++;
            }
        }

        builder.append("</chromosome>"); // end tag
        return builder.toString();
    }
}
