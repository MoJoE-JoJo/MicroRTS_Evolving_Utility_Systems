package AnjiIntegration;

public class XMLNeuron {
    private int id = 0;
    private String type = "";
    private String activation = "";

    public XMLNeuron(int id, String type, String activation){
        this.id = id;
        this.type = type;
        this.activation = activation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getActivation() {
        return activation;
    }
}
