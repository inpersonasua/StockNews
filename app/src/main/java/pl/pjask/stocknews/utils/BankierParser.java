package pl.pjask.stocknews.utils;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.pjask.stocknews.models.ArticleModel;
import pl.pjask.stocknews.models.Stock;


/**
 * Parser for bankier.pl website.
 */
public class BankierParser {
    private static final String BANKIER_SYMBOLS_URL = "http://www.bankier.pl/gielda/notowania/akcje";
    private static final String SYMBOL_ROW_CLASS = ".colWalor";

    private static final String BANKIER_NEWS_URL_TEMPLATE = "http://www.bankier.pl/inwestowanie/profile/quote.html?symbol=";
    private static final String NEWS_CLASS = "boxContent boxList";

    /**
     * Returns header of articles for given stock.
     *
     * @param stock stock name for example: KGHM, PGE, PKOBP...
     * @return List of {@link ArticleModel}
     * @throws IOException when website can't parsed.
     */
    public List<ArticleModel> getArticles(Stock stock) throws IOException {
        int newsBoxNumber = 0;
        int espiBoxNumber = 1;
        List<ArticleModel> articleModels = new ArrayList<>();

        if (stock.fetchNews())
            articleModels.addAll(getModels(stock, newsBoxNumber));

        if (stock.fetchEspi())
            articleModels.addAll(getModels(stock, espiBoxNumber));

        return articleModels;
    }

    @NonNull
    private List<ArticleModel> getModels(Stock symbol, int boxNumber) throws IOException {
        List<ArticleModel> articleModels = new ArrayList<>();

        Document document = Jsoup.connect(BANKIER_NEWS_URL_TEMPLATE + symbol.getStockSymbol()).get();
        Elements boxes = document.getElementsByClass(NEWS_CLASS);
        Element articleElement = boxes.get(boxNumber);
        Elements newsElements = articleElement.getElementsByTag("li");
        Elements urlElements = articleElement.getElementsByTag("a");
        Elements dateElements = articleElement.getElementsByTag("time");

        int titleBeginIndex = 11;
        ArticleModel model;
        for (int i = 0; i < newsElements.size(); i++) {
            model = new ArticleModel(newsElements.get(i).text().substring(titleBeginIndex));
            model.setStockSymbol(symbol.getStockSymbol());
            model.setUrl("http://www.bankier.pl" + urlElements.get(i).attr("href"));
            model.setDate(dateElements.get(i).attr("datetime"));
            articleModels.add(model);
        }
        return articleModels;
    }

    /**
     * Returns all symbols as Set from bankier website.
     *
     * @return set of symbols.
     * @throws IOException when website can't be parsed.
     */
    public Set<String> getSymbols() throws IOException {
        Document document = Jsoup.connect(BANKIER_SYMBOLS_URL).get();

        Elements rows = document.select("tbody").select(SYMBOL_ROW_CLASS);

        Set<String> symbols = new TreeSet<>();
        for (Element row : rows) {
            symbols.add(StringUtils.strip(row.getElementsByTag("a").text(), " "));
        }

        return symbols;
    }

    /**
     * Get map of Stocks where key are stock symbols (String) and values are Stocks.
     *
     * @return map of Stock.
     * @throws IOException when error occurred.
     */
    public Map<String, Stock> getStockMap() throws IOException {
        Document document = Jsoup.connect(BANKIER_SYMBOLS_URL).get();

        Elements rows = document.select("tbody").select(SYMBOL_ROW_CLASS);

        Map<String, Stock> symbols = new HashMap<>();
        for (Element row : rows) {
            String symbol = StringUtils.strip(row.getElementsByTag("a").text(), " ");
            Stock stock = new Stock(symbol);
            stock.setStockFullName(row.getElementsByTag("a").attr("title"));

            symbols.put(symbol, stock);
        }

        return symbols;
    }
}
