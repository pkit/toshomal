package sample;

import jaxcent.HtmlDiv;
import jaxcent.Jaxception;
import jaxcent.SearchType;
import toshomal.DbShow;
import toshomal.EmbeddedDBConnector;

import java.util.ArrayList;
import java.util.HashMap;

public class TestSample extends jaxcent.JaxcentPage
{
    Thread testThread = null;
    boolean update = false;

    public TestSample()
    {
        update = true;
        testThread = new Thread() {
            public void run() {
                update();
            }
        };
        try {
            EmbeddedDBConnector db = new EmbeddedDBConnector();
            HashMap<Integer,DbShow> hShows = db.getLatestShows();
            ArrayList<DbShow> list = new ArrayList<DbShow> (hShows.values());
            ShowEditorDiv eDiv = null;
            try {
                this.setBatchUpdates(true);
                eDiv = new ShowEditorDiv(this, SearchType.createNew, "", new String[] {}, new String[] {}, list.get(0));
                HtmlDiv main = new HtmlDiv(this, "main");
                eDiv.insertAtEnd(main);
            } catch (Jaxception jaxception) {
                jaxception.printStackTrace();
            } finally {
                this.setBatchUpdates(false);
            }
            if (eDiv != null) {
                eDiv.addHandlers();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        testThread.start();
    }

    private void update()
    {
        while(update)
        {
            try {
                Thread.sleep(1000);
            } catch (Exception e)
            {  }
        }
    }

    protected void onUnload()
    {
        update = false;
        testThread.interrupt();
    }
}
