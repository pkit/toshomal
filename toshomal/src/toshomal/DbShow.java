package toshomal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbShow {

    private int id;
    private String name;
    private int malid;
    private String status;
    private String type;
    private int epsnum;
    private String image;

    public DbShow(ResultSet rs)
    {
        try {
            this.id = rs.getInt("id_show");
            this.name = rs.getString("name");
            this.malid = rs.getInt("malid");
            this.status = rs.getString("status");
            this.type = rs.getString("type_show");
            this.epsnum = rs.getInt("eps_number");
            this.image = rs.getString("image");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMalUrl() {
        return String.format("http://myanimelist.net/anime/%d/", malid);
    }

    public String getImgUrl() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getId()
    {
        return id;
    }
}
