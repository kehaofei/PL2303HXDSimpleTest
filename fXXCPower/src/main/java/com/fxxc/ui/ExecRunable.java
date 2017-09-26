package com.fxxc.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecRunable {
	private static ExecutorService service = Executors.newFixedThreadPool(5);

	public static void execRun(Runnable runnable) {
		service.execute(runnable);
	}
}
