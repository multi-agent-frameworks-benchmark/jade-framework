package com.jade;

import com.jade.benchmark.CallForProposalsBenchmarkPath;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.text.SimpleDateFormat;

public class App {
    public static void main(String[] args) throws Exception {
        String dateRun = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new java.util.Date());

        Options options = new OptionsBuilder()
                .include(CallForProposalsBenchmarkPath.class.getSimpleName())
                .output("../../" + dateRun + "-benchmark-result-contract-net-protocol-jade.txt")
                .resultFormat(ResultFormatType.TEXT)
                .build();

        new Runner(options).run();
    }
}
