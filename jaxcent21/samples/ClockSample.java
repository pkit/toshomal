package sample;

import jaxcent.*;
import toshomal.*;

import java.sql.Timestamp;
import java.util.*;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

public class ClockSample extends jaxcent.JaxcentPage {

    HtmlDiv table;

    Thread clockThread = null;
    boolean update = false;

    EmbeddedDBConnector db;
    Timestamp lastShowUpdate;

    HashMap<Integer,DbShow> dbShows;

    public ClockSample() {
        table = new HtmlDiv(this, "myanimelist");
        update = true;
        lastShowUpdate = new Timestamp(0);

        clockThread = new Thread() {
            public void run() {
                updateClock();
            }
        };

        db = new EmbeddedDBConnector();
        dbShows = db.getLatestShows();
        ArrayList<DbShow> lShows = new ArrayList<DbShow>(dbShows.values());
        Collections.sort(lShows, Util.LATEST_LAST);
        try {
            this.setBatchUpdates(true);
            for(DbShow show : lShows)
            {
                setupNewShow(show);
                addToPageTop(show);
            }
        } finally {
            this.setBatchUpdates(false);
        }

        clockThread.start();
    }

    private void setupNewShow(DbShow show) {
        db.fetchEps(show);
        for(DbEps ep : show.getEpsList()) {
            db.fetchFiles(ep);
        }
        show.updateLatestEpisode();
        //db.fetchFiles(show);
        db.fetchTags(show);
    }

    private void addToPageTop(DbShow show)
    {
        try {
            HtmlDiv rowHdr = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "id", "class" },
                    new String[] { String.format("shdr%d", show.getId()), "table_newrow" }
            );
            rowHdr.setInnerText("&nbsp;");
            HtmlDiv row = new HtmlDiv(this, SearchType.createNew,
                    new String[] { "id", "style" },
                    new String[] { String.format("srow%d", show.getId()), "width: 100%;" }
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

            row.insertAtBeginning(table);
            rowHdr.insertAtBeginning(table);

            if (show.getUpdateTime().after(lastShowUpdate))
                lastShowUpdate = show.getUpdateTime();

        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
    }

    private HtmlDiv buildTags(DbShow show)
    {
        ArrayList<DbTag> showTags = show.getTagsList();
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
                    public void onClick()
                    {

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
        ArrayList<DbEps> showEps = show.getEpsList();
        try {
            HtmlDiv result = new HtmlDiv(this, SearchType.createNew, "Episodes: ",
                    new String[] { "id", "class" },
                    new String[] { String.format("seps%d", show.getId()), "spaceit_pad" }
            );
            if (show.getType().equals("Movie"))
            {
                new HtmlAnchor(this, SearchType.createNew, "Movie",
                        new String[]{ "class", "href" },
                        new String[]{ "Lightbox_Small button_form", "javascript:void(0);" }
                ) {
                    public void onClick()
                    {

                        //showEpsFilesOnPage();
                    }
                }.insertAtEnd(result);
                return result;
            }
            DbEps last = null;
            for (DbEps ep : showEps)
            {
                last = ep;
                String [] divVars = new String[]{ "Lightbox_Small button_edit", "javascript:void(0);" };
                if(ep.equals(show.getLatestEps()))
                    divVars[0] = "Lightbox_Small button_form";
                new EpsButton(this, SearchType.createNew, ep.toString(),
                        new String[]{ "class", "href" },
                        divVars,
                        result).insertAtEnd(result);
            }
            if(last != null && last.getNum() < show.getEpsNum())
            {
                new HtmlItalic(this, SearchType.createNew, String.format(" ··· %d", show.getEpsNum()),
                        new String[] { },
                        new String[] { }
                ).insertAtEnd(result);
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
                ArrayList<DbFile> newFiles = db.fetchNewFilesList(lastShowUpdate);
                try {
                    this.setBatchUpdates(true);
                    for (DbFile newFile : newFiles)
                    {
                        DbShow newShow = db.getShow(newFile);
                        setupNewShow(newShow);

                        DbShow show = dbShows.get(newShow.getId());
                        if (show != null)
                        {
                            dbShows.remove(show.getId());
                            removeFromPage(show);
                        }
                        dbShows.put(newShow.getId(), newShow);
                        addToPageTop(newShow);
                    }
                } finally {
                    this.setBatchUpdates(false);
                }
                Thread.sleep( 1000 );
            }
        } catch (Exception ex) {
            // Exit thread on interrupted exception
        }
    }

    private void removeFromPage(DbShow show)
    {
        HtmlDiv rowHdr = new HtmlDiv(this, String.format("shdr%d", show.getId()));
        HtmlDiv row = new HtmlDiv(this, String.format("srow%d", show.getId()));
        try {
            rowHdr.deleteElement();
            row.deleteElement();
        } catch (Jaxception jaxception) {
            jaxception.printStackTrace();
        }
    }

    private void testAnchor(String msg)
    {
        this.showMessageDialog(msg);
    }

    private class EpsButton extends HtmlAnchor {

        private boolean down = false;
        private HtmlDiv epsDiv;
        private HtmlDiv fileList;
        private JaxcentPage tpage;
        private DbEps ep;

        public EpsButton(JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values, HtmlDiv epsDiv) throws Jaxception {
            super(page, searchType, text, attributes, values);
            this.tpage = page;
            this.epsDiv = epsDiv;
        }

        public void onClick()
        {
            if (down)
            {
                try {
                    fileList = new HtmlDiv(tpage,SearchType.createNew,
                            new String[] { "class" }, new String[] { "eps_file_list" });
                    for(HtmlDiv fileDiv : buildFiles(ep))
                    {
                        fileDiv.insertAtEnd(fileList);
                    }
                    fileList.insertAfter(epsDiv);
                } catch (Jaxception jaxception) {
                    jaxception.printStackTrace();
                } finally {
                    down = ! down;
                }
            }
            else
            {
                try {
                    fileList.deleteElement();
                } catch (Jaxception jaxception) {
                    jaxception.printStackTrace();
                } finally {
                    down = ! down;
                }
            }
        }
    }

    private ArrayList<HtmlDiv> buildFiles(DbEps ep)
    {
        ArrayList<HtmlDiv> result = new ArrayList<HtmlDiv>();
        ArrayList<DbFile> sortedFiles = ep.getFiles();
        Collections.sort(sortedFiles, DbFile.LATEST_FIRST);
        for (DbFile f : sortedFiles)
        {
            try {
                HtmlDiv div = new HtmlDiv(this, SearchType.createNew, new String[] {}, new String[] {});
                HtmlAnchor link = new HtmlAnchor(this, SearchType.createNew, f.getName(), new String[] { "href" }, new String[] { "javascript:void(0);" });
                link.insertAtBeginning(div);
                result.add(div);
            } catch (Jaxception jaxception) {
                jaxception.printStackTrace();
            }
        }
        return result;
    }
}
