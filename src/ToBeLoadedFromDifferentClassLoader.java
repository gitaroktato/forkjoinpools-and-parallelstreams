import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ForkJoinPool;
/**
 * Created by Oresztesz_Margaritis on 12/15/2016.
 */
public class ToBeLoadedFromDifferentClassLoader {

    public void run() throws Exception {
        URLClassLoader currentClassLoader = (URLClassLoader) getClass().getClassLoader();
        System.out.println("Our current class-loader is " + currentClassLoader.toString());
        // NULL, means we're in boot class loader.
        System.out.println("Common pool's class-loader: " + ForkJoinPool.commonPool().getClass().getClassLoader() + ". This means boot class loader");
        // Same for this fellow.
        System.out.println("ForkJoinPool's class-loader: " + ForkJoinPool.class.getClassLoader() + ". This means boot class loader");
    }

}
