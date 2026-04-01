package com.bakuard.ecsEngine.entity;

import com.bakuard.ecsEngine.exception.DuplicateEntityNameException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class EntityNamesManager {

	private final EntityManager entityManager;
	private final HashMap<String, Entity> nameToEntity;
	private final HashMap<Entity, String> entityToName;

	public EntityNamesManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.nameToEntity = new HashMap<>();
		this.entityToName = new HashMap<>();
	}

	private EntityNamesManager(EntityNamesManager entityNamesManager, EntityManager entityManager) {
		this.entityManager = entityManager;
		this.nameToEntity = new HashMap<>(entityNamesManager.nameToEntity);
		this.entityToName = new HashMap<>(entityNamesManager.entityToName);
	}

	public EntityNamesManager copyWith(EntityManager entityManager) {
		return new EntityNamesManager(this, entityManager);
	}


	public void assignName(Entity entity, String name) {
		entityManager.assertIsAlive(entity);

		Entity relatedEntity = nameToEntity.get(name);
		if(relatedEntity != null && !relatedEntity.equals(entity))
			throw new DuplicateEntityNameException("Unique tag '" + name + "' already assign to " + relatedEntity);

		removeName(entity);
		nameToEntity.put(name, entity);
		entityToName.put(entity, name);
	}

	public void removeName(String name) {
		entityToName.remove(nameToEntity.remove(name));
	}

	public void removeName(Entity entity) {
		nameToEntity.remove(entityToName.remove(entity));
	}


	public Entity getEntityByName(String name) {
		return nameToEntity.get(name);
	}

	public String getNameByEntity(Entity entity) {
		return entityToName.get(entity);
	}

	public boolean hasName(Entity entity, String name) {
		return entityManager.isAlive(entity) && entity.equals(nameToEntity.get(name));
	}

	public boolean isNameClaimed(String name) {
		return nameToEntity.containsKey(name);
	}


	public Set<String> getAllNames() {
		return new HashSet<>(nameToEntity.keySet());
	}
}
