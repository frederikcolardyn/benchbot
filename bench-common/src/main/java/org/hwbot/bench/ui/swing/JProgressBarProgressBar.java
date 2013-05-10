package org.hwbot.bench.ui.swing;

import javax.swing.JProgressBar;

import org.hwbot.bench.ui.ProgressBar;

public class JProgressBarProgressBar implements ProgressBar {

    private final JProgressBar jProgressBar;

    public JProgressBarProgressBar(JProgressBar jProgressBar) {
        this.jProgressBar = jProgressBar;
    }

    public void setValue(int i) {
        System.out.print('.');
        jProgressBar.setValue(i);
    }

    public void setMaxValue(int max) {
        jProgressBar.setMaximum(max);
    }

}
