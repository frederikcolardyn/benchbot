package org.hwbot.bench.api;

import java.util.concurrent.Callable;

import org.hwbot.bench.ui.ProgressBar;

public abstract class Benchmark implements Callable<Long> {

    protected final int threads;
    protected final ProgressBar progressBar;
    protected BenchmarkConfiguration config;
    protected Long score;

    public Benchmark(int threads, ProgressBar progressBar) {
        this.threads = threads;
        this.progressBar = progressBar;
    }

    public Benchmark(BenchmarkConfiguration config, int threads, ProgressBar progressBar) {
        this.config = config;
        this.threads = threads;
        this.progressBar = progressBar;
    }

    public BenchmarkConfiguration getConfig() {
        return config;
    }

    public void setConfig(BenchmarkConfiguration config) {
        this.config = config;
    }

    public void warmup() {
    };

    public abstract Long benchmark(BenchmarkConfiguration configuration);

    public abstract String getClient();

    public Long call() throws Exception {
        System.out.println("Starting benchmark...");
        if (config == null) {
            return null;
        } else {
            this.score = benchmark(config);
            return score;
        }
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

}
