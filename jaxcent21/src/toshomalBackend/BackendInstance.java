package toshomalBackend;

import toshomal.ToshoReader;

import java.io.IOException;
import java.util.Date;

import static toshomal.ToshoReader.getToshoContent;

public class BackendInstance extends Thread {

    String logline;
    boolean bRun;
    ToshomalMessage beMsg;
    ToshomalMessage feMsg;
    ToshoReader reader;
    long sleepTime;
    //Date lastUpdate;

    public BackendInstance()
    {
        this.logline =  "Backend running...";
        this.reader = new ToshoReader();
        this.sleepTime = 60 * 1000;
        //this.lastUpdate = new Date(0);
    }
    public void run() {
        try {
            bRun = true;
            long sleep = sleepTime;
            long maxSleep = sleepTime << 6;
            while(bRun)
            {
                if(getToshoContent())
                    sleep = sleepTime;
                System.out.println(String.format("Sleeping %d seconds...", sleep / 1000));
                sleep(sleep);
                if (sleep < maxSleep)
                    sleep <<= 1;
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
