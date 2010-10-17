package toshomalBackend;

import toshomal.ToshoReader;

import java.io.IOException;
import java.util.Date;

import static toshomal.ToshoReader.getToshoContent;

/**
 * Created by IntelliJ IDEA.
 * User: Kit
 * Date: Oct 12, 2010
 * Time: 3:03:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class BackendInstance extends Thread {

    String logline;
    boolean bRun;
    ToshomalMessage beMsg;
    ToshomalMessage feMsg;
    ToshoReader reader;
    Date lastUpdate;

    public BackendInstance(ToshomalMessage beMessage, ToshomalMessage feMessage)
    {
        this.logline =  "Backend running...";
        this.beMsg = beMessage;
        this.feMsg = feMessage;
        this.reader = new ToshoReader();
        this.lastUpdate = new Date(0);
    }
    public void run() {
        try {
            bRun = true;
            while(bRun)
            {
                /*
                if(beMsg.put("Some Backend data"))
                { System.out.println("Successfully sent to frontend"); }
                else
                { System.out.println("Failed to send to frontend, will try later"); }
                System.out.println(logline);
                sleep(10000);
                String msg = feMsg.take();
                if(msg != null)
                { System.out.println("Got message from frontend: " + msg); }
                else
                { System.out.println("Failed to get message from frontend, will try later"); }
                */
                lastUpdate = getToshoContent(lastUpdate);
                sleep(30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void terminate() throws IOException
    {
        bRun = false;
    }
}
