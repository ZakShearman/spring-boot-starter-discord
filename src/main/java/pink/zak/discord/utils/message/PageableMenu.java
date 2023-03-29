package pink.zak.discord.utils.message;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class PageableMenu {
    protected final AtomicInteger currentPage;
    protected int maxPage = Integer.MAX_VALUE;

    protected PageableMenu(int startPage) {
        this.currentPage = new AtomicInteger(startPage);
    }

    protected PageableMenu() {
        this.currentPage = new AtomicInteger(1);
    }

    public abstract void drawPage(int page);

    public int previousPage() {
        int initialPage = this.currentPage.intValue();
        if (initialPage <= 1)
            return initialPage;

        this.drawPage(this.currentPage.decrementAndGet());
        return initialPage - 1;
    }

    public int nextPage() {
        int initialPage = this.currentPage.intValue();
        if (initialPage >= this.maxPage)
            return initialPage;

        this.drawPage(this.currentPage.incrementAndGet());
        return initialPage + 1;
    }
}
