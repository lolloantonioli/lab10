package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final String SEP = File.separator;
    private static final String FILE_NAME = "src" + SEP + "main" + SEP + "resources" + SEP + "config.yml";

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }

        int min = 0;
        int max = 0;
        int attempts = 0;
        try (final BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));) {
            final StringTokenizer tokenizer1 = new StringTokenizer(br.readLine(), ":");
            tokenizer1.nextToken();
            min = Integer.parseInt(tokenizer1.nextToken().trim());
            final StringTokenizer tokenizer2 = new StringTokenizer(br.readLine(), ":");
            tokenizer2.nextToken();
            max = Integer.parseInt(tokenizer2.nextToken().trim());
            final StringTokenizer tokenizer3 = new StringTokenizer(br.readLine(), ":");
            tokenizer3.nextToken();
            attempts = Integer.parseInt(tokenizer3.nextToken().trim());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        this.model = new DrawNumberImpl(min, max, attempts);
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl(), new DrawNumberViewImpl(), new PrintStreamView(System.out));
    }

}
