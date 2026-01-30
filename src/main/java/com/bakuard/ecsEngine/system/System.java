package com.bakuard.ecsEngine.system;

@FunctionalInterface
public interface System {

	public void update(SystemMeta systemMeta, ExecutionContext context);

}
