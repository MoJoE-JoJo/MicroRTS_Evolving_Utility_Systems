package AnjiIntegration;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class XMLToPlantUML {

    static String XMLFilePath;
    static StringBuilder stringBuilder;
    static Set<XMLNeuron> inputs = new HashSet<>();
    static Set<XMLNeuron> hidden = new HashSet<>();
    static Set<XMLNeuron> outputs = new HashSet<>();
    static Set<XMLConnection> connections = new HashSet<>();

    public static void main(String[] args) {
        int chromId = 12238;
        XMLFilePath = "./db/chromosome/chromosome" + chromId + ".xml";
        stringBuilder = new StringBuilder();
        stringBuilder.append(setupString());
        readXML(XMLFilePath);
        stringBuilder.append(inputs());
        stringBuilder.append(outputs());
        stringBuilder.append(hidden());
        stringBuilder.append(connections());

        File file = new File("./ANJI_PlantUML_Output.txt");
        BufferedWriter writer = null;

        //System.out.println(stringBuilder.toString());
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void readXML(String path){
        try {
            InputStream targetStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //System.out.println("Working Directory = " + System.getProperty("user.dir"));

        try (InputStream is = new FileInputStream(path)) {

            // parse XML file
            DocumentBuilder docbuilder = dbf.newDocumentBuilder();
            // read from a project's resources folder
            Document doc = docbuilder.parse(is);

            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            System.out.println("------");

            if (doc.hasChildNodes()) {
                NodeList nodeList = doc.getChildNodes();
                int nextActionIndex = 0;

                // first layer is a chromosome, so get its children which is all the neurons and connections
                if (nodeList.item(0).getNodeName() == "chromosome") {
                    nodeList = nodeList.item(0).getChildNodes();
                }

                for (int count = 0; count < nodeList.getLength(); count++) {

                    Node tempNode = nodeList.item(count);

                    // make sure it's an element node.
                    if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                        // get node name and value
                        String nodeName = tempNode.getNodeName();
                        System.out.println("\nNode Name = " + nodeName + " [OPEN]");

                        // switch on the node name
                        switch (nodeName) {
                            case "neuron" -> {
                                // handle neurons, need to handle based on input, hidden or output neuron
                                String neuronType = tempNode.getAttributes().getNamedItem("type").getNodeValue();
                                String neuronId = tempNode.getAttributes().getNamedItem("id").getNodeValue();
                                String neuronActivation = tempNode.getAttributes().getNamedItem("activation").getNodeValue();
                                switch (neuronType) {
                                    case "in" -> {
                                        inputs.add(new XMLNeuron(Integer.parseInt(neuronId), neuronType, neuronActivation));
                                    }
                                    case "out" -> {
                                        outputs.add(new XMLNeuron(Integer.parseInt(neuronId), neuronType, neuronActivation));
                                    }
                                    case "hid" -> {
                                        hidden.add(new XMLNeuron(Integer.parseInt(neuronId), neuronType, neuronActivation));
                                    }
                                }
                            }
                            case "connection" -> {
                                // Handle connections,
                                String srcId = tempNode.getAttributes().getNamedItem("src-id").getNodeValue();
                                String destId = tempNode.getAttributes().getNamedItem("dest-id").getNodeValue();
                                connections.add(new XMLConnection(Integer.parseInt(srcId), Integer.parseInt(destId)));
                            }
                        }
                    }
                }

            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String setupString(){
        return "@startuml\n" +
                "skinparam packageStyle rectangle\n"+
                "left to right direction\n" +
                "' Horizontal lines: -->, <--, <-->\n" +
                "' Vertical lines: ->, <-, <->\n" +
                "title Net Network \n";
    }

    private static String inputs(){
        StringBuilder isb = new StringBuilder();
        isb.append("package Inputs{\n");
        for (XMLNeuron neuron : inputs) {
            isb.append("object ");
            isb.append(neuron.getId());
            isb.append("{\n");
            isb.append(neuron.getActivation());
            isb.append("\n}\n");
        }
        isb.append("}\n");
        return isb.toString();
    }

    private static String outputs(){
        StringBuilder isb = new StringBuilder();
        isb.append("package Outputs{\n");
        for (XMLNeuron neuron : outputs) {
            isb.append("object ");
            isb.append(neuron.getId());
            isb.append("{\n");
            isb.append(neuron.getActivation());
            isb.append("\n}\n");
        }
        isb.append("}\n");
        return isb.toString();
    }

    private static String hidden(){
        StringBuilder isb = new StringBuilder();
        for (XMLNeuron neuron : hidden) {
            isb.append("object ");
            isb.append(neuron.getId());
            isb.append("{\n");
            isb.append(neuron.getActivation());
            isb.append("\n}\n");
        }
        return isb.toString();
    }

    private static String connections(){
        StringBuilder isb = new StringBuilder();
        for (XMLConnection connection: connections) {
            isb.append(connection.getSrcId());
            isb.append(" --> ");
            isb.append(connection.getDestId());
            isb.append("\n");
        }
        isb.append("\n");
        return isb.toString();
    }
}
