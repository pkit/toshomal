package sample;

import jaxcent.*;
import toshomal.*;
import toshomalBackend.ToshomalMessage;

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
    ArrayList<DbShow> dbShows;

    static String[][] tdArgs = {
            { "id", "class", "valign", "width" },
            { "id", "class", "valign", "style" },
            { "id", "class", "valign", "style" }
    };
    String[][] tdVals = {
            { "", "borderClass", "top", "53"  },
            { "", "borderClass", "top", "padding-left: 0pt;" },
            { "", "borderClass", "top", "padding-right: 0pt;" }
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
        dbFiles = new ArrayList<DbFile>();
        dbShows = db.getLatestShows();
        //dbFiles = db.getFileList();
        for(DbShow show : dbShows)
            createShow(show);
        clockThread.start();
    }

    private void injectFile(DbFile file)
    {
        if (file.getUpdateTime().after(lastFileUpdate))
            lastFileUpdate = file.getUpdateTime();
        DbShow show = db.getShow(file);
        String simgId = String.format("simg%d", show.getId());
        String sdescId = String.format("sdesc%d", show.getId());
        String stagId = String.format("stag%d", show.getId());

        HtmlTableCell imgCell = new HtmlTableCell(this, simgId);
        HtmlTableCell descCell = new HtmlTableCell(this, sdescId);
        HtmlTableCell tagCell = new HtmlTableCell(this, stagId);
        try {
            if (imgCell.checkNodeExists(RelationType.parentNode)
                    && descCell.checkNodeExists(RelationType.parentNode)
                    && tagCell.checkNodeExists(RelationType.parentNode))
            {
                descCell.setInnerHTML(buildDesc(file, show));
                tagCell.setInnerHTML(buildTags(file, show));
                return;
            }
        } catch (Jaxception jaxception) {}

        try {
            tdVals[0][0] = simgId;
            tdVals[1][0] = sdescId;
            tdVals[2][0] = stagId;
            table.insertRow(
                    0,
                    new String[] {
                            "<div class=\"picSurround\">"
                                    + buildImg(show) +
                                    "</div>",
                            buildDesc(file, show),
                            buildTags(file, show)
                    },
                    tdArgs,
                    tdVals
            );
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
    }

    private String buildTags(DbFile file, DbShow show)
    {
        ArrayList<DbTag> showTags = db.getShowTags(show);
        String result = "<div class=\"spaceit\">";
        for (DbTag tag : showTags)
        {
            String btn = "button_add";
            if (db.isFileTag(file, tag))
            {
                //btn = "button_add";
                file.addTag(tag);
            }
            result += "<a class=\"" + btn + "\" href=\""
                    + getTagUrl(tag)
                    + "\" style=\"font-size:"
                    + "1.0" + "em\">"
                    + tag.getName()
                    + "</a>";
        }
        return result + "</div>";
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
        //System.out.println("== " + show.getName());
        return "<a href=\""
                + show.getMalUrl()
                + "\"><strong>"
                + unescapeHtml(show.getName())
                + "</strong></a>"
                + "<div class=\"spaceit_pad\">" + show.getStatus() + "</div>"
                + "<div class=\"lightLink\">Updated: " + file.getUpdateTime() + "</div>"
                + "<div class=\"spaceit_pad\">Episodes: "
                + buildEps(file, show)
                + "</div>";
    }

    private String buildEps(DbFile file, DbShow show)
    {
        ArrayList<DbEps> showEps = db.getShowEps(show);
        String result = "";
        for (DbEps eps : showEps)
        {
            String btn = "button_edit";
            if (db.isFileEps(file, eps))
            {
                btn = "button_form";
                eps.addFile(file);
            }
            result += "<a class=\"Lightbox_Small " + btn
                    + "\" href=\""
                    + getEpsUrl(eps)
                    + "\">"
                    + eps.toString()
                    + "</a>";
        }
        return result;
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
                    injectFile(file);
                }
                Thread.sleep( 1000 );
            }
        } catch (Exception ex) {
            // Exit thread on interrupted exception
        }
    }
}
