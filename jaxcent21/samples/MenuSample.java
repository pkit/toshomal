package sample;

public class MenuSample extends jaxcent.JaxcentPage {

    protected void onJavaScriptRequest(java.lang.String cmd, java.lang.String[] args)
    {
        showMessageDialog( "Server received cmd: " + cmd );
    }
}
