package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.system.ExecutionContext;

public interface UncaughtExceptionHandler {

	public void handle(ExecutionContext context, Throwable exception);

}
