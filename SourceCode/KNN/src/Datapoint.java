import java.util.*;

public class Datapoint {
    private List<Double> attributes = new ArrayList<>();
    private boolean isFraud;

    public Datapoint(List<Double> a, boolean f){
        this.attributes = a;
        this.isFraud = f;
    }

    public Datapoint(boolean f, Double... d){
        this.isFraud = f;
        for (Double double1 : d) {
            attributes.add(double1);
        }
    }

    @Override
    public String toString() {
        String print = "[";
        for (int i = 0; i < attributes.size(); i++) {
            print += attributes.get(i);
            print += ", ";
            
        }
        print += "isFraud=" + isFraud + "]";
        return print;
    }

    public boolean isFraud() {
        return isFraud;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Datapoint other = (Datapoint) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        return true;
    }

    public List<Double> getAttributes() {
        return attributes;
    }

}
