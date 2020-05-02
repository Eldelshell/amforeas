package amforeas.demo;

import amforeas.AmforeasJetty;
import amforeas.SingletonFactory;
import amforeas.config.AmforeasConfiguration;

public class DemoJetty extends AmforeasJetty {

    public static void main (String[] args) throws Exception {
        System.out.println("*************************************************");
        System.out.println("*                                               *");
        System.out.println("*            Starting Amforeas Demo             *");
        System.out.println("*                                               *");
        System.out.println("*************************************************");

        SingletonFactory factory = new DemoSingletonFactory();
        final AmforeasConfiguration conf = factory.getConfiguration();

        DemoJetty me = new DemoJetty();
        me.startServer(conf);

    }
}
