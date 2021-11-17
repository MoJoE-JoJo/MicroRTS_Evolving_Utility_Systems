package AnjiIntegration;

public class XMLConnection {
    private int srcId = 0;
    private int destId = 0;

    public XMLConnection(int src, int dest){
        srcId = src;
        destId = dest;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public int getSrcId() {
        return srcId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public int getDestId() {
        return destId;
    }
}
