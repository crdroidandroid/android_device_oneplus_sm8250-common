package android.common;

public class OplusFrameworkFactory {

    private static OplusFrameworkFactory sOplusFrameworkFactory = null;

    public static OplusFrameworkFactory getInstance() {
        if (sOplusFrameworkFactory == null) {
            sOplusFrameworkFactory = new OplusFrameworkFactory();
        }
        return sOplusFrameworkFactory;
    }
}
