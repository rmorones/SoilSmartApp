package soilsmart.soilsmartapp;

import java.io.Serializable;
import java.util.List;

/**
 * SoilSmartApp
 * Created by Ricardo Morones on 2/27/16.
 */
public class User implements Serializable {
    private String email;
    private String passwordHash;
    private List<SoilSmartNode> nodes;

    User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nodes = null;
    }

    User(String email, String passwordHash, List<SoilSmartNode> nodes) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nodes = nodes;
    }

    public List<SoilSmartNode> getNodes() {
        return nodes;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
