package pl.pjask.stocknews;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import pl.pjask.stocknews.models.Stock;
import pl.pjask.stocknews.utils.Menu;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = LOLLIPOP, manifest = "/src/main/AndroidManifest.xml")
public class MenuTest {
    Menu menu;
    Context context;

    @Before
    public void setup() {
        context = RuntimeEnvironment.application;
        menu = Menu.getInstance(context);
    }

    @Test
    public void testSymbolsInsertion() {
        String symbolName1 = "KGHM";
        String symbolName2 = "PGE";
        Stock stock1 = new Stock(symbolName1);
        Stock stock2 = new Stock(symbolName2);

        menu.addSymbol(stock1);
        menu.addSymbol(stock2);

        assertTrue("symbols added and received from menu are different",
                menu.getSymbolNames().contains(symbolName1) &&
                        menu.getSymbolNames().contains(symbolName2) &&
                        menu.getSymbolNames().size() == 2);
    }

    @Test
    public void testRemoveSymbol() {
        String symbolName1 = "KGHM";
        String symbolName2 = "PGE";
        Stock stock1 = new Stock(symbolName1);
        Stock stock2 = new Stock(symbolName2);
        menu.addSymbol(stock1);
        menu.addSymbol(stock2);

        menu.removeSymbol(symbolName1);

        assertTrue("symbol is not removed from menu",
                menu.getSymbolNames().size() == 1 &&
                        menu.getSymbolNames().contains(symbolName2));
    }

}