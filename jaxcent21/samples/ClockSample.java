package sample;

import jaxcent.*;
import toshomal.DbFile;
import toshomal.DbShow;
import toshomalBackend.ToshomalMessage;
import toshomal.EmbeddedDBConnector;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

/**
 * Jaxcent sample.
 *
 * Simply displays the server time, continuously updating the time once a second.
 * Uses a thread that starts on onLoad, and is terminated during onUnload.
 *
 * The HTML page has a P tag identified as "clock".  The inner HTML of this
 * tag is updated by the thread.
 */

public class ClockSample extends jaxcent.JaxcentPage {

    //HtmlPara clockPara = new HtmlPara( this, "clock" );    // Reference to the P tag with id "clock"

    HtmlTable table;

    Thread clockThread = null;
    boolean update = false;
    ToshomalMessage beMsg;
    ToshomalMessage feMsg;
    EmbeddedDBConnector db;
    Timestamp lastFileUpdate;
    ArrayList<DbFile> dbFiles;

    static String[][] tdArgs = {
            { "class", "valign", "width" , "id"},
            { "style", "class", "valign", "id" }
    };
    String[][] tdVals = {
            { "borderClass", "top", "53", "simg" },
            { "padding-left: 0pt;", "borderClass", "top", "sdesc" }
    };

    // Start the thread in the page constructor.

    public ClockSample() {
        table = new HtmlTable(this, "toshomaltable");
        System.out.println("table: " + table.toString());
        update = true;
        lastFileUpdate = new Timestamp(0);
        //this.beMsg = beMessage;
        //this.feMsg = feMessage;
        clockThread = new Thread() {
            public void run() {
                updateClock();
            }
        };
        db = new EmbeddedDBConnector();
        dbFiles = db.getFileList();
        for(DbFile file : dbFiles)
        {
            if (file.getUpdateTime().after(lastFileUpdate))
                lastFileUpdate = file.getUpdateTime();
            DbShow show = db.getShow(file);
            String image = buildImg(show);
            String desc = buildDesc(file, show);
            try {
                tdVals[0][3] = String.format("simg%d", show.getId());
                tdVals[1][3] = String.format("sdesc%d", show.getId());
                table.insertRow(
                        0,
                        new String[] {
                                "<div class=\"picSurround\">" + image + "</div>",
                                desc
                        },
                        tdArgs,
                        tdVals
                );
            } catch (Jaxception jaxception) {
                jaxception.printStackTrace();
            }
        }
        clockThread.start();
    }

    private String buildImg(DbShow show)
    {
        return "<a href=\""
                + show.getMalUrl() +
                "\" class=\"hoverinfo_trigger\"><img src=\""
                + show.getImgUrl() +
                "\" border=\"0\"></a>";
    }

    private String buildDesc(DbFile file, DbShow show)
    {
        System.out.println("== " + show.getName());
        return "<a href=\""
                + show.getMalUrl() +
                "\"><strong>"
                + unescapeHtml(show.getName()) +
                "</strong></a>"
                + "<div class=\"spaceit_pad\">" + show.getStatus() + "</div>"
                + "<div class=\"lightLink\">" + file.getUpdateTime() + "</div>"
                + "<div class=\"spaceit_pad\"><a href=\""
                + file.getUrl() + "\">"
                + file.getName() + "</a></div>";
    }

    // Stop the thread on page unloading.

    protected void onUnload()
    {
        update = false;
        clockThread.interrupt();
    }

    // The updater method.

    void updateClock()
    {
        try {
            //ArrayList<String> fileList = new ArrayList<String>();
            //for (DbFile file : dbFiles)
            //{
            //    fileList.add(file.toString());
            //}
            while ( update ) {
                ArrayList<DbFile> newFiles = db.getFileList(lastFileUpdate);
                for (DbFile file : newFiles)
                {
                    dbFiles.add(file);
                    //    fileList.add(file.toString());
                    if (file.getUpdateTime().after(lastFileUpdate))
                        lastFileUpdate = file.getUpdateTime();
                    DbShow show = db.getShow(file);
                    String image = buildImg(show);
                    String desc = buildDesc(file, show);
                    tdVals[0][3] = String.format("simg%d", show.getId());
                    tdVals[1][3] = String.format("sdesc%d", show.getId());
                    table.insertRow(
                            0,
                            new String[] {
                                    "<div class=\"picSurround\">" + image + "</div>",
                                    desc
                            },
                            tdArgs,
                            tdVals
                    );
                }
                Thread.sleep( 1000 );
            }
        } catch (Exception ex) {
            // Exit thread on interrupted exception
        }
    }
}
