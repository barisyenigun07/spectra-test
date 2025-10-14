package com.spectra.commons;

import com.spectra.commons.model.JobFinished;

public class Main {
    public static void main(String[] args) {
        JobFinished finished = new JobFinished(1L, "DONE");
        System.out.println(finished.status());
        System.out.println(finished.id());
    }
}
