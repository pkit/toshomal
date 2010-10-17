package toshomal;

import com.sun.syndication.feed.synd.SyndEntry;
import org.jdom.Element;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class EmbeddedDBConnector
{
    Connection conn;
    String driver;
    String dbName;
    String connectionURL;

    public EmbeddedDBConnector()
    {
        String userHomeDir = System.getProperty("user.home", ".");
        String systemDir = userHomeDir + "";
        System.setProperty("derby.system.home", systemDir);
        this.conn = null;
        this.driver = "org.apache.derby.jdbc.EmbeddedDriver";
        this.dbName = "maldb";
        this.connectionURL = "jdbc:derby:" + dbName;
    }

    private Connection getDBConnection()
    {
        Connection c = null;
        try {
            c = DriverManager.getConnection(this.connectionURL);
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return c;
    }

    private PreparedStatement getStatement(String query) {
        try {
            if (this.conn != null)
            {
                System.out.println("DB Connection already exists");
                return null;
            }
            this.conn = getDBConnection();
            while (this.conn == null)
            {
                sleep(1000);
                this.conn = getDBConnection();
                System.out.println("DB connect failed, retrying...  DB: " + this.connectionURL);
            }
            return conn.prepareStatement(query);
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
        return null;
    }

    private void closeStatement(PreparedStatement stmt)
    {
        if (stmt == null)
            return;
        try {
            stmt.close();
            this.conn.close();
            this.conn = null;
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private int getShowId(String showTitle)
    {
        String query = "select id_show from show where name = ?";
        PreparedStatement stmt = this.getStatement(query);
        int result = -1;
        try {
            stmt.setString(1, showTitle);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                result = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return result;
    }

    private int getEpsId(int showId, int epsNum, int epsSub)
    {
        String query = "select id_eps from eps where id_show = ? and num = ? and sub = ?";
        PreparedStatement stmt = this.getStatement(query);
        int result = -1;
        try {
            stmt.setInt(1, showId);
            stmt.setInt(2, epsNum);
            stmt.setInt(3, epsSub);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                result = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return result;
    }

    private int getFileId(String url)
    {
        String query = "select id_file from file where url = ?";
        PreparedStatement stmt = this.getStatement(query);
        int result = -1;
        try {
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                result = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return result;
    }

    private int insertNewShow(String title, String malId, String epsnum, String type, String status, String imgurl)
    {
        String insQry = "insert into show (name, malid, eps_number, type_show, status, image) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setString(1, title);
            stmt.setInt(2, Integer.parseInt(malId));
            stmt.setInt(3, Integer.parseInt(epsnum));
            stmt.setString(4, type);
            stmt.setString(5, status);
            stmt.setString(6, imgurl);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return getShowId(title);
    }

    private int insertNewEps(int showId, int eps, int sub)
    {
        String insQry = "insert into eps (id_show, num, sub) values (?, ?, ?)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setInt(1, showId);
            stmt.setInt(2, eps);
            stmt.setInt(3, sub);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return getEpsId(showId, eps, sub);
    }

    private int insertNewFile(String name, String url, String ext, String md5, String ver, String size)
    {
        String insQry = "insert into file (time, name, url, ext, md5, ver, size) values (current_timestamp, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setString(1, name);
            stmt.setString(2, url);
            stmt.setString(3, ext);
            stmt.setString(4, md5);
            stmt.setInt(5, Integer.parseInt(ver));
            stmt.setString(6, size);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return getFileId(url);
    }

    private int getTagId(String tag)
    {
        String query = "select id_tag from tag where tag.name = ?";
        PreparedStatement stmt = this.getStatement(query);
        int result = -1;
        try {
            stmt.setString(1, tag);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                result = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return result;
    }

    private int insertNewTag(String name)
    {
        String insQry = "insert into tag (name, type_tag) values (?, 0)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return getTagId(name);
    }

    private void updateFileTags(int fileId, String tag)
    {
        int tagId = getTagId(tag);
        if (tagId == -1)
        {
            tagId = insertNewTag(tag);
        }
        String insQry = "insert into tagsperfile values (?, ?)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setInt(1, tagId);
            stmt.setInt(2, fileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
    }

    private void updateFileEps(int fileId, int epsId)
    {
        String insQry = "insert into epsperfile values (?, ?)";
        PreparedStatement stmt = this.getStatement(insQry);
        try {
            stmt.setInt(1, epsId);
            stmt.setInt(2, fileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
    }

    public void UpdateEntry(Element entry, SyndEntry t_entry, ParsedFileName fname) {
        try {
            Matcher m_thumb = Pattern.compile("\\.jpg$").matcher(entry.getChild("image").getTextTrim());
            String image = m_thumb.replaceFirst("t.jpg");
            int show_id = getShowId(entry.getChild("title").getTextTrim());
            if (show_id == -1)
            {
                show_id = insertNewShow(entry.getChild("title").getTextTrim(),
                        entry.getChild("id").getTextTrim(),
                        entry.getChild("episodes").getTextTrim(),
                        entry.getChild("type").getTextTrim(),
                        entry.getChild("status").getTextTrim(),
                        image);
            }
            int file_id = getFileId(t_entry.getLink());
            if (file_id == -1)
            {
                Matcher m_size = Pattern.compile("Size: ([^<]+)").matcher(t_entry.getDescription().getValue());
                String size = "0MB";
                if (m_size.find())
                    size = m_size.group(1);
                file_id = insertNewFile(fname.getName(), t_entry.getLink(), fname.getExt(), fname.getMD5(), fname.getVer(), size);
                List<String> taglist = fname.getTags();
                for (String aTaglist : taglist) {
                    updateFileTags(file_id, aTaglist);
                }
            }
            Matcher m_batch = Pattern.compile("(\\d+)-(\\d+)").matcher(fname.getEps());
            if(m_batch.find())
            {
                int start = Integer.parseInt(m_batch.group(1));
                int end = Integer.parseInt(m_batch.group(2));
                int sub = Integer.parseInt(fname.getSub());

                for (int i = start; i <= end; ++i)
                {
                    int eps_id = getEpsId(show_id, i, sub);
                    if (eps_id == -1)
                    {
                        eps_id = insertNewEps(show_id, i, sub);
                    }
                    updateFileEps(file_id, eps_id);
                }
            }
            else
            {
                int eps_id = getEpsId(show_id, Integer.parseInt(fname.getEps()), Integer.parseInt(fname.getSub()));
                if (eps_id == -1)
                {
                    eps_id = insertNewEps(show_id, Integer.parseInt(fname.getEps()), Integer.parseInt(fname.getSub()));
                }
                updateFileEps(file_id, eps_id);
            }
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String testSelect()
    {
        String query = "select * from test_table;";
        PreparedStatement stmt = this.getStatement(query);
        String result = "";
        try {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                result += rs.getString(1) + "\n";
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return result;
    }

    public boolean testInsert(String val)
    {
        String insQry = "insert into test_table values (?)";
        PreparedStatement stmt = this.getStatement(insQry);
        boolean res = false;
        try {
            stmt.setString(1, val);
            stmt.executeUpdate();
            res = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.closeStatement(stmt);
        return res;
    }

    public ArrayList<DbFile> getFileList()
    {
        String query = "select * from file";
        ArrayList<DbFile> result = new ArrayList<DbFile>();
        PreparedStatement stmt = this.getStatement(query);
        try {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                result.add(new DbFile(rs));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeStatement(stmt);
            return null;
        }
        this.closeStatement(stmt);
        return result;
    }

    public ArrayList<DbFile> getFileList(Timestamp updateTime)
    {
        String query = "select * from file where time > ?";
        ArrayList<DbFile> result = new ArrayList<DbFile>();
        PreparedStatement stmt = this.getStatement(query);
        try {
            stmt.setTimestamp(1, updateTime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                result.add(new DbFile(rs));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeStatement(stmt);
            return null;
        }
        this.closeStatement(stmt);
        return result;
    }

    public DbShow getShow(DbFile file) {
        String query = "select distinct * from show, eps, epsperfile where show.id_show = eps.id_show and eps.id_eps = epsperfile.id_eps and epsperfile.id_file = ?";
        PreparedStatement stmt = this.getStatement(query);
        DbShow result = null;
        try {
            stmt.setInt(1, file.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                result = new DbShow(rs);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeStatement(stmt);
            return null;
        }
        this.closeStatement(stmt);
        return result;
    }
}

