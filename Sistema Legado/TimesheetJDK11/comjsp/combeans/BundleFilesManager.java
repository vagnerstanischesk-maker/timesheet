package combeans;

import java.util.ResourceBundle;

public class BundleFilesManager
{
    private static ResourceBundle bundle = null;

    private static final synchronized void setBundle(String bundleName)
    {
        String strBundleName = "";

        if ( bundleName == null){
            strBundleName = "default";
        }else{
            strBundleName = bundleName;
        }//end else
        
        bundle = ResourceBundle.getBundle(strBundleName);
        //System.out.println("Boundle getString : "+ bundle.getString("jsp-path"));
    }

    public static synchronized final ResourceBundle getBundle(String bundleName){

        setBundle(bundleName);

        return bundle;
    }

    
}