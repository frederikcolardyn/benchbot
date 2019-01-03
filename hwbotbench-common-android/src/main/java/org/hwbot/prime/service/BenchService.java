package org.hwbot.prime.service;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import org.hwbot.bench.Benchmark;
import org.hwbot.bench.prime.ProgressBar;
import org.hwbot.bench.security.EncryptionModule;
import org.hwbot.prime.ui.Output;
import org.hwbot.prime.ui.android.AndroidProgressBar;
import org.hwbot.prime.ui.android.BenchConsole;
import org.hwbot.prime.ui.android.ViewConsole;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public abstract class BenchService implements Runnable {

    public static String HWBOT_APP_CLIENT_DEV_VERSION = "dev";
    protected int availableProcessors;

    protected BenchmarkStatusAware benchUI;
    protected ProgressBar progressBar;
    protected Output output;
    protected Button compareButton;

    protected byte[] key;
    protected byte[] iv;
    protected Float processorSpeed;

    protected Integer stageId;
    protected Number score;
    protected static AndroidHardwareService hardwareService;
    protected static SecurityService securityService;
    protected static DataServiceXml dataServiceXml;

//     public static String SERVER = "http://127.0.0.1:9090";
//    public static String SERVER = "https://uat.hwbot.org";
    public static String SERVER = "https://hwbot.org";

    public void initialize(TextView console, android.widget.ProgressBar progressbar, BenchmarkStatusAware benchUI) throws IOException {
        // Hardware gatherHardwareInfo = hardwareService.gatherHardwareInfo();
        // Log.i("hw info", gatherHardwareInfo.toString());
        // processor = hardwareService.getProcessorInfo();
        // deviceName = hardwareService.getDeviceName();
        // socName = hardwareService.getSocName();
        // deviceVendor = hardwareService.getDeviceVendor();
        availableProcessors = Runtime.getRuntime().availableProcessors();

        if (benchUI == null) {
            benchUI = new BenchConsole(this);
        }

        output = new ViewConsole(console);
        if (progressbar != null) {
            this.progressBar = new AndroidProgressBar(progressbar, 100);
        } else {
            this.progressBar = null;
        }
        this.benchUI = benchUI;

        // output.write("Processor detected:\n" + processor);
        // output.write("Temperature:\n" + hardwareService.getProcessorTemperature() + "C");
        // output.write("Estimated freq: " + hardwareService.getProcessorSpeed() + "MHz ", false);
        // processorSpeed = hardwareService.getProcessorSpeed();
        // output.write((new JavaSysMon().numCpus() + "x ") + getProcessorFrequency() + " Ghz");
    }

    public String getProcessorFrequency() {
        String freq;
        if (processorSpeed == null) {
            freq = "n/a";
        } else {
            NumberFormat instance = NumberFormat.getInstance(Locale.ENGLISH);
            instance.setMaximumFractionDigits(2);
            freq = instance.format(processorSpeed);
        }
        return freq;
    }

    public enum BenchPhase {
        ready, warmup, inprogress, finished
    }

    private ExecutorService exec;
    private BenchPhase currentPhase = BenchPhase.ready;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> processorFrequencyMonitorScheduler;
    private Benchmark benchmark;

    public void benchmark() {
        exec = Executors.newFixedThreadPool(2, new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.setName("benchmark");
                thread.setDaemon(false);
                return thread;
            }
        });
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdownNow();
            processorFrequencyMonitorScheduler.cancel(true);
        }
        benchmark = instantiateBenchmark();
        new Thread(this).start();
    }

    public void run() {
        Future<Number> submit = exec.submit(benchmark);
        this.currentPhase = BenchPhase.inprogress;
        // wait for outout
        try {
            score = submit.get();
            securityService.updateChecksum(score);
            this.currentPhase = BenchPhase.finished;
            // Log.i(this.getClass().getSimpleName(), "Notifying benchmark has finished: " + benchUI);
            benchUI.notifyBenchmarkFinished(score);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void benchmarkold() {
        // Log.i(this.getClass().getName(), "Benchmarking...");
        ExecutorService exec = Executors.newFixedThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("benchmark");
                thread.setDaemon(false);
                thread.setPriority(Thread.MAX_PRIORITY);
                return thread;
            }
        });
        Future<Number> submit = exec.submit(instantiateBenchmark());
        //
        // while(!submit.isDone()){
        // try {
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        //
        // try {
        // Long score = submit.get();
        // benchUI.notifyBenchmarkFinished(score);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // } catch (ExecutionException e) {
        // e.printStackTrace();
        // }
    }
    
    public abstract Benchmark instantiateBenchmark();

    public byte[] getDataFile(Context ctx) {
        byte[] bytes = null;
        // processor speed ignored, not reliable enough...
        // verifyMemoryUnaltered();
        // String xml = createXml(version, processor, processorSpeed, score, deviceName, socName, deviceVendor);
        EncryptionModule encryptionModule = securityService.getEncryptionModule();
        String xml = DataServiceXml.createXml(getVersion(), score, stageId, hardwareService.getDeviceInfo(), securityService.getCredentials(), encryptionModule);
        bytes = securityService.encrypt(xml, ctx);
        return bytes;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public String formatScore(Number score) {
        return String.format(Locale.ENGLISH, "%.2f", score);
    }

    public Number getScore() {
        return score;
    }

    public void setScore(Number score) {
        this.score = score;
    }

    public abstract String getVersion();

    public Integer getStageId() {
        return stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }
}
