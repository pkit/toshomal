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
        for(DbShow show : dbShows)
            createShow(show);
        clockThread.start();
    }

    private void createShow(DbShow show)
    {
        String simgId = String.format("simg%d", show.getId());
        String sdescId = String.format("sdesc%d", show.getId());
        String stagId = String.format("stag%d", show.getId());

        try {
            tdVals[0][0] = simgId;
            tdVals[1][0] = sdescId;
            tdVals[2][0] = stagId;
            HtmlTableRow row = table.insertRow(0,
                    new String[] {
                            buildImg(show).getInnerHTML(),
                            buildDesc(file, show).getInnerHTML(),
                            buildTags(show).getInnerHTML()
                    },
                    tdArgs,
                    tdVals
            );
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
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

                            buildDesc(show),
                            buildTags(show)
                    },
                    tdArgs,
                    tdVals
            );
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
    }

    private HtmlDiv buildTags(DbShow show)
    {
        ArrayList<DbTag> showTags = show.getTags();
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew, new String[] { "class" }, new String[] { "spaceit"});
            for (DbTag tag : showTags)
            {
                result.insertAtEnd(
                        new HtmlAnchor(this, SearchType.createNew, tag.getName(),
                                new String[] { "class", "href", "style" },
                                new String[] { "button_add", "javascript:void(0);", show.getFontSize(tag) }
                        )
                        {
                            public void onClick()
                            {

                            }
                        }
                );
            }
            return result;
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
        return null;
    }

    private HtmlDiv buildImg(DbShow show)
    {
        try {
            HtmlAnchor anchor = new HtmlAnchor(this, SearchType.createNew,
                    new String[] { "class", "href" },
                    new String[] { "hoverinfo_trigger", show.getMalUrl() }
            );
            anchor.insertAtBeginning(
                    new HtmlImage(this, SearchType.createNew,
                            new String[] { "src", "border" },
                            new String[] { show.getImgUrl(), "0" }
                    )
            );
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "class" }, new String[] { "picSurround" }
            );
            result.insertAtBeginning(anchor);
            return result;
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
        return null;
    }

    private HtmlDiv buildDesc(DbFile file, DbShow show)
    {
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew, new String[] {}, new String[] {});
            HtmlAnchor anchor = new HtmlAnchor(this, SearchType.createNew,
                    new String[] { "href" },
                    new String[] { show.getMalUrl() }
            );
            anchor.insertAtBeginning(new HtmlBold(this, SearchType.createNew, unescapeHtml(show.getName())));
            result.insertAtEnd(anchor);
            result.insertAtEnd(
                    new HtmlDiv(this, SearchType.createNew, show.getStatus(),
                            new String[] { "class"}, new String[] { "spaceit_pad"}
                    )
            );
            result.insertAtEnd(
                    new HtmlDiv(this, SearchType.createNew, "Updated: " + file.getUpdateTime(),
                            new String[] { "class"}, new String[] { "lightLink"}
                    )
            );
            result.insertAtEnd(buildEps(show));
            return result;
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
        return null;
    }

    private HtmlDiv buildEps(DbShow show)
    {
        ArrayList<DbEps> showEps = show.getEps();
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew, "Episodes: ", new String[] { "class" }, new String[] { "spaceit_pad"});
            for (DbEps ep : showEps)
            {
                result.insertAtEnd(
                        new HtmlAnchor(this, SearchType.createNew, ep.toString(),
                                new String[] { "class", "href" },
                                new String[] { "Lightbox_Small button_edit", "javascript:void(0);" }
                        )
                        {
                            public void onClick()
                            {

                            }
                        }
                );
            }
            return result;
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
        return null;
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
