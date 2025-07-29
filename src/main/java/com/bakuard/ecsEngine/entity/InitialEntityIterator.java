package com.bakuard.ecsEngine.entity;

public interface InitialEntityIterator {

	public boolean next();

	public Entity getEntity();

	public boolean isEntityAlive();

}
