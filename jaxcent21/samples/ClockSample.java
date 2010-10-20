package sample;

import jaxcent.*;
import toshomal.*;
import toshomalBackend.ToshomalMessage;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

public class ClockSample extends jaxcent.JaxcentPage {

    HtmlDiv table;

    Thread clockThread = null;
    boolean update = false;

    EmbeddedDBConnector db;
    Timestamp lastFileUpdate;

    ArrayList<DbShow> dbShows;

    public ClockSample() {
        table = new HtmlDiv(this, "myanimelist");
        System.out.println("table: " + table.toString());
        update = true;
        lastFileUpdate = new Timestamp(0);

        clockThread = new Thread() {
            public void run() {
                updateClock();
            }
        };

        db = new EmbeddedDBConnector();
        dbShows = db.getLatestShows();
        for(DbShow show : dbShows)
            createShow(show);
        
        clockThread.start();
    }

    private void createShow(DbShow show)
    {
        try {
            HtmlDiv rowHdr = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "class" },
                    new String[] { "table_newrow" }
            );
            rowHdr.setInnerText("&nbsp;");
            HtmlDiv row = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "style" },
                    new String[] { "width: 100%;" }
            );
            HtmlDiv cellImg = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "id", "class" },
                    new String[] { String.format("simg%d", show.getId()), "cell_img" }
            );
            HtmlDiv cellDesc = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "id", "class" },
                    new String[] { String.format("simg%d", show.getId()), "cell_desc" }
            );
            HtmlDiv cellTags = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "class" },
                    new String[] { "cell_tags" }
            );
            HtmlDiv cellBorder = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "class", "style" },
                    new String[] { "borderClass", "clear: both;" }
            );

            buildImg(show).insertAtEnd(cellImg);
            HtmlDiv desc = buildDesc(show);
            buildEps(show).insertAtEnd(desc);
            desc.insertAtEnd(cellDesc);
            buildTags(show).insertAtEnd(cellTags);

            cellImg.insertAtEnd(row);
            cellDesc.insertAtEnd(row);
            cellTags.insertAtEnd(row);
            cellBorder.insertAtEnd(row);

            rowHdr.insertAtEnd(table);
            row.insertAtEnd(table);

            if (show.getUpdateTime().after(lastFileUpdate))
                lastFileUpdate = show.getUpdateTime();

        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
    }

    private HtmlDiv buildTags(DbShow show)
    {
        ArrayList<DbTag> showTags = show.getTags();
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "id", "class" },
                    new String[] { String.format("stag%d", show.getId()), "spaceit" }
            );
            for (DbTag tag : showTags)
            {
                new HtmlAnchor(
                        this, SearchType.createNew, tag.getName(),
                        new String[]{"class", "href", "style"},
                        new String[]{"button_add", "javascript:void(0);", show.getFontSize(tag)}
                ) {
                    public void onClick() {

                    }
                }.insertAtEnd(result);
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
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "class" }, new String[] { "picSurround" }
            );
            HtmlAnchor anchor = new HtmlAnchor(this, SearchType.createNew,
                    new String[] { "class", "href" },
                    new String[] { "hoverinfo_trigger", show.getMalUrl() }
            );
            new HtmlImage(this, SearchType.createNew,
                    new String[]{"src", "border"},
                    new String[]{show.getImgUrl(), "0"}
            ).insertAtBeginning(anchor);
            anchor.insertAtBeginning(result);
            return result;
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
        return null;
    }

    private HtmlDiv buildDesc(DbShow show)
    {
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew,
                    new String[] {}, new String[] {}
            );
            HtmlAnchor anchor = new HtmlAnchor(this, SearchType.createNew,
                    new String[] { "href" },
                    new String[] { show.getMalUrl() }
            );
            new HtmlBold(this, SearchType.createNew,
                    unescapeHtml(show.getName())
            ).insertAtBeginning(anchor);

            anchor.insertAtEnd(result);

            new HtmlDiv(this, SearchType.createNew, show.getStatus(),
                    new String[]{"class"}, new String[]{"spaceit_pad"}
            ).insertAtEnd(result);

            new HtmlDiv(this, SearchType.createNew, "Updated: " + show.getUpdateTime(),
                    new String[]{ "class" }, new String[]{ "lightLink" }
            ).insertAtEnd(result);

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
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew, "Episodes: ",
                    new String[] { "id", "class" },
                    new String[] { String.format("seps%d", show.getId()), "spaceit_pad" }
            );
            for (DbEps ep : showEps)
            {
                new HtmlAnchor(this, SearchType.createNew, ep.toString(),
                        new String[]{ "class", "href" },
                        new String[]{ "Lightbox_Small button_edit", "javascript:void(0);" }
                ) {
                    public void onClick() {

                    }
                }.insertAtEnd(result);
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
/*                ArrayList<DbFile> newFiles = db.getFileList(lastFileUpdate);
                for (DbFile file : newFiles)
                {
                    dbFiles.add(file);
                    injectFile(file);
                }*/
                Thread.sleep( 1000 );
            }
        } catch (Exception ex) {
            // Exit thread on interrupted exception
        }
    }
}
