package toshomal;

import org.apache.derby.iapi.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbEps {

    private int id;
    private int showId;
    private int num;
    private int sub;

    public DbEps(ResultSet rs)
    {
        try {
            this.id = rs.getInt("id_eps");
            this.showId = rs.getInt("id_show");
            this.num = rs.getInt("num");
            this.sub = rs.getInt("sub");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        if (sub == 0)
            return String.format("%02d", num);
        return String.format("%02d.%d", num, sub);
    }

    public int getId()
    {
        return id;
    }
}
