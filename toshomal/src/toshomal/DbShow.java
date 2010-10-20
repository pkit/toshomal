package toshomal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DbShow {

    private int id;
    private String name;
    private int malid;
    private String status;
    private String type;
    private int epsnum;
    private String image;
    private ArrayList<DbFile> files;
    private ArrayList<DbEps> eps;
    private ArrayList<DbTag> tags;
    private int maxCount;
    private int minCount;

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
            files = new ArrayList<DbFile>();
            eps = new ArrayList<DbEps>();
            tags = new ArrayList<DbTag>();
            this.maxCount = 0;
            this.minCount = 5;
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

    public boolean addFile(DbFile file)
    {
        return files.add(file);
    }

    public boolean addEps(DbEps ep)
    {
        return eps.add(ep);
    }

    public boolean addTag(DbTag tag)
    {
        int tcount = tag.getCount();
        if (tcount > maxCount)
            maxCount = tcount;
        //if (tcount < minCount)
        //    minCount = tcount;
        return tags.add(tag);
    }

    public void setFiles(ArrayList<DbFile> files)
    {
        this.files = files;
    }

    public ArrayList<DbTag> getTags()
    {
        return tags;
    }

    public ArrayList<DbEps> getEps()
    {
        return eps;
    }

    public String getFontSize(DbTag tag)
    {
        return String.format("font-size: %.2fem", 0.7 + Math.log(tag.getCount()) / Math.log(maxCount));
    }
}
